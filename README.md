# FLINK STREAM
## 基础篇
### 背景
1. Flink 引擎介绍，横向对比（spark，strom）。架构介绍

2. 运行第一个样例程序  
   1. 样例程序
   2. 基本计算流程介绍：source（输入）、operate（算子处理）、sink（输出）
   3. 运行一个example
   4. JAVA Lambda和类型推断

3. Stream 编程模型

4. 时间
    1. 四种时间（接收时间，处理时间，事件时间）
    2. 迟到或数据乱序情况下怎么处理：水印是什么？如何解决问题。

5. 数据类型和序列化[_](https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/types_serialization.html)

6. operate 算子
    1. 基本stream算子：map，flatmap... 
https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/stream/operators/
    2. window 
    3. join
    4. processFunction
    5. Async IO、LRU

7. connector
    1. kafka
    2. hadoop(HDFS)
    3. ElasticSearch


##成长篇
8. side output
    1. split stream

9. table API&SQL API

##未完待续，持续更新

# FLINK BATH


### 关注我的公众号
畅所欲言，共同进步
![qrcode_for_gh_eac3d4651e58_344](http://qiniu.lgwen.cn/wechat/qrcode_for_gh_eac3d4651e58_344-416a7124e3704fdf96d24e5c87246e86.jpg)
