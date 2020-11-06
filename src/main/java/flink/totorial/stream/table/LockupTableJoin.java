package flink.totorial.stream.table;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.functions.AsyncTableFunction;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.sources.LookupableTableSource;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * 2020/7/17
 * aven.wu
 * danxieai258@163.com
 */
public class LockupTableJoin {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        // name, age, classId
        DataStream<String> source = env.fromCollection(Arrays.asList(
                "abc,11,1",
                "cde,13,1",
                "aaa,15,1",
                "esd,9,2"
        ));

        DataStream<Tuple3<String, Integer, Integer>> tuple4DataStream = source.map(new MapFunction<String, Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> map(String value) throws Exception {
                String[] arrays = value.split(",");
                return new Tuple3<>(arrays[0], Integer.valueOf(arrays[1])
                        , Integer.valueOf(arrays[2]));
            }
        });

        tableEnv.registerDataStream("student", tuple4DataStream, "name, age, classId, proctime.proctime");
        tableEnv.registerTableSource("class", new MyLookupTableSource());
        Table table = tableEnv.sqlQuery(
                "SELECT " +
                    "s.name, s.age " +
                "FROM " +
                    "student as s " +
                    "JOIN class FOR SYSTEM_TIME AS OF s.proctime AS c " +
                    "ON s.classId = c.id");

        tableEnv.toAppendStream(table, new RowTypeInfo(
                TypeInformation.of(String.class), TypeInformation.of(Integer.class))).print();

        env.execute();
    }


    public static class MyTableFunction extends AsyncTableFunction<Row> {

        @Override
        public void open(FunctionContext context) throws Exception {
            super.open(context);
        }


        public void eval(CompletableFuture<Collection<Row>> future, Object... params) {
            System.out.println(params);

            //future.complete(Collections.singleton())
        }

    }


    public static class MyLookupTableSource implements LookupableTableSource<Row> {


        @Override
        public TableFunction<Row> getLookupFunction(String[] lookupKeys) {
            return null;
        }

        @Override
        public AsyncTableFunction<Row> getAsyncLookupFunction(String[] lookupKeys) {
            return new MyTableFunction();
        }

        @Override
        public boolean isAsyncEnabled() {
            return true;
        }

        public DataStream<Row> getDataStream(StreamExecutionEnvironment execEnv) {
            return null;
        }

        @Override
        public DataType getProducedDataType() {
            return DataTypes.ROW(DataTypes.FIELD("id", DataTypes.INT()),
                    DataTypes.FIELD("cname", DataTypes.STRING()));
        }

        @Override
        public TableSchema getTableSchema() {
            return TableSchema.builder()
                    .field("id", DataTypes.INT())
                    .field("cname",DataTypes.STRING())
                    .build();
        }
    }
}
