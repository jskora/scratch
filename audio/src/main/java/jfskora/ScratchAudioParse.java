package jfskora;

import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ScratchAudioParse {

    public static void main(String[] args) {
        for (File f : getFileList(args)) {
            Metadata metadata = tika_parse(f);
            System.out.println();
            System.out.println("============================================================");
            System.out.println(f.getName());
                System.out.println("----------------------------------------");
                System.out.println(formatMetadata(metadata));
            System.out.println("========================================");
        }
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
            metadata.set("error_tika_parse", "tika_parse error processing file (" + audioFile.getName() + "): " + e.getMessage());
        }
        return metadata;
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
