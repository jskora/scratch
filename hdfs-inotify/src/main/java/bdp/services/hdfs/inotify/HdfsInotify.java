package bdp.services.hdfs.inotify;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.hadoop.hdfs.DFSInotifyEventInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.EventBatch;
import org.apache.hadoop.hdfs.inotify.MissingEventsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

import static bdp.services.config.loader.HadoopConfig.getConfiguration;
import static com.google.common.base.Preconditions.checkNotNull;

public class HdfsInotify {

    public static Logger LOG = LoggerFactory.getLogger(HdfsInotify.class);

    private static class HdfsInotifyOpts {
        @Parameter(names = {"--kronos-config"}, required = true)
        private String kronosConfig;

        String getKronosConfig() {
            return kronosConfig;
        }
    }

    private static Map getConfig(HdfsInotifyOpts opts) {
        Map config;
        try (FileInputStream configFile = new FileInputStream(opts.getKronosConfig())) {
            config = new Yaml().load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("reading configuration failed");
        }
        return config;
    }

    private static DistributedFileSystem getDistributedFileSystem(String configName, Map configs) throws IOException {
        checkNotNull(configName);

        return (DistributedFileSystem) DistributedFileSystem.get(getConfiguration(configName, configs));
    }

    private static DistributedFileSystem getDistributedHadoopFileSystem(Map hdfsConfig) {
        DistributedFileSystem fs;
        try {
            fs = getDistributedFileSystem("directory-poll", hdfsConfig);
        } catch (IOException e) {
            throw new RuntimeException("Failed to collect working set.", e);
        }
        return fs;
    }

    public static void main(String[] args) throws IOException, MissingEventsException, InterruptedException {

        HdfsInotifyOpts opts = new HdfsInotifyOpts();
        JCommander parser = new JCommander();
        parser.addObject(opts);
        parser.parse(args);

        LOG.info("configuring");

        Map config = getConfig(opts);
        DistributedFileSystem fs = getDistributedHadoopFileSystem(config);
        DFSInotifyEventInputStream stream = fs.getInotifyEventStream();
        DecimalFormat decimal = new DecimalFormat("#,###");

        LOG.info("starting main loop");
        while (true) {
            EventBatch batch = stream.take();
            Arrays.stream(batch.getEvents()).forEach(rawevent -> {
                switch(rawevent.getEventType()) {
                    case APPEND:
                        Event.AppendEvent appendEvent = (Event.AppendEvent)rawevent;
                        LOG.info(String.format("append   %s",
                                appendEvent.getPath()
                        ));
                        break;
                    case CLOSE:
                        Event.CloseEvent closeEvent = (Event.CloseEvent)rawevent;
                        LOG.info(String.format("%s close    %s size=%s",
                                Instant.ofEpochMilli(closeEvent.getTimestamp()).toString(),
                                closeEvent.getPath(),
                                decimal.format(closeEvent.getFileSize())
                        ));
                        break;
                    case CREATE:
                        Event.CreateEvent createEvent = (Event.CreateEvent)rawevent;
                        LOG.info(String.format("%s create   %s owner=%s group=%s perms=%s",
                                Instant.ofEpochMilli(createEvent.getCtime()).toString(),
                                createEvent.getPath(),
                                createEvent.getOwnerName(),
                                createEvent.getGroupName(),
                                createEvent.getPerms().toShort()
                        ));
                        break;
                    case METADATA:
                        Event.MetadataUpdateEvent metaEvent = (Event.MetadataUpdateEvent)rawevent;
                        LOG.info(String.format("%s metadata %s metadatatype=%s owner=%s group=%s atime=%s",
                                Instant.ofEpochMilli(metaEvent.getMtime()).toString(),
                                metaEvent.getPath(),
                                metaEvent.getMetadataType(),
                                metaEvent.getOwnerName(),
                                metaEvent.getGroupName(),
                                Instant.ofEpochMilli(metaEvent.getAtime()).toString()
                        ));
                        break;
                    case RENAME:
                        Event.RenameEvent renameEvent = (Event.RenameEvent)rawevent;
                        LOG.info(String.format("%s rename   %s dst=%s",
                                Instant.ofEpochMilli(renameEvent.getTimestamp()).toString(),
                                renameEvent.getSrcPath(),
                                renameEvent.getDstPath()
                        ));
                        break;
                    case UNLINK:
                        Event.UnlinkEvent unlinkEvent = (Event.UnlinkEvent)rawevent;
                        LOG.info(String.format("%s unlink   %s",
                                Instant.ofEpochMilli(unlinkEvent.getTimestamp()).toString(),
                                unlinkEvent.getPath()
                        ));
                        break;
                    default:
                        LOG.info(String.format("unknown type=%s toString=%s",
                                rawevent.getEventType(),
                                rawevent.toString()
                        ));
                        break;
                }
            });
        }
    }
}
