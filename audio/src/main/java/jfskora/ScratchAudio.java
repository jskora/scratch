package jfskora;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.exception.TikaException;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScratchAudio {
    public static void main(String[] args) {
        for (String arg : args) {
            File argFile = new File(arg);
            if (argFile.isDirectory()) {
                File[] children = argFile.listFiles();
                if (children != null && children.length > 0) {
                    for (File argChildFile : children) {
                        if (!argChildFile.isDirectory()) {
                            audioFileStats(argChildFile);
                        }
                    }
                }
            } else {
                audioFileStats(argFile);
            }
        }
    }

    private static void audioFileStats(File audioFile) {
        System.out.println("\n============================================================");
        System.out.println("audio file: " + audioFile.getName());
        System.out.println("----------------------------------------");
        System.out.println("javax.sound");
        System.out.println("----------------------------------------");
        System.out.flush();
        System.err.flush();
        audioFileStatsJavaXSound(audioFile);
        System.out.flush();
        System.err.flush();
        System.out.println("----------------------------------------");
        System.out.println("tika");
        System.out.println("----------------------------------------");
        System.out.flush();
        System.err.flush();
        audioFileStatsTika(audioFile);
    }

    private static void audioFileStatsJavaXSound(File audioFile) {

        final AudioInputStream audioInputStream;
        final AudioFormat audioFormat;
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(audioFile));
            audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            audioFormat = audioInputStream.getFormat();
        } catch (UnsupportedAudioFileException | IOException e) {
            System.out.println("error processing file: " + e.getMessage());
            System.out.flush();
            System.err.flush();
            return;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        System.out.println("format: " + audioFormat.toString());
        System.out.println("length: " + audioInputStream.getFrameLength() + " frames");
        System.out.println("frames: " + audioFormat.getFrameRate());
        System.out.println("time: " + audioInputStream.getFrameLength() / audioFormat.getFrameRate() + " seconds");
        System.out.flush();
        System.err.flush();
    }

    private static void audioFileStatsTika(File audioFile) {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(audioFile));
            inputStream.mark((int)audioFile.length());
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
                if (bldr.length() > 0) {
                    bldr.append(System.lineSeparator());
                }
                bldr.append(key).append(": ").append(multiBldr.toString().trim());
            }

            if (handler.toString().trim().length() > 0) {
                System.err.println("------------------------------------------------------------");
                System.err.println("    " + handler.toString());
                System.err.println("------------------------------------------------------------");
                System.out.flush();
                System.err.flush();
            }
            System.out.println("metadata:");
            System.out.println(bldr.toString());
            System.out.flush();
            System.err.flush();

            inputStream.reset();
            MediaType media = new DefaultDetector().detect(inputStream, new Metadata());
            System.out.println("media: " +  media.toString());
            System.out.flush();
            System.err.flush();
        } catch (SAXException e) {
            System.out.println("SAXException: " + e.getMessage());
            System.out.flush();
            System.err.flush();
            e.printStackTrace();
        } catch (TikaException e) {
            System.out.println("TikaException: " + e.getMessage());
            System.out.flush();
            System.err.flush();
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            System.out.flush();
            System.err.flush();
            e.printStackTrace();
        }
    }

}
