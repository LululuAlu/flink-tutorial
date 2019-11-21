## 编程模型
此章编程模式是重点，虽然不涉及代码但非常有必要花时间阅读(2-4)节为重点
### 1 层次抽象(Levels of Abstraction)
![模型](https://ci.apache.org/projects/flink/flink-docs-release-1.9/fig/levels_of_abstraction.svg)  

从底向上，抽象程都由低到高，以下说明了解以下即可。

* **最低层次的抽象仅仅提供了有状态的流**。它通过流程函数嵌入到DataStream API中。它允许用户自由处理来自一个或多个流的事件，并使用一致的容错状态。此外，用户可以注册事件时间和处理时间回调，允许程序实现复杂的计算。


* 第二层**核心层**。实际上，大多数应用程序不需要上层的抽象描述，而是根据核心API (DataStream API(有界/无界流)和DataSet API(有界数据集)进行编程。这些连贯api为数据处理提供了常见的构建块，比如用户指定的各种形式的转换、连接、聚合、窗口、状态等。在这些api中处理的数据类型用各自的编程语言表示为类。  
低层流程功能与DataStream
API集成，使得仅对某些操作进行低层抽象成为可能。DataSet API在有界数据集上提供了额外的原语，比如循环/迭代。


* 再上一层**Table API**是一个以表为中心的声明性DSL，可以动态地更改表(在表示流时)。表API遵循(扩展的)关系模型:表有一个附加的模式(类似于关系数据库中的表)，而API提供了类似的操作，如select、project、join、group-by、aggregate等。表API程序声明性地定义应该执行什么逻辑操作，而不是确切地指定操作代码的外观。虽然表API可以通过各种类型的用户定义函数进行扩展，但是它的表达能力不如核心API，但是使用起来更简洁(需要编写的代码更少)。  
此外Table API程序在执行前还需要经过一个应用规则优化器。可以在表和DataStream/DataSet之间进行无缝转换，允许程序混合Table API和DataStream和DataSet API。


* Flink提供的最高抽象级别是**SQL**。这种抽象在语义和表达方面与Table API类似，用SQL代替table API编程方式。SQL抽象与Table API密切交互，SQL查询可以在Table API定义的表上执行。

### 2 程序和数据流(Programs and Dataflows)

Flink程序的基本构建块是**streams**和**transformations**。(在Flink的DataSet API中使用的数据集也是内部流的——稍后详细介绍)。从概念上讲，流是(无界)一个不间断的数据流，转换是将一个或多个流作为输入，并产生一个或多个输出的操作。
在运行时，Flink程序由流和操作符组成，并被映射到**数据流**(streaming dataflows)上。每个数据流可以由多个源（source），最后汇聚（sink）到一个或多个结果上。数据流类似于任意形状的**有向无环图**(DAGs)。虽然也可以通过迭代构造特殊形式的循环，但为了简单起见，我们在大多数情况下会忽略这一点。

![code](https://ci.apache.org/projects/flink/flink-docs-release-1.9/fig/program_dataflow.svg)

通常，程序中的转换与数据流中的操作符之间是一对一的对应关系。但有时一个转换可能包含多个转换操作符。

source和sink被记录在流连接器和批连接器文档中。Transformations通过++DataStream operate++ 和 ++DataSet transformations++来描述。

### 3 并行数据流 (Parallel Dataflows)

Flink是并行分布式的程序。在执行期间，一个流有一个或多个分区(stream partitions)，每个operate有一个或多个operate子任务。operate子任务彼此独立，并在不同的线程中执行，也可能在不同的机器或容器中执行。

operator subtasks的数量就是这个operate的真实并行度。不同的的操作(operate)会有不同的不同的并行度。

![parallelism](https://ci.apache.org/projects/flink/flink-docs-release-1.9/fig/parallel_dataflow.svg)
Streams可以在两个operator之间一对一的发送数据，也可以对数据进行重新分区:

* **One-to-one** stream(上图中的source和map()操作)保留了元素的分区和顺序。也就是说map()操作的subtask[1]会看到和source操作subtask[1]生成的顺序相同的数据。


* **Redistributing** stream (在上图上面的map()和keyBy/window之间，以及keyBy/window和sink之间)根据所用的操作会改变流的分区，每个operator subtasks将数据发送到不同的target subtasks。例如keyBy()(会根据key重新分区)、broadcast()或rebalance() (随机重新分区)。在重分区中，只能保证每对发送和接收子任务(subtask)之间保持有序(例如map()的subtask[1]和keyBy/window的subtask[2])++*补充解释：map()接收到的和输出的顺序是一致的，keyBy/window顺序是一致的。但由map()输入，从keyBy/window输出就不能保证数据顺序一致*++。因此在本例中，保留了每个键的顺序，但是并行性确实引入了不同键的聚合结果到达接收器的顺序的不确定性。

### 窗口(Windows)
窗口在流计算中是一个很重要的概念。聚合操作(sum,count)在流和批上的工作方式不同，在流计算中流是无限的，无法等数据全部到齐了之后再触发聚合操作。所以必须通过窗口(window)来限定数据的范围，比如计算五分钟内的事件数量，或者100个元素的总和。

Windows可以是时间驱动(例如:每30秒)或数据驱动(例如:每100个元素)。人们通常会区分不同类型的窗口，比如翻滚窗口(tumbling windows)(没有重叠)、滑动窗口(sliding window)(有重叠)和会话窗口(session windows)(未接收到数据的的时间间隔)。

![windows](https://ci.apache.org/projects/flink/flink-docs-release-1.9/fig/windows.svg)

### 时间(Time)
在Stream的程序中提到的时间可以有不同的意义：
* **事件时间(Event Time)**是创建事件的时间。它通常由事件中的时间戳来描述，例如由生产传感器或生产服务附加的时间戳。Flink通过时间戳分配程序访问事件时间戳。


* **接收时间(Ingestion time)**是事件在source进入Flink数据流的时间。*在平时开发的时候很少会用到。*


* **处理时间(Processing Time)**是每个operator的本地时间。

![time](https://ci.apache.org/projects/flink/flink-docs-release-1.9/fig/event_ingestion_processing_time.svg)

### 有状态的操作 Stateful Operations

虽然数据流中大多数只操作事件本身(单条处理)(如事件解析)，但有些操作需要记录多个事件的信息(例如窗口操作)。这些操作称为有状态的。

使用一个嵌入式的key/value存储对象来存储状态。状态是分区和分布式的并且跟有状态的操作绑定。因此，只有在keyBy()函数之后才能访问key/value状态，并且只能访问与当前事件的键相关的值。将流的键和状态对齐，可以确保所有状态更新都是本地操作，从而保证一致性而不增加事务开销。这种对齐还允许Flink重新分配状态并透明地调整流分区。

![state](https://ci.apache.org/projects/flink/flink-docs-release-1.9/fig/state_partitioning.svg)

### 检查点和容错(Checkpoints for Fault Tolerance)

Flink使用流回放和checkpoint的组合实现容错。checkpoint与每个输入流中的特定点以及每个操作符的对应状态相关。数据流可以从检查点恢复，同时通过恢复操作符的状态并从检查点重播事件来保持一致性(exactly-once)。

检查点间隔是一种用恢复时间(需要重播的事件数量)来平衡执行期间的容错开销的方法。

### Batch on Streaming

批处理作为Flink流程序的特殊情况，其中流是有界的(元素的有限数量)(可以理解成为一个有限个元素的大窗口)。数据集在内部被视为数据流。因此，可以将批数据当做流数据来处理，只有少数例外:

* 批处理程序的容错不使用检查点。恢复是通过完全重放流来实现的。这是可能的，因为输入是有界的。这将使成本更接近于恢复，但使常规处理更便宜，因为它避免了检查点。

* DataSet API中的有状态操作使用简化的n-memory/out-of-core 数据结构，而不是key/value索引。

* DataSet API引入了特殊的同步(superstep-based)迭代，这只可能在有界的流上实现。


### 关注我的公众号
了解我的最新动向  
![qrcode_for_gh_eac3d4651e58_344](http://qiniu.lgwen.cn/wechat/qrcode_for_gh_eac3d4651e58_344-416a7124e3704fdf96d24e5c87246e86.jpg)
### 收藏我的个人[博客](http://lgwen.cn)
