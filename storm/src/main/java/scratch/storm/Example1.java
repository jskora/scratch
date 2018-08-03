package scratch.storm;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.shade.com.google.common.base.Strings;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class Example1 {

    private static final Integer MAXWORDS = 4;

    public static void main(String[] args) {
        log.info("**********************");
        log.info("*** Example1.run() ***");
        log.info("**********************");
        new Example1().run();
    }

    private void run() {
        Config config = new Config();
        config.setDebug(false);
        config.setNumWorkers(1);

        TopologyBuilder builder = new TopologyBuilder();
        WordSpout wordSpout = new WordSpout();
        DumpBolt dumpBolt = new DumpBolt();

        builder.setSpout("wordspout", wordSpout, 1);
        builder.setBolt("dumps", new DumpBolt(), 1).shuffleGrouping("wordspout");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", config, builder.createTopology());

        Utils.sleep(6000);

        cluster.shutdown();
    }

    public static class WordSpout extends BaseRichSpout {
        private List<String> wordList = Arrays.asList("one", "two", "three", "four", "five", "six", "seven");
        private SpoutOutputCollector _collector;
        private Integer index;

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            _collector = spoutOutputCollector;
            index = 0;
        }

        @Override
        public void nextTuple() {
            Utils.sleep(100);
            if (index < MAXWORDS) {
                final String word = Strings.padEnd(wordList.get(index++ % wordList.size()), 5, ' ');
                log.info("emitting word={} id={} max={}", word, index);
                _collector.emit(new Values(word));
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }

    public static class DumpBolt extends BaseRichBolt {
        private int i = 0;
        private transient OutputCollector _collector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            _collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            i++;
            final String word = tuple.getStringByField("word");
            log.info("dumping word={} i={}", word, i);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields());
        }
    }
}
