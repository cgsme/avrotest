## avro
-- 
#### avro介绍
Apache avro是一种数据序列化框架，它使用名为schema的JSON文档来描述数据结构。设计用于支持大批量数据交换的应用。

Avro出自Hadoop之父Doug Cutting, 在Thrift已经相当流行的情况下推出Avro，其目标不仅是提供一套类似Thrift的通讯中间件,更是要建立一个新的、标准性的云计算的数据交换和存储的Protocol。
这个和Thrift的理念不同，Thrift出自Facebook用于后台各个服务间的通讯,Thrift的设计强调统一的编程接口的多语言通讯框架。
Thrift认为没有一个完美的方案可以解决所有问题，因此尽量保持一个中立的框架，插入不同的实现并互相交互。
而Avro偏向实用，排斥多种方案带来的可能的混乱，主张建立一个统一的标准，并不介意采用特定的优化。
Avro的创新之处在于融合了显式声明的Schema和高效二进制的数据表达，强调数据的自我描述，克服了以往单纯XML或二进制系统的缺陷。
Avro对Schema动态加载功能，是Thrift编程接口所不具备的，符合了Hadoop上的Hive/Pig及NOSQL 等既属于[ad hoc](https://baike.baidu.com/item/Ad%20hoc/534288?fr=aladdin)（点对点网络模式），
又追求性能的应用需求。

avro提供：
* 丰富的数据格式(规范中介绍)
* 紧凑，快速的二进制数据格式
* 容器文件，用于存储持久的数据
* 远程过程调用（RPC）
* 与动态语言(js\php\asp\ruby\python等)的简单集成。读取或写入文件时不需要使用代码生成，也不需要使用或实现RPC协议。代码生成作为可选的优化组合，仅值得在静态语言(java\c++\c#等)下实现。


#### schema（模式）
1. avro依赖与schema。读取Avro数据时，始终存在写入时使用的模式。这允许每个数据写入时不会产生每个值的开销，从而使序列化既快又小。这也方便于使用动态脚本语言，因为数据及其模式完全是自我描述的。
2. 因为avro数据被存储在文件中时，其schema也一同被存储。所以该文件可以被任何程序处理。如果程序读取数据时需要另一种schema也很容易解决，因为这两种schema都存在。
3. 当avro使用在RPC中时，客户端和服务端在连接握手（handshake）中交换schema（可以优化，对于大多数调用并不会交换schema）由于客户端和服务器都具有另一个完整模式，
因此相同命名字段，缺少字段，额外字段等之间的对应关系都可以轻松解决。
4. avro schema使用json定义，这便于在已有json库的语言中实现。

Avro尽管提供了RPC机制，事实上Avro的核心特性决定了它通常用在“大数据”存储场景（Mapreduce），即我们通过借助schema将数据写入到“本地文件”或者HDFS中，然后reader再根据schema去迭代获取数据条目。
它的schema可以有限度的变更、调整，而且Avro能够巧妙的兼容，这种强大的可扩展性正是“文件数据”存储所必须的。

#### 与其他系统进行比较
Avro提供类似于诸如[Thrift](http://thrift.apache.org/)，[Protocol Buffers](http://code.google.com/p/protobuf/)等系统的功能[.Avro](http://thrift.apache.org/)在以下基本方面与这些系统不同。
* 动态类型：Avro不需要生成代码。数据始终伴随着一种schema，该schema允许在不生成代码，静态数据类型等的情况下完全处理该数据。这有助于构建通用数据处理系统和语言。
* 无标记数据：由于在读取数据时存在模式，因此需要进行编码的类型信息少的多，因此序列化大小较小。
* 没有手动分配的字段ID：当schema改变时，处理数据时始终存在旧schema和新schema，因此，可以使用字段名象征性地解决差异。

与[Thrift](https://www.cnblogs.com/AI001/p/3996846.html)<br>
Avro和Thrift都是跨语言，基于二进制的高性能的通讯中间件. 它们都提供了数据序列化的功能和RPC服务。
总体功能上类似，但是哲学不一样. Thrift出自Facebook用于后台各个服务间的通讯,Thrift的设计强调统一的编程接口的多语言通讯框架。
Avro的推出，其目标不仅是提供一套类似Thrift的通讯中间件更是要建立一个新的，标准性的云计算的数据交换和存储的Protocol。
这个和Thrift的理念不同，Thrift认为没有一个完美的方案可以解决所有问题，因此尽量保持一个Neutral框架，插入不同的实现并互相交互。
而Avro偏向实用，排斥多种方案带来的 可能的混乱，主张建立一个统一的标准，并不介意采用特定的优化。
Avro的创新之处在于融合了显式,declarative的Schema和高效二进制的数据表达，强调数据的自我描述，克服了以往单纯XML或二进制系统的缺陷。
Avro对Schema动态加载功能，是Thrift编程接口所不具备的，符合了Hadoop上的Hive/Pig及NOSQL 等既属于ad hoc，又追求性能的应用需求。

结论：
    Thrift适用于程序对程序静态的数据交换，要求schema预知并相对固定。
    Avro在Thrift基础上增加了对schema动态的支持且性能上不输于Thrift。
    Avro显式地设计schema使它更适用于搭建数据交换及存储的通用工具和平台,特别是在后台。
    目前Thrift的优势在于更多的语言支持和相对成熟。

----

#### 为什么使用Avro
因为他是Hadoop的一个子项目 -_-
As we know，JSON是一种轻量级的数据传输格式，对于大数据集，JSON数据会显示力不从心，因为JSON的格式是key：value型，每条记录都要附上key的名字。
有的时候，光key消耗的空间甚至会超过value所占空间，这对空间的浪费十分严重，尤其是对大型数据集来说，因为它不仅不够紧凑，还要重复地加上key信息，
不仅会造成存储空间上的浪费，更会增加了数据传输的压力，从而给集群增加负担，进而影响整个集群的吞吐量。而采用Avro数据序列化系统可以比较好的解决此问题，
因为用Avro序列化后的文件由schema和真实内容组成，schema只是数据的元数据，相当于JSON数据的key信息，schema单独存放在一个JSON文件中，
这样一来，数据的元数据只存了一次，相比JSON数据格式的文件，大大缩小了存储容量。从而使得Avro文件可以更加紧凑地组织数据。

XML 和 JSON都有很强的表达性，但是他们太大了并且处理比较慢。当处理PB级别的数据，大小和速度很重要。


#### 应用场景
avro相关应用：https://blog.cloudera.com/blog/category/avro/

Nifi -_-
kafka:
    一个主要的痛点可能是协调生产者和消费者之间商定的信息格式。<br>
    案例介绍:
    [第一部分](https://blog.cloudera.com/blog/2018/07/robust-message-serialization-in-apache-kafka-using-apache-avro-part-1/)
    [第二部分](https://blog.cloudera.com/blog/2018/07/robust-message-serialization-in-apache-kafka-using-apache-avro-part-2/)
    [第三部分](https://blog.cloudera.com/blog/2018/08/robust-message-serialization-in-apache-kafka-using-apache-avro-part-3/)<br>
    [demo源码](https://github.com/cloudera/kafka-examples)
    包括：
        SimpleClient-卡夫卡生产商和消费者的最小例子。
        SimpleFlafka-使用Flume和Kafka消费群的简单示例。
        SchemaProvider-使用Apache AVro进行高效序列化和schema版本控制的示例。
        StructuredStreamingRefApp-演示 Kafka-> Spark Structured Streaming -> Kudu pipeline for ingestion
Hive 
    [demo](https://www.iteblog.com/archives/1007.html)
Hadoop（MapReduce、在Hadoop中将Apache Avro数据转换为[Parquet][]格式）

Spark
Pig
Hbase
flume(日志采集、聚合和传输的系统)
...



[Parquet]:https://www.baidu.com/link?url=brMlrPm5Vi4SgWhN1d0p4Dnn9u6SNT72Y0Lulq6BdkM0TFKJP5-pn59sSUHvZBAM&wd=&eqid=b6b01cc40000ba78000000065cdad2a8










---

作者关于Avro的[博文](https://blog.cloudera.com/blog/2009/11/avro-a-new-format-for-data-interchange/)

##### Apache Avro：数据交换的新格式
2009年11月2日作者：Doug Cutting 

Apache Avro是Apache Hadoop系列项目的最新成员。Avro定义了一种旨在支持数据密集型应用程序的数据格式，并以各种编程语言提供对此格式的支持。

背景
我们希望数据驱动的应用程序是动态的：人们应该能够快速组合来自不同来源的数据集。我们希望促进新颖，创新的数据探索。例如，理想情况下，某人应该能够轻松关联销售点交易，网站访问和外部提供的人口统计数据，而无需进行大量准备工作。这应该是使用脚本和交互式工具即时实现的。

目前的数据格式通常不适用于此。XML和JSON是富有表现力的，但它们很大，而且处理起来很慢。当您处理数PB的数据时，大小和速度非常重要。

Google使用名为Protocol Buffers的系统来解决这个问题。（还有其他系统，比如Thrift，类似于Protocol Buffers，我在这里不会明确讨论，但我对Protocol Buffers的评论也适用于此。）Google已经免费提供Protocol Buffers，但它并不适合我们的目的。

通用数据
使用Protocol Buffers，可以定义数据结构，然后生成可以有效读写的代码。但是，如果希望从脚本语言快速实现协议缓冲区数据的实验，首先必须：找到数据结构定义; 为它生成代码; 最后，在可以触摸数据之前加载该代码。这可能不是那么糟糕，但如果想要拥有一个可以浏览任何数据集的通用工具，它必须首先找到定义，然后为每个这样的数据集生成和加载代码。这使得应该简单的事情变得复杂。

相反，Avro的格式始终以易于处理的形式存储数据的数据结构定义。然后，Avro实现可以在运行时使用这些定义以通用方式向应用程序提供数据，而不需要生成代码。

Avro中的代码生成是可选的：在某些编程语言中，有时使用特定的数据结构是很好的，这些数据结构对应于频繁序列化的数据类型。但是，在像Hive和Pig这样的脚本系统中，代码生成将是一种强加，因此Avro不需要它。

将完整数据结构定义与数据一起存储的另一个优点是它允许更快和更紧凑地写入数据。协议缓冲区为数据添加注释，因此即使定义与数据不完全匹配，仍可以处理数据。但是，这些注释会使数据略大且处理速度较慢。  基准测试表明，不需要这种注释的Avro数据比其他序列化系统更小，更快。

Avro Schemas
Avro使用JSON来定义数据结构的架构。例如，二维点可能被定义为Avro记录：

    {
        "type": "record", "name": "Point",
         "fields": [
          {"name": "x", "type": "int"},
          {"name": "y", "type": "int"},
         ]
    }
    
每个实例都被序列化为两个整数，没有额外的每个记录或每个字段的注释。使用可变长度的 Zig-zag编码编写整数。因此，具有小的正值和负值的点可以写入少至两个字节：100个点可能仅需要200个字节。

除记录和数字类型外，Avro还支持数组，映射，枚举，变量和固定长度的二进制数据和字符串。它还定义了一个容器文件格式，旨在为MapReduce和其他分析框架提供良好的支持。有关详细信息，请参阅Avro规范。

兼容性
应用程序不断发展，随着它们的发展，它们的数据结构也会发生变化 我们希望应用程序的新版本仍然能够处理旧版本创建的数据，反之亦然。Avro以与协议缓冲区大致相同的方式处理此问题。当应用程序需要不存在的字段时，Avro会提供在架构中指定的默认值。Avro忽略数据中存在的意外值。这并不能解决所有后向兼容性问题，但它使得最常见的问题易于处理。

RPC
Avro还允许定义远程过程调用（RPC）协议。虽然RPC中使用的数据类型通常与数据集中的数据类型不同，但使用通用序列化系统仍然很有用。数据密集型应用程序需要基于RPC的分布式框架。因此，在我们需要能够处理数据集文件的任何地方，我们还需要能够使用RPC。因此，在公共基础上构建这些可以最小化人们例如能够编写将处理数据但不能使用分布式框架的代码的机会。

与Hadoop集成
我们希望在Hadoop的MapReduce中使用Avro数据很容易。这仍然是一项进展中的工作。问题MAPREDUCE-1126和MAPREDUCE-815跟踪此问题。

请注意，Avro数据结构可以指定其排序顺序，因此在一种编程语言中创建的复杂数据可以按另一种编程语言进行排序。在没有反序列化的情况下也可以进行排序，因此非常快。

我们希望Avro能够取代Hadoop现有的RPC。Hadoop目前要求其客户端和服务器运行完全相同的Hadoop版本。我们希望使用Avro允许一个人，例如，有一个Hadoop应用程序可以与运行不同版本的HDFS和/或MapReduce的多个集群通信。

最后，我们希望Avro允许Hadoop应用程序更容易用Java以外的语言编写。例如，一旦Hadoop基于Avro构建，我们希望以Python，C和C ++等语言支持原生MapReduce和HDFS客户端。

从Hadoop World谈起
我最近在Hadoop World上的演讲中涵盖了许多这些主题，我们很高兴发布该视频以及此博客文章。




