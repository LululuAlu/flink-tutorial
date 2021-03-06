package flink.totorial.stream.state;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 2019/11/21
 * aven.wu
 * danxieai258@163.com
 */
public class KeyedFlatMapStateExecute {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.fromElements(Tuple2.of(1L, 3L), Tuple2.of(1L, 5L), Tuple2.of(1L, 7L), Tuple2.of(1L, 4L), Tuple2.of(1L, 2L))
                .keyBy(0)
                .sum(1)
                .flatMap(new KeyedDescriptionFlatMapFunction())
                .print();
        env.execute();
    }
}
