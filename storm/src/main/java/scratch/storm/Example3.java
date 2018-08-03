package scratch.storm;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Example3 {

    private static final Integer MAXWORDS = 35;

    public static void main(String[] args) {
        log.info("**********************");
        log.info("*** Example3.run() ***");
        log.info("**********************");
        new Example3().run();
    }

    private static final AtomicInteger fire = new AtomicInteger(0);

    private void run() {
        Config config = new Config();
        config.setDebug(false);
        config.setNumWorkers(1);

        TopologyBuilder builder = new TopologyBuilder();
        SignalSpout signalSpout = new SignalSpout();
        WordSpout wordSpout = new WordSpout().setSignalSpout(signalSpout);
        WordCounterBolt wordCounterBolt = new WordCounterBolt();
        DumpBolt dumpBolt = new DumpBolt();

        builder.setSpout("wordspout", wordSpout, 1);
        builder.setSpout("signalspout", signalSpout, 1);
        builder.setBolt("wordcounterbolt", wordCounterBolt, 1).shuffleGrouping("wordspout").allGrouping("signalspout");
        builder.setBolt("dumps", dumpBolt, 1).shuffleGrouping("wordcounterbolt");
        StormTopology topology = builder.createTopology();

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", config, topology);

        Utils.sleep(5000);

        cluster.killTopology("test");

        cluster.shutdown();
    }

    public static class SignalSpout extends BaseRichSpout {

        private SpoutOutputCollector _collector;

        @Override
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            _collector = collector;
        }

        @Override
        public void nextTuple() {
            Utils.sleep(50);
            log.info("fire={} {}", fire.get(), this);
            if (fire.get() == 1) {
                fire.set(0);
                log.info("RESETCOUNT");
                _collector.emit(new Values("resetCount"));
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("action"));
        }
    }

    public static class WordSpout extends BaseRichSpout {
        private List<String> wordList = Arrays.asList("one", "two", "three", "four", "five", "six", "seven");
        private SpoutOutputCollector _collector;
        private Integer index;
        private Map<Object, String> inFlight = new HashMap<>();
        private SignalSpout _signalSpout;

        WordSpout setSignalSpout(final SignalSpout signalSpout) {
             _signalSpout = signalSpout;
             log.info("setSignalSpout = {}", _signalSpout);
             return this;
        }

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
            if (index % 15 == 0) {
                log.info("setting FIRE");
                fire.set(1);
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
            try {
                if (tuple.getSourceComponent().equals("signalspout")) {
                    log.info("counting SIGNALS");
                    String action = tuple.getStringByField("action");
                    if ("resetCount".equals(action)) {
                        log.info("counting RESETCOUNT");
                        counts.clear();
                    } else {
                        log.error("unexpected signal {}", action);
                        throw new RuntimeException("unexpected signal");
                    }
                } else {
                    String word = tuple.getStringByField("word");
                    Integer occurence = counts.getOrDefault(word, 0) + 1;
                    log.info("counting word={} n={}", word, occurence);
                    counts.put(word, occurence);
                    _collector.emit(tuple, new Values(word, occurence));
                    _collector.ack(tuple);
                }
            } catch (IllegalArgumentException ignore) {
            }

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
