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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Example2 {

    private static final Integer MAXWORDS = 15;

    public static void main(String[] args) {
        log.info("**********************");
        log.info("*** Example2.run() ***");
        log.info("**********************");
        new Example2().run();
    }

    private void run() {
        Config config = new Config();
        config.setDebug(false);
        config.setNumWorkers(1);

        TopologyBuilder builder = new TopologyBuilder();
        WordSpout wordSpout = new WordSpout();
        WordCounterBolt wordCounterBolt = new WordCounterBolt();
        DumpBolt dumpBolt = new DumpBolt();

        builder.setSpout("wordspout", wordSpout, 1);
        builder.setBolt("wordcounter", wordCounterBolt, 1).shuffleGrouping("wordspout");
        builder.setBolt("dumps", dumpBolt, 1).shuffleGrouping("wordcounter");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", config, builder.createTopology());

        Utils.sleep(5000);

        cluster.shutdown();
    }

    public static class WordSpout extends BaseRichSpout {
        private List<String> wordList = Arrays.asList("one", "two", "three", "four", "five", "six", "seven");
        private SpoutOutputCollector _collector;
        private Integer index;
        private Map<Object, String> inFlight = new HashMap<>();

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
                inFlight.put(index, word);
                log.info("emitting word={} id={} max={}", word, index, MAXWORDS);
                _collector.emit(new Values(word), index);
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }

        @Override
        public void ack(Object msgId) {
            log.info("got ACK  word={} id={}", inFlight.getOrDefault(msgId, "unknown"), msgId);
        }
    }

    public static class WordCounterBolt extends BaseRichBolt {
        private OutputCollector _collector;
        private Map<String, Integer> counts = new HashMap<>();

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            _collector = outputCollector;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            Integer occurence = counts.getOrDefault(word, 0) + 1;
            log.info("counting word={} n={}", word, occurence);
            counts.put(word, occurence);
            _collector.emit(tuple, new Values(word, occurence));
            _collector.ack(tuple);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
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
            final Integer count = tuple.getIntegerByField("count");
            log.info("dumping  word={} n={} i={}", word, count, i);
            _collector.ack(tuple);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields());
        }
    }
}
