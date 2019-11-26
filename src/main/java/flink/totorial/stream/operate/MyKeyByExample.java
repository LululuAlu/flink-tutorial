package flink.totorial.stream.operate;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 2019/11/26
 * aven.wu
 * danxieai258@163.com
 */
public class MyKeyByExample {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Tuple2<String, Integer>> stream = env.fromElements(
                Tuple2.of("2", 2),
                Tuple2.of("2", 2),
                Tuple2.of("2", 3),
                Tuple2.of("1", 21),
                Tuple2.of("4", 4),
                Tuple2.of("4", 4),
                Tuple2.of("7", 8));
        stream.keyBy(0).countWindow(2).sum(1).print();
        env.execute();
    }
}
