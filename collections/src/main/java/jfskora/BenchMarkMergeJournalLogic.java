package jfskora;

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
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadFactory;

@SuppressWarnings("Duplicates")
public class BenchMarkMergeJournalLogic {

    @SuppressWarnings("unused")
    static Logger logger = LoggerFactory.getLogger(BenchMarkMergeJournalLogic.class);

    private static final int BASE_EVENT_ID = 100000;
    private static final int NUM_EVENTS = 100000;

    private static final int NUM_JOURNALS = 10;
    private static final int NUM_TESTS = 3;

    private static final int NUM_DISCARD_RUNS = 5;
    private static final int NUM_TRACKED_RUNS = 10;

    private File[] journalFiles;
    private File[] mergeFiles;

    private static String TEMP_FOLDER = System.getProperty("java.io.tmpdir");

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        BenchMarkMergeJournalLogic tester = new BenchMarkMergeJournalLogic();
        long[] totals = new long[NUM_TESTS];
        for (int n = 0; n < NUM_DISCARD_RUNS; n++) {
            tester.run();
        }
        for (int n = 0; n < NUM_TRACKED_RUNS; n++) {
            final Map<String, Long> results = tester.run();
            totals[0] += results.get("merge1");
            totals[1] += results.get("merge2");
            totals[2] += results.get("merge3");
            Thread.sleep(1000L);
        }
        Long totalDelta = totals[1] - totals[0];
        System.out.println("total merge1=" + totals[0] + " merge2=" + totals[1] + " delta=" + totalDelta
                + " " + (((float)(totals[1] - totals[0])) / (float)totals[0]) * 100.0 + "%");
        System.out.println("total merge1=" + totals[0] + " merge3=" + totals[2] + " delta=" + totalDelta
                + " " + (((float)(totals[2] - totals[0])) / (float)totals[0]) * 100.0 + "%");
    }

    private BenchMarkMergeJournalLogic() throws IOException {
        this.journalFiles = new File[NUM_JOURNALS];
        this.mergeFiles = new File[NUM_TESTS];
        makePaths();
        writeJournals();
    }

    private Map<String, Long> run() throws IOException, NoSuchAlgorithmException {
        writeJournals();
        Map<String, Long> results = new HashMap<>();

        Long time0;
        Long time1;
        System.out.println("");

        time0 = System.nanoTime();
        List<Long> merge2Results = merge2(mergeFiles[1]);
        time1 = System.nanoTime();
        System.out.println("merge2 id=" + merge2Results.get(0) + " records=" + merge2Results.get(1)
                + " nanos=" + (time1 - time0) + " md5=" + md5File(mergeFiles[1]));
        results.put("merge2", time1 - time0);

        time0 = System.nanoTime();
        List<Long> merge3Results = merge3(mergeFiles[2]);
        time1 = System.nanoTime();
        System.out.println("merge3 id=" + merge2Results.get(0) + " records=" + merge2Results.get(1)
                + " nanos=" + (time1 - time0) + " md5=" + md5File(mergeFiles[2]));
        results.put("merge3", time1 - time0);

        time0 = System.nanoTime();
        List<Long> merge1Results = merge1(mergeFiles[0]);
        time1 = System.nanoTime();
        System.out.println("merge1 id=" + merge1Results.get(0) + " records=" + merge1Results.get(1)
                + " nanos=" + (time1 - time0) + " md5=" + md5File(mergeFiles[0]));
        results.put("merge1", time1 - time0);

//        time0 = System.nanoTime();
//        List<Long> merge4Results = merge4(mergeFiles[3]);
//        time1 = System.nanoTime();
//        System.out.println("merge3 id=" + merge3Results.get(0) + " records=" + merge3Results.get(1)
//                + " nanos=" + (time1 - time0) + " md5=" + md5File(mergeFiles[3]));
//        results.add(time1 - time0);

        return results;
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
                final Map.Entry<StandardProvenanceEventRecord, RecordReader> entry = recordToReaderMap.entrySet().iterator().next();
                final StandardProvenanceEventRecord record = entry.getKey();
                final RecordReader reader = entry.getValue();

                writer.writeRecord(record, record.getEventId());

                assert(record.getEventId() > maxId);
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
                }

            }
        }
        return Arrays.asList(maxId, records);
    }

    private List<Long> merge2(File mergeOutput) throws IOException {
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

                assert(record.getEventId() > maxId);
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
                }

            }
        }
        return Arrays.asList(maxId, records);
    }

    private class RecordReaderTuple {
        StandardProvenanceEventRecord record;
        RecordReader reader;
        RecordReaderTuple(StandardProvenanceEventRecord record, RecordReader reader) {
            this.record = record;
            this.reader = reader;
        }
    }

    private List<Long> merge3(File mergeOutput) throws IOException {
        long maxId = 0L;
        long records = 0;


//        //noinspection Convert2Lambda
//        SortedMap<StandardProvenanceEventRecord, RecordReader> recordToReaderMap = new TreeMap<>(new Comparator<StandardProvenanceEventRecord>() {
//            @Override
//            public int compare(final StandardProvenanceEventRecord o1, final StandardProvenanceEventRecord o2) {
//                return Long.compare(o1.getEventId(), o2.getEventId());
//            }
//        });

        PriorityQueue<RecordReaderTuple> recordQueue = new PriorityQueue<>(new Comparator<RecordReaderTuple>() {
            @Override
            public int compare(RecordReaderTuple o1, RecordReaderTuple o2) {
                return Long.compare(o1.record.getEventId(), o2.record.getEventId());
            }
        });

        // load first records
        for (int i = 0; i < journalFiles.length; i++) {
            RecordReader reader = RecordReaders.newRecordReader(journalFiles[i], null, Integer.MAX_VALUE);
            StandardProvenanceEventRecord record = reader.nextRecord();
            if (record != null) {
//                recordToReaderMap.put(record, reader);
                recordQueue.add(new RecordReaderTuple(record, reader));
            }
        }

        try (final RecordWriter writer = RecordWriters.newRecordWriter(mergeOutput, false, true)) {
            writer.writeHeader(0);

            RecordReaderTuple tuple;
            while (!recordQueue.isEmpty()) {
                tuple = recordQueue.poll();

                writer.writeRecord(tuple.record, tuple.record.getEventId());

                assert(tuple.record.getEventId() > maxId);
                maxId = tuple.record.getEventId();

                records++;

//                // Remove this entry from the map
//                recordToReaderMap.remove(record);

//                // Get the next entry from this reader and add it to the map
//                StandardProvenanceEventRecord nextRecord = null;

                try {
//                    nextRecord = reader.nextRecord();
                    tuple.record = tuple.reader.nextRecord();
                } catch (final EOFException ignore) {
                }

                if (tuple.record != null) {
                    recordQueue.add(tuple);
                }

            }
        }
        return Arrays.asList(maxId, records);
    }

    private void makePaths() throws IOException {
        for (int i = 0; i < NUM_JOURNALS; i++) {
            journalFiles[i] = new File(TEMP_FOLDER + File.separator + "journal-" + i);
            if (journalFiles[i].exists()) {
                Files.delete(journalFiles[i].toPath());
            }
        }
        System.out.println("journalFiles=" + StringUtils.join(journalFiles, ", "));

        for (int i = 0; i < NUM_TESTS; i++) {
            mergeFiles[i] = new File(TEMP_FOLDER + File.separator + "merge1-" + i);
            if (mergeFiles[i].exists()) {
                Files.delete(mergeFiles[i].toPath());
            }
        }
        System.out.println("mergeFiles=" + StringUtils.join(mergeFiles, ", "));
    }

    private void writeJournals() throws IOException {

        final Map<String, String> attributes = new HashMap<>();

        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(ProvenanceEventType.RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        attributes.put("uuid", "12345678-0000-0000-0000-012345678912");
        builder.fromFlowFile(createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");

        final ProvenanceEventRecord record = builder.build();
        RecordWriter[] writers = new RecordWriter[NUM_JOURNALS];

        for (int n = 0; n < NUM_JOURNALS; n++) {
            writers[n] = RecordWriters.newRecordWriter(journalFiles[n], false, false);
            writers[n].writeHeader(0L);
        }
        for (int id = BASE_EVENT_ID; id < BASE_EVENT_ID + NUM_EVENTS; id++) {
            writers[id % NUM_JOURNALS].writeRecord(record, id);
        }
        for (int n = 0; n < NUM_JOURNALS; n++) {
            writers[n].close();
        }
    }

    private String md5File(File source) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(source.getPath())));
        return DatatypeConverter.printHexBinary(md.digest());
    }

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

            @Override
            public long getLineageStartIndex() {
                return 0;
            }

            @Override
            public long getQueueDateIndex() {
                return 0;
            }
        };
    }
}
