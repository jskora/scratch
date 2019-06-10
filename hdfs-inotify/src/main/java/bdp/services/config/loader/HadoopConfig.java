package bdp.services.config.loader;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.copyOfRange;
import static org.apache.hadoop.fs.CommonConfigurationKeysPublic.FS_DEFAULT_NAME_DEFAULT;
import static org.apache.hadoop.fs.FileSystem.FS_DEFAULT_NAME_KEY;

public class HadoopConfig {

    public static final String HADOOP_TOP_CONFIG = "hadoop";
    public static final String LOCAL_CONFIG = "use-local";
    public static final String RESOURCE_XML_CONFIG = "resources";

    public static Configuration getConfiguration(String configName, Map configs) {
        checkNotNull(configName);

        Configuration configuration = new Configuration();

        Map hdfsConfig = subConfig(configs, configName, HADOOP_TOP_CONFIG);
        if (hdfsConfig != null && hdfsConfig.containsKey(RESOURCE_XML_CONFIG)) {
            checkState(hdfsConfig.get(RESOURCE_XML_CONFIG) instanceof Collection);
            for (Object resource : (Collection)hdfsConfig.get(RESOURCE_XML_CONFIG)) {
                configuration.addResource(new Path(resource.toString()));
            }
        }

        configuration.setBoolean("fs.hdfs.impl.disable.cache", true);
        configuration.setBoolean("fs.file.impl.disable.cache", true);

        return configuration;
    }

    public static FileSystem getFileSystem(String configName, Map configs) throws IOException {
        checkNotNull(configName);

        if (isLocal(configName, configs)) {
            Configuration configuration = getConfiguration(configName, configs);
            configuration.set(FS_DEFAULT_NAME_KEY, FS_DEFAULT_NAME_DEFAULT);
            return FileSystem.getLocal(configuration);
        } else {
            return FileSystem.get(getConfiguration(configName, configs));
        }
    }

    private static boolean isLocal(String configName, Map configs) {
        checkNotNull(configName);

        Map hdfsConfig = subConfig(configs, configName, HADOOP_TOP_CONFIG);
        return hdfsConfig != null
                && hdfsConfig.containsKey(LOCAL_CONFIG)
                && TRUE.toString().equalsIgnoreCase(hdfsConfig.get(LOCAL_CONFIG).toString());
    }

    private static Map subConfig(Map configs, String... path) {
        //If no config or path, just return null
        if (configs == null || path == null)
            return null;

        //if no more path elements, just return the config
        if (path.length == 0)
            return configs;

        //If the top config path is not valid it can't continue.
        if (!configs.containsKey(path[0]) || !(configs.get(path[0]) instanceof Map))
            return null;

        //Recursively walk path to get the next portion of the path from the configuration.
        return subConfig((Map) configs.get(path[0]), copyOfRange(path, 1, path.length));
    }
}
