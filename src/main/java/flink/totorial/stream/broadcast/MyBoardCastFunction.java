package flink.totorial.stream.broadcast;

import flink.totorial.stream.wordcount.SocketStreamWordCount;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 2020/1/19
 * aven.wu
 * danxieai258@163.com
 */
public class MyBoardCastFunction {

    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> source = env
                .socketTextStream("localhost", 9999);
        MapStateDescriptor<String, String > ruleStateDescriptor = new MapStateDescriptor<>(
                "RulesBroadcastState",
                BasicTypeInfo.STRING_TYPE_INFO,
                TypeInformation.of(new TypeHint<String>() {}));

        DataStream<String> dataStream = env
                .socketTextStream("localhost", 10000);

        BroadcastStream<String> broadcastStream = dataStream.broadcast(ruleStateDescriptor);


        source.connect(broadcastStream).process(new BroadcastProcessFunction<String, String, Object>() {

            private transient String value;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                value = "123";
            }

            @Override
            public void processElement(String value, ReadOnlyContext ctx, Collector<Object> out) throws Exception {
                System.out.println("processElement 局部变量value: " + this.value);
            }

            @Override
            public void processBroadcastElement(String value, Context ctx, Collector<Object> out) throws Exception {
                System.out.println("收到广播流数据，内容是：" + value);
                this.value = value;
                System.out.println("更新全局变量");
            }
        }).setParallelism(2);

        env.execute();

    }
}
