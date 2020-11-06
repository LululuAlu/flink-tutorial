package flink.totorial.stream.state;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * 2020/8/12
 * aven.wu
 * danxieai258@163.com
 */
public class ListStateExample {


    public static class MyListStateMap extends RichMapFunction<String, String> {
        private transient ListState<String> element;


        @Override
        public void open(Configuration parameters) throws Exception {
            System.out.println("call open");
            ListStateDescriptor<String> descriptor =
                    new ListStateDescriptor<>(
                            "average", // the state name
                            TypeInformation.of(String.class)); // default value of the state, if nothing was set
            element = getRuntimeContext().getListState(descriptor);
        }


        @Override
        public String map(String value) throws Exception {
            System.out.println(element.get());
            element.add(value);
            return value;
        }
    }


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> source = env.fromCollection(Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10"));
        source.map(new MyListStateMap()).print();
        env.execute();
    }
}
