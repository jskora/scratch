package jfskora;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class WatchService1 {

    private WatchService watcher;

    public static void main(String[] args) {
        WatchService1 watchService1 = new WatchService1();
        watchService1.run();
    }

    public WatchService1(File folder) {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
