package jfskora;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScratchAudio {

    public static String RESOURCE_PATH_KEY = "resource.path";
    public static String TIKA_MEDIA_TYPE_KEY = "tika.media.type";
    public static String TIKA_LANGUAGE_KEY = "tika.language";

    public static void main(String[] args) {
        HashMap<File, HashMap<String, Metadata>> files = new HashMap<>();
        for (String arg : args) {
            File argFile = new File(arg);
            if (argFile.isDirectory()) {
                File[] children = argFile.listFiles();
                if (children != null && children.length > 0) {
                    for (File argChildFile : children) {
                        if (!argChildFile.isDirectory()) {
                            files.put(argChildFile, new HashMap<String, Metadata>());
                        }
                    }
                }
            } else {
                files.put(argFile, new HashMap<String, Metadata>());
            }
        }
        System.out.println();
        System.out.println("============================================================");
        System.out.println("types");
        System.out.println("----------------------------------------");
        for (File f : files.keySet()) {
            files.get(f).put("tika-detect", tika_getMetadata(f));
            files.get(f).put("javafx", audioFileStatsJavaXSound(f));
            files.get(f).put("tika-parse", audioFileStatsTika(f));
            System.out.println(f.getName() + " = " + (files.get(f).get("tika-detect").get(TIKA_MEDIA_TYPE_KEY)));
        }
        System.out.println("========================================");

        for (File f : files.keySet()) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println(f.getName());
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("tika-detect");
            System.out.println("----------------------------------------");
            System.out.println(formatMetadata(files.get(f).get("tika-detect")));
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("(JavaFX) AudioSystem.getAudioInputStream().getFormat()");
            System.out.println("----------------------------------------");
            System.out.println(formatMetadata(files.get(f).get("javafx")));
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            System.out.println("tika-parse");
            System.out.println("----------------------------------------");
            System.out.println(formatMetadata(files.get(f).get("tika-parse")));
            System.out.println("========================================");
        }
    }

    private static Metadata tika_getMetadata(File file) {
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
            metadata.set("error-tikaparse", "tika-parse error processing file (" + file.getName() + "): " + e.getMessage());
            return metadata;
        } catch (IOException e) {
            metadata.set("error-tikaparse", "tika-parse error processing file (" + file.getName() + "): " + e.getMessage());
            return metadata;
        }

//        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("en.ngp");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        StringBuilder content = new StringBuilder();
//        String line = reader.readLine();
//        while (line != null) {
//            content.append(line);
//            line = reader.readLine();
//        }
//        reader.close();
//        LanguageProfile profile = new LanguageProfile(content.toString());

    }

    private static Metadata audioFileStatsJavaXSound(File audioFile) {

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

    private static Metadata audioFileStatsTika(File audioFile) {
        Metadata metadata = new Metadata();
        try {
            String filetype = new Tika().detect(audioFile);
            metadata.set("tika.filetype", filetype);

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
            metadata.set("error-tika-detect", "tika-detect error processing file (" + audioFile.getName() + "): " + e.getMessage());
        }
        return metadata;
    }

    private static String formatMetadata(Metadata metadata) {
        StringBuilder builder = new StringBuilder();
        for (String key : metadata.names()) {
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
