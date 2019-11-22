package flink.totorial.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * 2019/11/21
 * aven.wu
 * danxieai258@163.com
 */
public class KeyedDescriptionFlatMapFunction extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Long>> {

    private transient ValueState<Tuple2<Long, Long>> sum;

    @Override
    public void flatMap(Tuple2<Long, Long> value, Collector<Tuple2<Long, Long>> out) throws Exception {
        Tuple2<Long, Long> currentSum = sum.value();
        //手动判空
        if (currentSum == null) {
            currentSum = new Tuple2<>(0L, 0L);
        }
        currentSum.f0 += 1;

        currentSum.f1 += value.f1;
        // valueState
        sum.update(currentSum);
        if (currentSum.f0 >= 2) {
            out.collect(new Tuple2<>(value.f0, currentSum.f1 / currentSum.f0));
            sum.clear();
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        StateTtlConfig ttlConfig = StateTtlConfig
                .newBuilder(Time.seconds(1))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();

        ValueStateDescriptor<Tuple2<Long, Long>> descriptor =
                new ValueStateDescriptor<>(
                        "average", // the state name
                        TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {}) // type informatio
                ); // default value of the state, if nothing was set
        // 设置TTL
        descriptor.enableTimeToLive(ttlConfig);
        sum = getRuntimeContext().getState(descriptor);
    }
}
