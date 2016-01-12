package jfskora;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class ScratchAudio {
    public static void main(String[] args) {
        playWaveFile("8kulaw.wav");
    }

    private static void playWaveFile(String url) {
        System.out.println("----------------------------------------");
        System.out.println("Playing wave file: " + url);
        System.out.println("----------------------------------------");

        final InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(url);
        final AudioInputStream audioInputStream;
        final AudioFormat audioFormat;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            audioFormat = audioInputStream.getFormat();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("format: " + audioFormat.toString());
        System.out.println("length: " + audioInputStream.getFrameLength() + " frames @ " + audioFormat.getFrameRate() +
                " fps, " + audioInputStream.getFrameLength() / audioFormat.getFrameRate() + " seconds");

        final Clip clip;
        final Long start, end;
        try {
            clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            clip.open(audioInputStream);
            start = System.currentTimeMillis();
            clip.start();
            clip.drain();
            end = System.currentTimeMillis();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("play time: " + (end - start) + " milliseconds");
        System.out.println("---------- done ----------\n");
    }
}
