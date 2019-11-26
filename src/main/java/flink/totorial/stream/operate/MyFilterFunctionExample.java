package flink.totorial.stream.operate;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 2019/11/26
 * aven.wu
 * danxieai258@163.com
 */
public class MyFilterFunctionExample {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Long> stream = env.fromElements(5L,6L,8L,100L,101L,54561L);
        stream.filter(new MyFilterFunction()).print();
        env.execute();
    }

    public static class MyFilterFunction implements FilterFunction<Long> {
        @Override
        public boolean filter(Long value) throws Exception {
            return value % 2 != 0;
        }
    }
}
