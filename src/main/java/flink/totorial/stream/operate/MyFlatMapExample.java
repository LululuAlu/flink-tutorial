package flink.totorial.stream.operate;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 2019/11/26
 * aven.wu
 * danxieai258@163.com
 */
public class MyFlatMapExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Long> stream = env.fromElements(5L, 6L, 8L, 100L,56L, 22326L, 89230L, 100L);
        stream.flatMap(new MyFlatMap()).print();
        env.execute();
    }
    public static class MyFlatMap implements FlatMapFunction<Long, Long> {
        @Override
        public void flatMap(Long value, Collector<Long> out) throws Exception {
            if (value % 2 == 0) {
                out.collect(value);
            }
        }
    }
}
