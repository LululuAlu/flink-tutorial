package flink.totorial.stream.operate;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 2020/11/10
 * aven.wu
 * danxieai258@163.com
 */
public class StreamJoin {

    public static void main(String[] args) {
        DataStream<Integer> orangeStream = null;
        DataStream<Integer> greenStream = null;

        orangeStream.join(greenStream)
                .where(x -> x)
        .equalTo(x -> x)
        .window(TumblingProcessingTimeWindows.of(Time.milliseconds(2)))
                .apply(new JoinFunction<Integer, Integer, String>() {
                    @Override
                    public String join(Integer first, Integer second) {
                        return first + "," + second;
                    }
                });
    }
}
