package jfskora;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ScratchTika {

    public static String LINE1 = "----------------------------------------";
    public static String LINE2 = "========================================";

    public static void main(String[] args) throws TikaException, SAXException, IOException {
        for (String arg: args) {
            File fileArg = new File(arg);
            if (Files.isDirectory(fileArg.toPath())) {
                for (File file : Arrays.asList(fileArg.listFiles())) {
                    parseFile(file.getPath());
                }
            } else {
                parseFile(arg);
            }
        }
    }

    public static void parseFile(String filepath) throws SAXException, TikaException, IOException {
        final InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(filepath);
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try {
            parser.parse(inputStream, handler, metadata);

            StringBuilder bldr = new StringBuilder();
            for (String key : metadata.names()) {
                StringBuilder multiBldr = new StringBuilder();
                if (metadata.isMultiValued(key)) {
                    multiBldr.append("[");
                    for (String val : metadata.getValues(key)) {
                        if (multiBldr.length() > 1) {
                            multiBldr.append(", ");
                        }
                        multiBldr.append(val);
                    }
                    multiBldr.append("]");
                } else {
                    multiBldr.append(metadata.get(key));
                }
                bldr.append(key).append(" = ").append(multiBldr.toString()).append(System.lineSeparator());
            }

            System.err.println(LINE2);
            System.err.println("file: " + filepath);
            System.err.println(LINE1);
            System.err.println("handler.toString()");
            System.err.println(LINE1);
            System.err.println(handler.toString().trim());
            System.err.println(LINE1);
            System.err.println("metadata");
            System.err.println(LINE1);
            System.err.println(bldr.toString().trim());
            System.err.println(LINE2);
        } catch (SAXException e) {
            System.err.println("SAXException: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (TikaException e) {
            System.err.println("TikaException: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }
}
