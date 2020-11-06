package flink.totorial.stream.asyncio;


import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * 2020/3/21
 * aven.wu
 * danxieai258@163.com
 */
public class AsyncFunctionExample extends RichAsyncFunction<String, String> {
    private transient DataSource dataSource = null;

    @Override
    public void open(Configuration parameters) throws Exception {
//        dataSource = new DruidDataSource();
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUsername("root");
//        dataSource.setPassword("123456");
//        dataSource.setUrl("jdbc:mysql://localhost:3306/day01?characterEncoding=utf8");
    }


    @Override
    public void asyncInvoke(String input, ResultFuture<String> resultFuture) throws Exception {
//        String sql = "SELECT id, name FROM orde WHERE id = ?";
//        String result = null;
//        Connection connection = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        try {
//            connection = dataSource.getConnection();
//            stmt = connection.prepareStatement(sql);
//            stmt.setString(1, param);
//            rs = stmt.executeQuery();
//            while (rs.next()) {
//                result = rs.getString("name");
//            }
//        } finally {
//            if (rs != null) {
//                rs.close();
//            }
//            if (stmt != null) {
//                stmt.close();
//            }
//            if (connection != null) {
//                connection.close();
//            }
//        }
//        resultFuture.complete(Collections.singleton(result));
    }

    @Override
    public void timeout(String input, ResultFuture<String> resultFuture) throws Exception {

    }
}
