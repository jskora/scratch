package jfskora;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.provenance.ProvenanceEventBuilder;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.ProvenanceEventType;
import org.apache.nifi.provenance.StandardProvenanceEventRecord;
import org.apache.nifi.provenance.serialization.RecordReader;
import org.apache.nifi.provenance.serialization.RecordReaders;
import org.apache.nifi.provenance.serialization.RecordWriter;
import org.apache.nifi.provenance.serialization.RecordWriters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@SuppressWarnings("Duplicates")
public class BenchMarkMergeJournalLogic {

    @SuppressWarnings("unused")
    static Logger logger = LoggerFactory.getLogger(BenchMarkMergeJournalLogic.class);

    private static final int DEFAULT_BASE_EVENT_ID = 100000;
    private static final int DEFAULT_NUM_EVENTS = 10000;

    private static final int DEFAULT_NUM_JOURNALS = 10;
    private static final int DEFAULT_NUM_TESTS = 4;

    private static final int DEFAULT_NUM_ITERATIONS = 5;
    private static final int DEFAULT_NUM_RUNS = 1;

    private File[] journalFiles;
    private File[][] mergeFiles;

    private static String TEMP_FOLDER = System.getProperty("java.io.tmpdir");

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {

        Options options = new Options();

        Option optEvents = new Option("e", "events", true, "number of provenance events to generate");
        optEvents.setRequired(false);
        options.addOption(optEvents);

        Option optFiles = new Option("f", "files", true, "list folder and files");
        optFiles.setRequired(false);
        options.addOption(optFiles);

        Option optGC = new Option("g", "gc", true, "attempt garbage collection");
        optEvents.setRequired(false);
        options.addOption(optGC);

        Option optIterations = new Option("i", "iterations", true, "number of iterations within a run");
        optIterations.setRequired(false);
        options.addOption(optIterations);

        Option optJournals = new Option("j", "journals", true, "number of journal files");
        optJournals.setRequired(false);
        options.addOption(optJournals);

        Option optRuns = new Option("r", "runs", true, "number of test runs");
        optRuns.setRequired(false);
        options.addOption(optRuns);

        Option optTestIndex = new Option("t", "test", true, "index of single test else batch is run (0=original, 1=iterator.firstKey, 2=array of tuples, 3=arrays only)");
        optTestIndex.setRequired(false);
        options.addOption(optTestIndex);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length - 1];
            formatter.printHelp(main.getClassName(), options);
            System.exit(1);
            return;
        }
        int pJournals = (cmd.hasOption("journals") ? Integer.parseInt(cmd.getOptionValue("journals")) : DEFAULT_NUM_JOURNALS);
        int pIterations = (cmd.hasOption("iterations") ? Integer.parseInt(cmd.getOptionValue("iterations")) : DEFAULT_NUM_ITERATIONS);
        int pRuns = (cmd.hasOption("runs") ? Integer.parseInt(cmd.getOptionValue("runs")) : DEFAULT_NUM_RUNS);
        int pEvents = (cmd.hasOption("events") ? Integer.parseInt(cmd.getOptionValue("events")) : DEFAULT_NUM_EVENTS);
        boolean pGC = (cmd.hasOption("gc") && Boolean.parseBoolean(cmd.getOptionValue("gc")));
        boolean pFiles = (cmd.hasOption("files") && Boolean.parseBoolean(cmd.getOptionValue("files")));

        BenchMarkMergeJournalLogic tester = new BenchMarkMergeJournalLogic(pJournals, DEFAULT_NUM_TESTS,
                pRuns, pIterations, DEFAULT_BASE_EVENT_ID, pEvents, pGC, pFiles);
        if (cmd.hasOption("test")) {
            int pTestIndex = Integer.parseInt(cmd.getOptionValue("test"));
            tester.single(pTestIndex);
        } else {
            tester.batch();
        }
    }

    private int numJournals;
    private int numTests;
    private int numIterations;
    private int numRuns;
    private int baseEventId;
    private int numEvents;
    private boolean attemptGC;
    private boolean showFiles;
    private Runtime runtime;

    private BenchMarkMergeJournalLogic(int numJournals, int numTests, int numRuns, int numIterations,
                                       int baseEventId, int numEvents, boolean attemptGC,
                                       boolean showFiles) throws IOException {
        this.numJournals = numJournals;
        this.numTests = numTests;
        this.numRuns = numRuns;
        this.numIterations = numIterations;
        this.baseEventId = baseEventId;
        this.numEvents = numEvents;
        this.attemptGC = attemptGC;
        this.showFiles = showFiles;

        this.journalFiles = new File[this.numJournals];
        this.mergeFiles = new File[this.numTests][this.numRuns];

        this.runtime = Runtime.getRuntime();

        makePaths(TEMP_FOLDER);
    }

    private void single(int testIndex) throws IOException, NoSuchAlgorithmException, InterruptedException {
        long grandTotal = 0;
        long memoryTotal = 0;
        for (int run = 0; run < numRuns; run++) {
            long total = 0;
            writeJournals();

            List<Long> result = Arrays.asList(0L, 0L, 0L);
            if (this.attemptGC) {
                runtime.gc();
                Thread.sleep(100L);
            }
            long m0 = runtime.totalMemory() - runtime.freeMemory();
            for (int iteration = 0; iteration < numIterations; iteration++) {
                result = run(testIndex, mergeFiles[testIndex][run]);
                total += result.get(0);
                grandTotal += result.get(0);
            }
            long m1 = runtime.totalMemory() - runtime.freeMemory();
            memoryTotal += m1 - m0;
            System.out.printf("merge%1d iterations=%4d total=%12dms avg=%12dms memory=%12d/%12d maxId=%12d records=%9d md5=%s\n",
                    testIndex, numIterations, total / 1000000, (total / numIterations) / 1000000, m1 - m0, m1,
                    result.get(1), result.get(2), md5File(mergeFiles[testIndex][0]));

        }
        System.out.printf("merge%d iterations=%4d total=%12dms avg=%12dms memory=%12d\n", testIndex, numRuns * numIterations,
                grandTotal / 1000000, grandTotal / (numRuns * numIterations) / 1000000, memoryTotal / numRuns);
        System.out.printf("  journal.size=" + journalFiles[testIndex].length()
                + " (" + journalFiles[testIndex].getPath() + ")" + " * " + numJournals + "\n");
        System.out.printf("   merged.size=" + mergeFiles[testIndex][0].length()
                + " (" + mergeFiles[testIndex][0].getPath() + ")\n");
    }

    private void batch() throws IOException, NoSuchAlgorithmException, InterruptedException {
        writeJournals();
        for (int run = 0; run < numRuns; run++) {
            long[] totals = new long[numTests];
            long[] totalsFinal = new long[numTests];
            System.out.println("\nrun=" + run);
            writeJournals();
            for (int testIndex = 0; testIndex < numTests; testIndex++) {
                List<Long> result = Arrays.asList(0L, 0L, 0L);
                runtime.gc();
                long m0 = runtime.totalMemory() - runtime.freeMemory();
                for (int iteration = 0; iteration < numIterations; iteration++) {
                    result = run(testIndex, mergeFiles[testIndex][run]);
                    totals[testIndex] += result.get(0);
                    totalsFinal[testIndex] = result.get(0);
                }
                long m1 = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("merge" + testIndex + " runs=" + numRuns + " total=" + totals[testIndex]
                        + " avg=" + (totals[testIndex] / numIterations)
                        + " maxId=" + result.get(1) + " records=" + result.get(2)
                        + " md5=" + md5File(mergeFiles[testIndex][0])
                        + " memory=" + (m1 - m0) + "/" + m1);
            }
            for (int m = 1; m < numTests; m++) {
                System.out.printf("merge%1d avg0=%12d avg%1d=%12d delta=%12d %8.4f%%\n",
                        m, totals[0] / numRuns, m, totals[m] / numRuns,
                        totals[m] - totals[0], (((float) totals[m] - totals[0]) / (float) totals[0]) * 100.0);
                System.out.printf("      last0=%12d avg%1d=%12d delta=%12d %8.4f%%\n",
                        totalsFinal[0], m, totalsFinal[m],
                        totalsFinal[m] - totalsFinal[0], (((float) totalsFinal[m] - totalsFinal[0]) / (float) totalsFinal[0]) * 100.0);
            }
        }
    }

    private List<Long> run(int testIndex, File mergeFile) throws IOException {
        List<Long> data = Arrays.asList(0L, 0L);
        Long time0 = System.nanoTime();
        switch (testIndex) {
            case 0:
                data = merge0(mergeFile);
                break;
            case 1:
                data = merge1(mergeFile);
                break;
            case 2:
                data = merge2(mergeFile);
                break;
            case 3:
                data = merge3(mergeFile);
                break;
        }
        Long time1 = System.nanoTime();
        return Arrays.asList(time1 - time0, data.get(0), data.get(1));
    }

    private List<Long> merge0(File mergeOutput) throws IOException {
        long maxId = 0L;
        long records = 0;

        //noinspection Convert2Lambda
        SortedMap<StandardProvenanceEventRecord, RecordReader> recordToReaderMap = new TreeMap<>(new Comparator<StandardProvenanceEventRecord>() {
            @Override
            public int compare(final StandardProvenanceEventRecord o1, final StandardProvenanceEventRecord o2) {
                return Long.compare(o1.getEventId(), o2.getEventId());
            }
        });

        // load first records
        for (File journalFile : journalFiles) {
            RecordReader reader = RecordReaders.newRecordReader(journalFile, null, Integer.MAX_VALUE);
            StandardProvenanceEventRecord record = reader.nextRecord();
            if (record != null) {
                recordToReaderMap.put(record, reader);
            }
        }

        try (final RecordWriter writer = RecordWriters.newRecordWriter(mergeOutput, false, true)) {
            writer.writeHeader(0);

            while (!recordToReaderMap.isEmpty()) {
                final Map.Entry<StandardProvenanceEventRecord, RecordReader> entry = recordToReaderMap.entrySet().iterator().next();
                final StandardProvenanceEventRecord record = entry.getKey();
                final RecordReader reader = entry.getValue();

                writer.writeRecord(record, record.getEventId());

                if (record.getEventId() <= maxId) {
                    throw new RuntimeException("EventIDs out of order");
                }
                maxId = record.getEventId();

                records++;

                // Remove this entry from the map
                recordToReaderMap.remove(record);

                // Get the next entry from this reader and add it to the map
                StandardProvenanceEventRecord nextRecord = null;

                try {
                    nextRecord = reader.nextRecord();
                } catch (final EOFException ignore) {
                }

                if (nextRecord == null) {
                    reader.close();
                } else {
                    recordToReaderMap.put(nextRecord, reader);
                }

            }
        }
        return Arrays.asList(maxId, records);
    }

    private List<Long> merge1(File mergeOutput) throws IOException {
        long maxId = 0L;
        long records = 0;

        //noinspection Convert2Lambda
        SortedMap<StandardProvenanceEventRecord, RecordReader> recordToReaderMap = new TreeMap<>(new Comparator<StandardProvenanceEventRecord>() {
            @Override
            public int compare(final StandardProvenanceEventRecord o1, final StandardProvenanceEventRecord o2) {
                return Long.compare(o1.getEventId(), o2.getEventId());
            }
        });

        // load first records
        for (File journalFile : journalFiles) {
            RecordReader reader = RecordReaders.newRecordReader(journalFile, null, Integer.MAX_VALUE);
            StandardProvenanceEventRecord record = reader.nextRecord();
            if (record != null) {
                recordToReaderMap.put(record, reader);
            }
        }

        try (final RecordWriter writer = RecordWriters.newRecordWriter(mergeOutput, false, true)) {
            writer.writeHeader(0);

            while (!recordToReaderMap.isEmpty()) {
                final StandardProvenanceEventRecord record = recordToReaderMap.firstKey();
                final RecordReader reader = recordToReaderMap.get(record);

                writer.writeRecord(record, record.getEventId());

                if (record.getEventId() <= maxId) {
                    throw new RuntimeException("EventIDs out of order");
                }
                maxId = record.getEventId();

                records++;

                // Remove this entry from the map
                recordToReaderMap.remove(record);

                // Get the next entry from this reader and add it to the map
                StandardProvenanceEventRecord nextRecord = null;

                try {
                    nextRecord = reader.nextRecord();
                } catch (final EOFException ignore) {
                }

                if (nextRecord != null) {
                    recordToReaderMap.put(nextRecord, reader);
                } else {
                    reader.close();
                }

            }
        }
        return Arrays.asList(maxId, records);
    }

    private class Merge2Tuple {
        StandardProvenanceEventRecord record;
        RecordReader reader;
        Merge2Tuple(StandardProvenanceEventRecord record, RecordReader reader) {
            this.record = record;
            this.reader = reader;
        }
    }

    private List<Long> merge2(File mergeOutput) throws IOException {
        long maxId = 0L;
        long records = 0;

        //noinspection Convert2Lambda
        PriorityQueue<Merge2Tuple> recordQueue = new PriorityQueue<>(new Comparator<Merge2Tuple>() {
            @Override
            public int compare(Merge2Tuple o1, Merge2Tuple o2) {
                return Long.compare(o1.record.getEventId(), o2.record.getEventId());
            }
        });

        // load first records
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < journalFiles.length; i++) {
            RecordReader reader = RecordReaders.newRecordReader(journalFiles[i], null, Integer.MAX_VALUE);
            StandardProvenanceEventRecord record = reader.nextRecord();
            if (record != null) {
                recordQueue.add(new Merge2Tuple(record, reader));
            }
        }

        try (final RecordWriter writer = RecordWriters.newRecordWriter(mergeOutput, false, true)) {
            writer.writeHeader(0);

            Merge2Tuple tuple;
            while (!recordQueue.isEmpty()) {
                tuple = recordQueue.poll();

                writer.writeRecord(tuple.record, tuple.record.getEventId());

                if (tuple.record.getEventId() <= maxId) {
                    throw new RuntimeException("EventIDs out of order");
                }
                maxId = tuple.record.getEventId();

                records++;

                try {
                    tuple.record = tuple.reader.nextRecord();
                } catch (final EOFException ignore) {
                }

                if (tuple.record != null) {
                    recordQueue.add(tuple);
                } else {
                    tuple.reader.close();
                }

            }
        }
        return Arrays.asList(maxId, records);
    }

    private int findNextRecord(final StandardProvenanceEventRecord[] recordList,
                               final int lastIndex, final Long lastEventId) {
        Long lowestId = Long.MAX_VALUE;
        int lowestIndex = -1;
        for (int counter = 0; counter < numJournals; counter++) {
            int index = (lastIndex + counter) % numJournals;
            if (recordList[index] != null) {
                final long eventId = recordList[index].getEventId();
                // return immediately if next sequential event is found
                if (eventId == lastEventId + 1) {
                    return index;
                }
                if (eventId < lowestId) {
                    lowestId = eventId;
                    lowestIndex = index;
                }
            }
        }
        return lowestIndex;
    }

    private List<Long> merge3(File mergeOutput) throws IOException {
        long maxId = 0L;
        long records = 0;

        StandardProvenanceEventRecord[] recordList = new StandardProvenanceEventRecord[numJournals];
        RecordReader[] readerList = new RecordReader[numJournals];

        // load first records
        //noinspection ForLoopReplaceableByForEach
        int activeRecords = 0;
        for (int i = 0; i < journalFiles.length; i++) {
            readerList[i] = RecordReaders.newRecordReader(journalFiles[i], null, Integer.MAX_VALUE);
            recordList[i] = readerList[i].nextRecord();
            if (recordList[i] != null) {
                activeRecords++;
            }
        }

        try (final RecordWriter writer = RecordWriters.newRecordWriter(mergeOutput, false, true)) {
            writer.writeHeader(0);

            int index = 0;
            Long lastEventId = -1L;
            while (activeRecords > 0) {
                // NOTE: findNextRecord skips readers for null record entries
                index = findNextRecord(recordList, index, lastEventId);
                lastEventId = recordList[index].getEventId();

                writer.writeRecord(recordList[index], recordList[index].getEventId());

                if (recordList[index].getEventId() <= maxId) {
                    throw new RuntimeException("EventIDs out of order");
                }
                maxId = recordList[index].getEventId();

                records++;

                try {
                    recordList[index] = readerList[index].nextRecord();
                } catch (final EOFException ignore) {
                }

                if (recordList[index] == null) {
                    activeRecords--;
                }
            }
        }
        return Arrays.asList(maxId, records);
    }

    private void makePaths(String folder) throws IOException {
        for (int i = 0; i < numJournals; i++) {
            journalFiles[i] = new File(folder + File.separator + "journal-" + i);
            if (journalFiles[i].exists()) {
                Files.delete(journalFiles[i].toPath());
            }
        }
        if (this.showFiles) {
            System.out.println("journalFiles=" + StringUtils.join(journalFiles, ", "));
        }

        for (int i = 0; i < numTests; i++) {
            for (int j = 0; j < numRuns; j++) {
                mergeFiles[i][j] = new File(folder + File.separator + "merge0-" + i + "-" + j);
                if (mergeFiles[i][j].exists()) {
                    Files.delete(mergeFiles[i][j].toPath());
                }
            }
        }
        if (this.showFiles) {
            System.out.print("mergeFiles=");
            for (int i = 0; i < mergeFiles.length; i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.print(mergeFiles[i][0]);
            }
            System.out.println("");
        }
    }

    private void writeJournals() throws IOException {

        final Map<String, String> attributes = new HashMap<>();

        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();

        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(ProvenanceEventType.RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        attributes.put("uuid1", "2345678-0000-0000-0000-012345678912-");
        attributes.put("uuid2", "345678-0000-0000-0000-012345678912-1");
        attributes.put("uuid3", "45678-0000-0000-0000-012345678912-12");
        attributes.put("uuid4", "5678-0000-0000-0000-012345678912-123");
        attributes.put("uuid5", "678-0000-0000-0000-012345678912-1234");
        attributes.put("uuid6", "78-0000-0000-0000-012345678912-12345");
        attributes.put("uuid7", "8-0000-0000-0000-012345678912-123456");
        attributes.put("uuid8", "-0000-0000-0000-012345678912-1234567");
        attributes.put("uuid9", "0000-0000-0000-012345678912-12345678");
        builder.fromFlowFile(createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");

        final ProvenanceEventRecord record = builder.build();
        RecordWriter[] writers = new RecordWriter[numJournals];

        for (int n = 0; n < numJournals; n++) {
            writers[n] = RecordWriters.newRecordWriter(journalFiles[n], false, false);
            writers[n].writeHeader(0L);
        }
        for (int id = baseEventId; id < baseEventId + numEvents; id++) {
            writers[id % numJournals].writeRecord(record, id);
        }
        for (int n = 0; n < numJournals; n++) {
            writers[n].close();
        }
    }

    private String md5File(File source) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try {
            md.update(Files.readAllBytes(Paths.get(source.getPath())));
        } catch(NoSuchFileException nsf) {
            System.out.println("filename=" + source.getPath());
            throw nsf;
        }
        return DatatypeConverter.printHexBinary(md.digest());
    }

    @SuppressWarnings("SameParameterValue")
    private static FlowFile createFlowFile(final long id, final long fileSize, final Map<String, String> attributes) {
        final Map<String, String> attrCopy = new HashMap<>(attributes);

        return new FlowFile() {
            @Override
            public long getId() {
                return id;
            }

            @Override
            public long getEntryDate() {
                return System.currentTimeMillis();
            }

            @Override
            public long getLineageStartDate() {
                return System.currentTimeMillis();
            }

            @Override
            public Long getLastQueueDate() {
                return System.currentTimeMillis();
            }

            @Override
            public Set<String> getLineageIdentifiers() {
                return null;
            }

            @Override
            public boolean isPenalized() {
                return false;
            }

            @Override
            public String getAttribute(final String s) {
                return attrCopy.get(s);
            }

            @Override
            public long getSize() {
                return fileSize;
            }

            @Override
            public Map<String, String> getAttributes() {
                return attrCopy;
            }

            @Override
            public int compareTo(@SuppressWarnings("NullableProblems") final FlowFile o) {
                return 0;
            }
        };
    }
}
