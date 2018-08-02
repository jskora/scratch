package scratch.storm;

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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Example1 {

    public static void main(String[] args) {
        new Example1().run();
    }

    void run() {
        Config config = new Config();
        config.setDebug(false);
        config.setNumWorkers(1);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("words", new WordSpout().setMax(4), 1);
        builder.setBolt("dumps", new DumpBolt(), 1)
                .shuffleGrouping("words");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", config, builder.createTopology());

        Utils.sleep(6000);

        cluster.shutdown();
    }

    public static class DumpBolt extends BaseRichBolt {

        static final long serialVersionUID = 2L;

        private transient OutputCollector _collector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            _collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            System.err.println(tuple.getMessageId() + " = " + tuple.getValueByField("word"));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields());
        }
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
                _collector.emit(new Values(words.get(index++ % words.size())));
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }

}
