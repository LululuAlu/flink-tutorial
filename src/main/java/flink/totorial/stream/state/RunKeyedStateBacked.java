package flink.totorial.stream.state;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

/**
 * 2020/11/10
 * aven.wu
 * danxieai258@163.com
 */
public class RunKeyedStateBacked {

    private static String stateUrl = "hdfs://1.server1:8020/com/dbappsecurity/flink/state-backend-test";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(60 * 1000);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(50 * 1000);
        StateBackend stateBackend = new FsStateBackend(stateUrl);
        env.setStateBackend(stateBackend);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        Properties consumerProperties = new Properties();
        consumerProperties.setProperty("group.id", "stateBackend");
        consumerProperties.setProperty("max.partition.fetch.bytes", "10485760");
        consumerProperties.setProperty("max.poll.records", "50000");
        consumerProperties.setProperty("auto.offset.reset", "earliest");
        consumerProperties.setProperty("bootstrap.servers", "1.kafka1:9092,2.kafka1:9092,3.kafka1:9092");

        FlinkKafkaConsumer010<String> consumer = new FlinkKafkaConsumer010<>("rubbish", new SimpleStringSchema(), consumerProperties);

        DataStream<String> dataStream = env.addSource(consumer);

        DataStream<Tuple4<String, Integer, Integer, String>> user = dataStream.map(new MapFunction<String, Tuple4<String, Integer, Integer, String>>() {
            @Override
            public Tuple4<String, Integer, Integer, String> map(String value) throws Exception {
                JSONObject j = JSONObject.parseObject(value);
                return Tuple4.of(j.getString("name"), j.getIntValue("age"), j.getIntValue("count"), j.getString("timestamp"));
            }
        });

        user.keyBy(0).window(TumblingProcessingTimeWindows.of(Time.seconds(5))).sum(1).print();
        env.execute();
    }
}
