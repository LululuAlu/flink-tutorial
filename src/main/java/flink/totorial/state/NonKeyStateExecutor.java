package flink.totorial.state;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 2019/11/23
 * aven.wu
 * danxieai258@163.com
 */
public class NonKeyStateExecutor {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.fromElements(Tuple2.of("1", 3), Tuple2.of("1", 5), Tuple2.of("1", 7), Tuple2.of("1", 4), Tuple2.of("1", 2))
                .addSink(new NonKeyStateBufferingSink(3));
        env.execute();
    }
}
