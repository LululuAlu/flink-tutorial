package flink.totorial.stream.operate;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import java.text.SimpleDateFormat;

/**
 * 2019/11/26
 * aven.wu
 * danxieai258@163.com
 */
public class MyRichMapFunctionExample extends RichMapFunction<String, String> {

    // SimpleFormatter 无法被序列化在网络中传播
    private transient SimpleDateFormat simpleFormatter;

    @Override
    public void open(Configuration parameters) throws Exception {
        simpleFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public String map(String value) throws Exception {
        // do something
        return value;
    }
}
