package flink.totorial.stream.operate;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 2019/11/26
 * aven.wu
 * danxieai258@163.com
 */
public class MapFunctionExample {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Long> stream = env.fromElements(5L,6L,8L,100L);
        stream.map(new MyMapFunction()).print();
        env.execute();
    }
    // 输入一个Long 类型元素，输出tuple
    public static class MyMapFunction implements MapFunction<Long, Tuple2<String, Long>> {
        @Override
        public Tuple2<String, Long> map(Long value) throws Exception {
            return Tuple2.of(String.valueOf(value), value);
        }
    }
}
