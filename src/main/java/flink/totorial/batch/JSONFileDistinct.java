package flink.totorial.batch;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;

/**
 * 2019/11/26
 * aven.wu
 * danxieai258@163.com
 */
public class JSONFileDistinct {

    public static void main(String[] args) {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);

        DataSet<String> text = env.readTextFile("G:\\develop\\repertory\\flinklocal\\target\\cepdevice.txt");

        Table table = tEnv.fromDataSet(text, "");

        Table result = tEnv.sqlQuery(
                "SELECT SUM(amount) FROM " + table + " WHERE product LIKE '%Rubber%'");


    }
}
