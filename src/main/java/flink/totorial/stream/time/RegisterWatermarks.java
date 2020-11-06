package flink.totorial.stream.time;

import flink.totorial.FileUtil;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Random;

/**
 * 2019/11/14
 * aven.wu
 * danxieai258@163.com
 */
public class RegisterWatermarks {

    public static void main(String[] args) throws Exception{
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<MyEvent> withTimestampsAndWatermarks = env.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> ctx) throws Exception {
                InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream("files/event.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    Thread.sleep(100);
                    ctx.collect(line);
                }

            }

            @Override
            public void cancel() {

            }
        }).map(x -> {
            String[] str = x.split(",");
            MyEvent event = new MyEvent();
            event.count = Integer.parseInt(str[0]);
            event.group = str[1];
            event.severity = str[2];
            event.eventTime = new Timestamp(System.currentTimeMillis() - new Random().nextInt(5000));
            return event;
        }).assignTimestampsAndWatermarks(new MyTimestampsAndWatermarks());

        withTimestampsAndWatermarks
                .keyBy(MyEvent::getGroup)
                .timeWindow(Time.seconds(10))
                .reduce(MyEvent::add)
                .print();

        env.execute();
    }

    public static class MyTimestampsAndWatermarks implements AssignerWithPeriodicWatermarks<MyEvent> {

        private Watermark current;

        long currentMaxTimestamp = 0L;
        long maxOutOfOrderless = 5000L;
        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            current = new Watermark(currentMaxTimestamp - maxOutOfOrderless);
            return current;
        }

        @Override
        public long extractTimestamp(MyEvent element, long previousElementTimestamp) {
            long timeStamp = element.eventTime.getTime();
            currentMaxTimestamp = Math.max(timeStamp, currentMaxTimestamp);
            return timeStamp;
        }
    }

    public static class MyEvent {
        public long count;

        public String group;

        public String severity;

        public Timestamp eventTime;

        @Override
        public String toString() {
            return "MyEvent{" +
                    "count=" + count +
                    ", group='" + group + '\'' +
                    ", severity='" + severity + '\'' +
                    ", eventTime=" + eventTime +
                    '}';
        }

        public long getCount() {
            return count;
        }

        public String getGroup() {
            return group;
        }

        public String getSeverity() {
            return severity;
        }

        public MyEvent add(MyEvent event) {
            this.count += event.count;
            return this;
        }
    }
}
