package scratch.storm;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class Example2 implements CommandLineRunner {

    public static void main(String[] args) {
        log.warn("{} starting up", Example2.class.getCanonicalName());
        SpringApplication app = new SpringApplicationBuilder(Example2.class).build();
        app.run(args);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run(String... args) throws Exception {
        Config config = new Config();
        config.setDebug(false);
        config.setNumWorkers(1);

        TopologyBuilder builder = new TopologyBuilder();
        WordSpout wordSpout = new WordSpout().setMax(15);
        WordCounterBolt wordCounterBolt = new WordCounterBolt();

        DumpBolt dumpBolt = new DumpBolt();
        builder.setSpout("words", wordSpout, 1);
        builder.setBolt("wordcounter", wordCounterBolt, 1).shuffleGrouping("words");
        builder.setBolt("dumps", dumpBolt, 1).shuffleGrouping("wordcounter");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", config, builder.createTopology());

        Utils.sleep(30000);

        cluster.shutdown();
    }

    public static class WordSpout extends BaseRichSpout {
        private List<String> words = Arrays.asList("one", "two", "three", "four", "five", "six", "seven");
        private SpoutOutputCollector _collector;
        private int index;
        private Integer _max;

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            _collector = spoutOutputCollector;
            index = 0;
        }

        public WordSpout setMax(Integer max) {
            _max = max;
            return this;
        }

        @Override
        public void nextTuple() {
            Utils.sleep(1000);
            if (index < _max) {
                final String word = words.get(index++ % words.size());
                final String msgid = String.format("ackid=%d",index);
                log.info("emitting messageId={} word='{}' max={}", msgid, word, _max);
                _collector.emit(new Values(word), msgid);
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }

        @Override
        public void ack(Object msgId) {
            log.info("got ACK for id={}", msgId);
        }
    }

    public static class WordCounterBolt extends BaseRichBolt {
        private OutputCollector _collector;
        private Map<String, Integer> counts = new HashMap<>();

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            _collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            Integer occurence = counts.getOrDefault(word, 0) + 1;
            log.info("counting messageId={} word={} n={}",
                    tuple.getMessageId(), word, occurence);
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
            log.info("dumping messageId='{}' i={} word='{}' n='{}'", tuple.getMessageId(), i++,
                    tuple.getStringByField("word"), tuple.getIntegerByField("count"));
            _collector.ack(tuple);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields());
        }
    }
}
