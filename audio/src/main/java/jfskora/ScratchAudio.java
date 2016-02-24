package jfskora;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ScratchAudio {

    public static String RESOURCE_PATH_KEY = "resource.path";
    public static String TIKA_MEDIA_TYPE_KEY = "tika.media.type";
    public static String TIKA_LANGUAGE_KEY = "tika.language";

    public static void main(String[] args) {

        Options options = new Options();

        OptionBuilder.withLongOpt("lang");
        OptionBuilder.withDescription("include language summary");
        options.addOption(OptionBuilder.create("lang"));

        OptionBuilder.withLongOpt("tika_detect");
        OptionBuilder.withDescription("use Tika detect logic");
        options.addOption(OptionBuilder.create("tika_detect"));

        OptionBuilder.withLongOpt("javafx");
        OptionBuilder.withDescription("use Java FX logic");
        options.addOption(OptionBuilder.create("javafx"));

        OptionBuilder.withLongOpt("tika_parse");
        OptionBuilder.withDescription("use Tika parse logic");
        options.addOption(OptionBuilder.create("tika_parse"));

        OptionBuilder.withLongOpt("tika_language");
        OptionBuilder.withDescription("use Tika language logic");
        options.addOption(OptionBuilder.create("tika_language"));

        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            System.exit(1);
        }

        List<File> scannedFiles = getFileList(commandLine.getArgs());
        HashMap<File, HashMap<String, Metadata>> files = new HashMap<>();
        for (File file : scannedFiles) {
            files.put(file, new HashMap<String, Metadata>());
        }

        ArrayList<String> ctxList = new ArrayList<String>();
        if (commandLine.hasOption("tika_detect")) {
            ctxList.add("tika_detect");
        }
        if (commandLine.hasOption("javafx")) {
            ctxList.add("javafx");
        }
        if (commandLine.hasOption("tika_parse")) {
            ctxList.add("tika_parse");
        }
        if (commandLine.hasOption("tika_language")) {
            ctxList.add("tika_language");
        }
        String[] contexts = ctxList.toArray(new String[1]);

        System.out.println();
        for (File f : files.keySet()) {
            if (commandLine.hasOption("tika_detect")) {
                files.get(f).put("tika_detect", tika_detect(f));
            }
            if (commandLine.hasOption("javafx")) {
                files.get(f).put("javafx", javafx(f));
            }
            if (commandLine.hasOption("tika_parse")) {
                files.get(f).put("tika_parse", tika_parse(f));
            }
            if (commandLine.hasOption("tika_language")) {
                files.get(f).put("tika_language", tika_language(f));
            }
        }
        System.out.println("========================================");

        for (File f : files.keySet()) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println(f.getName());
            for (String context : contexts) {
                System.out.println("++++++++++++++++++++++++++++++++++++++++");
                System.out.println(context);
                System.out.println("----------------------------------------");
                System.out.println(formatMetadata(files.get(f).get(context)));
            }
            System.out.println("========================================");
        }

        if (commandLine.hasOption("lang")) {
            System.out.println();
            System.out.println("============================================================");
            for (File f : files.keySet()) {
                System.out.println(f.getName());
                HashMap<String, Metadata> fileMetaMap = files.get(f);
                for (String context : contexts) {
                    Metadata ctxMeta = fileMetaMap.get(context);
                    if (Arrays.asList(ctxMeta.names()).contains(TIKA_LANGUAGE_KEY)) {
                        System.out.println(context + "." + TIKA_LANGUAGE_KEY + " = " + ctxMeta.get(TIKA_LANGUAGE_KEY));
                    }
                }
                System.out.println("----------------------------------------");
            }
        }
    }

    private static List<File> getFileList(String[] args) {
        List<File> files = new ArrayList<>();
        for (String arg : args) {
            File argFile = new File(arg);
            if (argFile.isDirectory()) {
                File[] children = argFile.listFiles();
                if (children != null && children.length > 0) {
                    for (File argChildFile : children) {
                        if (!argChildFile.isDirectory()) {
                            files.add(argChildFile);
                        }
                    }
                }
            } else {
                files.add(argFile);
            }
        }
        return files;
    }

    private static Metadata tika_language(File file) {
        Tika tika = new Tika();
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try {
            FileInputStream content = new FileInputStream(file);
            parser.parse(content, handler, metadata, new ParseContext());
            LanguageIdentifier object = new LanguageIdentifier(handler.toString());
            metadata.set(TIKA_LANGUAGE_KEY, object.getLanguage());
        } catch (SAXException | TikaException | IOException e) {
            e.printStackTrace();
            return metadata;
        }
        return metadata;
    }

    private static Metadata tika_detect(File file) {
        Metadata metadata = new Metadata();
        TikaConfig tikaConfig;
        try {
            tikaConfig = new TikaConfig();
        } catch (TikaException e) {
            e.printStackTrace();
            return metadata;
        } catch (IOException e) {
            e.printStackTrace();
            return metadata;
        }

        metadata.set(Metadata.RESOURCE_NAME_KEY, file.toString());
        metadata.set(RESOURCE_PATH_KEY, file.getAbsolutePath());
        try {
            MediaType mediaType = tikaConfig.getDetector().detect(TikaInputStream.get(file), metadata);
            metadata.set(TIKA_MEDIA_TYPE_KEY, mediaType.toString());

            StringBuilder text = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                text.append(line).append(System.lineSeparator());
            }
            reader.close();
            LanguageIdentifier identifier = new LanguageIdentifier(text.toString());
            metadata.set(TIKA_LANGUAGE_KEY, identifier.getLanguage());
            return metadata;
        } catch (FileNotFoundException e) {
            metadata.set("error_tika_parse", "tika_parse error processing file (" + file.getName() + "): " + e.getMessage());
            return metadata;
        } catch (IOException e) {
            metadata.set("error_tika_parse", "tika_parse error processing file (" + file.getName() + "): " + e.getMessage());
            return metadata;
        }
    }

    private static Metadata javafx(File audioFile) {

        final Metadata metadata = new Metadata();
        final AudioInputStream audioInputStream;
        final AudioFormat audioFormat;
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(audioFile));
            audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            audioFormat = audioInputStream.getFormat();
        } catch (UnsupportedAudioFileException | IOException e) {
            metadata.set("error-javafx", "javafx error processing file (" + audioFile.getName() + "): " + e.getMessage());
            return metadata;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        metadata.set("format", audioFormat.toString());
        metadata.set("length", audioInputStream.getFrameLength() + " frames");
        metadata.set("frames", Float.toString(audioFormat.getFrameRate()));
        metadata.set("time", Float.toString(audioInputStream.getFrameLength() / audioFormat.getFrameRate()) + " seconds");
        return metadata;
    }

    private static Metadata tika_parse(File audioFile) {
        Metadata metadata = new Metadata();
        try {
            String filetype = new Tika().detect(audioFile);
            metadata.set("tika.filetype", filetype);
            metadata.set("file.size", Long.toString(audioFile.length()));

            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(audioFile));
            new AutoDetectParser().parse(inputStream, new BodyContentHandler(), metadata);
            inputStream.close();

            for (String key : metadata.names()) {
                StringBuilder dataBuilder = new StringBuilder();
                if (metadata.isMultiValued(key)) {
                    for (String val : metadata.getValues(key)) {
                        if (dataBuilder.length() > 1) {
                            dataBuilder.append(", ");
                        }
                        dataBuilder.append(val);
                    }
                } else {
                    dataBuilder.append(metadata.get(key));
                }
                metadata.set(key, dataBuilder.toString().trim());
            }

            inputStream = new BufferedInputStream(new FileInputStream(audioFile));
            MediaType media = new DefaultDetector().detect(inputStream, new Metadata());
            metadata.set("media", media.toString());
        } catch (SAXException | IOException | TikaException e) {
            metadata.set("error_tika_detect", "tika_detect error processing file (" + audioFile.getName() + "): " + e.getMessage());
        }
        return metadata;
    }

    private static String formatMetadata(Metadata metadata) {
        StringBuilder builder = new StringBuilder();
        String[] names = metadata.names();
        Arrays.sort(names, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        for (String key : names) {
            StringBuilder multiBldr = new StringBuilder();
            if (metadata.isMultiValued(key)) {
                for (String val : metadata.getValues(key)) {
                    if (multiBldr.length() > 1) {
                        multiBldr.append(", ");
                    }
                    multiBldr.append(val);
                }
            } else {
                multiBldr.append(metadata.get(key));
            }
            if (builder.length() > 0) {
                builder.append(System.lineSeparator());
            }
            builder.append(key).append(": ").append(multiBldr.toString().trim());
        }
        return builder.toString();
    }
}
