## 编写你的第一个flink程序  
被公认为大数据的hello world程序world count。我们将用flink Stream 编写一个通socket中读取字符并统计出结果。  
创建maven工程，指定JAVA编译版本1.8
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
    </plugins>
</build>
```
引入flinkStream相关依赖
```xml
<dependencies>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-java</artifactId>
        <version>${flink-version}</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-streaming-java_2.11</artifactId>
        <version>${flink-version}</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```
以下是一个统计的例子，你可以复制代码并在本地执行。
```java
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;


public class SocketStreamWordCount {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<String, Integer>> dataStream = env
                .socketTextStream("localhost", 9999)
                .flatMap(new Splitter())
                .keyBy(0)
                .timeWindow(Time.seconds(5))
                .sum(1);

        dataStream.print();

        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }
}
```
使用命令行
`nc -lk 9999` windows需要额外安装[netcat](https://eternallybored.org/misc/netcat/netcat-win32-1.12.zip)并配置环境变量。 使用`nc -l -p 9999` (使用cmd终端启动貌似无法输入数据，我使用git bash启动)启动一个tcp 9999端口用于向程序发送数据。

![tcp](http://lgwen.cn/upload/2019/11/tcp-cea65fe553cf46338ceec74223a97e75.png)

启动程序，并在命令行中输入测试数据。
```
123 abc 你好
我是谁，abc，1
1
ab
c
c
ab
```
查看控制台
![console](http://lgwen.cn/upload/2019/11/console-e0e5164ecb16484f899989d61c22d493.png)

#### 程序解析
Flink 程序是先定义逻辑，直到调用`execute()`方法时才会运行。
* 执行环境  
`StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();`创建flink执行环境。
* Source  
`env.socketTextStream("localhost", 9999)` 定义一个从9999 端口读取数据。
* Operate(DataStream Transformations)
```
socketTextStream("localhost", 9999)
    .flatMap(new Splitter())
    .keyBy(0)
    .timeWindow(Time.seconds(5))
    .sum(1)
```
flink 定义的算子，如果有用过JDK的Stream的同学可能会眼熟这些算子。`timeWindow`表示一个时间窗口，在无限的数据流中通过窗口将数据变为有限的。搭配算子，keyBy，sum等计算。
* Sink  
`dataStream.print()` sink，表示输出。输出有多重flink官方支持了很多，如kafka，filesystem，ES等等。也可以自己定义。之后教程会对常用的算子做介绍。
* Splitter  
我们可以自己定义算子，只需要实现或者继承flink算子接口。例子中的算子`flatMap`根据空格拆分行，并返回多个数据。

#### 将代码发布到集群上运行
maven添加打包插件
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.0.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.5</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
按照上一章的方式启动集群，提交你的第一个程序到集群上运行（记得先启动nc -p -l 9999）。使用命令行  
`./bin/flink run -p 1 -c flink.totorial.wordcount.SocketStreamWordCount ./jars/flink-tutorial-1.0-SNAPSHOT.jar` 提交任务，windows命令相同。在web页面上查看Job
![AAA8BAA4-66C8-4c59-A2D9-5F959589296E](http://lgwen.cn/upload/2019/11/AAA8BAA4-66C8-4c59-A2D9-5F959589296E-13871ba4877d4aa9a281f991b61c1e74.png)

随便在控制台上输入一些数据，可以看到web页面上会有统计。
![A883A6DD-9C39-4e38-BF66-3078A844C17A](http://lgwen.cn/upload/2019/11/A883A6DD-9C39-4e38-BF66-3078A844C17A-ed8aa51d4df04a1999468d797185e9fe.png)


**本教程的所有示例代码都已上传至Github仓库[flink-toturial](https://github.com/LululuAlu/flink-tutorial)**

### 关注我的公众号
了解我的最新动向  
![qrcode_for_gh_eac3d4651e58_344](http://qiniu.lgwen.cn/wechat/qrcode_for_gh_eac3d4651e58_344-416a7124e3704fdf96d24e5c87246e86.jpg)
### 收藏我的个人[博客](http://lgwen.cn)
