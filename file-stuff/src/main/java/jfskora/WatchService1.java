package jfskora;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class WatchService1 {

    private WatchService watcher;
    private WatchKey key;
    private Path target;

    public static void main(String[] args) {
        WatchService1 watchService1 = null;
        try {
            watchService1 = new WatchService1(new File("/tmp").toPath());
        } catch (IOException e) {
            System.err.println("Could not create WatchService:");
            e.printStackTrace();
            System.exit(1);
        }
        System.err.println("service starting");
        watchService1.run();
        System.err.println("service stopped");
    }

    private WatchService1(Path folder) throws IOException {
        target = folder;
        try {
            System.err.print("Starting watch service on " + target.toAbsolutePath().toString());
            watcher = FileSystems.getDefault().newWatchService();
            key = target.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            FileSystem fs = FileSystems.getDefault();
        } catch (IOException e) {
            e.printStackTrace();
            throw(e);
        }
    }

    private void run() {
        for (;;) {
            WatchKey currkey;
            try {
                currkey = watcher.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            List<WatchEvent<?>> events = currkey.pollEvents();
            for (WatchEvent<?> event : events) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;

                Path filename = ev.context();
                Path child = target.resolve(filename);

                String contentType;
                try {
                    contentType = Files.probeContentType(child);
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    contentType = "unknown";
                }

                System.out.format("%n%s: '%s' is type %s%n", kind, filename, contentType);

                if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                    try {
                        FileStore store = Files.getFileStore(child);
                        if (store != null && store.supportsFileAttributeView(BasicFileAttributeView.class)) {
                            BasicFileAttributes basicAttribs = Files.getFileAttributeView(child, BasicFileAttributeView.class).readAttributes();
                            System.out.format("lastModifiedTime: %s%n", basicAttribs.lastModifiedTime().toString());
                            System.out.format("lastAccessTime: %s%n", basicAttribs.lastAccessTime().toString());
                            System.out.format("creationTime: %s%n", basicAttribs.creationTime().toString());
                            System.out.format("size: %d%n", basicAttribs.size());
                            System.out.format("isRegularFile: %b%n", basicAttribs.isRegularFile());
                            System.out.format("isDirectory: %b%n", basicAttribs.isDirectory());
                            System.out.format("isSymbolicLink: %b%n", basicAttribs.isSymbolicLink());
                            System.out.format("isOther: %b%n", basicAttribs.isOther());
                            System.out.format("fileKey: %b%n", basicAttribs.fileKey());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }
}
