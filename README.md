
# avro
#### avro介绍
Apache avro是一种数据序列化系统。
avro提供：
* 丰富的数据格式
* 紧凑，快速的二进制数据格式
* 容器文件，用于存储持久的数据
* 远程过程调用（RPC）
* 与动态语言(js\php\asp\ruby\python等)的简单集成。读取或写入文件时不需要使用代码生成，也不需要使用或实现RPC协议。代码生成作为可选的优化组合，仅值得在静态语言(java\c++\c#等)下实现。


#### schema（模式）
1. avro依赖与schema。读取Avro数据时，始终存在写入时使用的模式。这允许每个数据写入时不会产生每个值的开销，从而使序列化既快又小。这也方便于使用动态脚本语言，因为数据及其模式完全是自我描述的。
2. 因为avro数据被存储在文件中时，其schema也一同被存储。所以该文件可以被任何程序处理。如果程序读取数据时需要另一种schema也很容易解决，因为这两种schema都存在。
3. 当avro使用在RPC中时，客户端和服务端在连接握手中交换schema（可以优化，对于大多数调用并不会交换schema）由于客户端和服务器都具有另一个完整模式，因此相同命名字段，缺少字段，额外字段等之间的对应关系都可以轻松解决。
4. avro schema使用json定义，这便于在已有json库的语言中实现。


#### 与其他系统进行比较
Avro提供类似于诸如[Thrift](http://thrift.apache.org/)，[Protocol Buffers](http://code.google.com/p/protobuf/)等系统的功能[.Avro](http://thrift.apache.org/)在以下基本方面与这些系统不同。
* 动态类型：Avro不需要生成代码。数据始终伴随着一种schema，该schema允许在不生成代码，静态数据类型等的情况下完全处理该数据。这有助于构建通用数据处理系统和语言。
* 无标记数据：由于在读取数据时存在模式，因此需要进行编码的类型信息少的多，因此序列化大小较小。
* 没有手动分配的字段ID：当schema改变时，处理数据时始终存在旧schema和新schema，因此，可以使用字段名象征性地解决差异。

----



#### 1、下载
###### jar方式
avro有几种实现：C, C++, C#, Java, PHP, Python, Ruby。我们主要使用avro-1.8.2.jar 和 avro-tools-1.8.2.jar（用于代码生成）两个jar包即可。<br>
avro的java实现还依赖了[Jackson](http://wiki.fasterxml.com/JacksonDownload "Jackson")库（core-asl.jar和mapper-asl.jar）

    下载地址：http://avro.apache.org/releases.html
    
###### maven方式

    <dependency>
      <groupId>org.apache.avro</groupId>
      <artifactId>avro</artifactId>
      <version>1.8.2</version>
    </dependency>
          
    <!-- 以下是avro的maven插件 (用于执行代码生成): -->
    <plugin>
      <groupId>org.apache.avro</groupId>
      <artifactId>avro-maven-plugin</artifactId>
      <version>1.8.2</version>
      <executions>
        <execution>
          <phase>generate-sources</phase>
          <goals>
            <goal>schema</goal>
          </goals>
          <configuration>
            <sourceDirectory>${project.basedir}/src/main/avro/</sourceDirectory>
            <outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
          </configuration>
        </execution>
      </executions>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <configuration>
        <source>1.6</source>
        <target>1.6</target>
      </configuration>
    </plugin>

###### 编译源码方式

    方法：https://cwiki.apache.org/AVRO/Build+Documentation
    
    
#### 定义模式（schema）
avro模式使用json定义，模式由简单类型（null, boolean, int, long, float, double, bytes, and string）、和复杂类型（record, enum, array, map, union, and fixed）组成

    当个avsc文件只能包含一个schema定义
    user.avsc:
        {"namespace": "example.avro",
         "type": "record",  // 必须
         "name": "User",    // 必须
         "fields": [    // 通过数组定义，必须
             {"name": "name", "type": "string"},
             {"name": "favorite_number",  "type": ["int", "null"]},  // 类型可以是 int 或 null 之一
             {"name": "favorite_color", "type": ["string", "null"]}  // 类型可以是string 或 null 之一
         ]
        }
     
    namespace和name一起组成全类名：example.avro.User。
    注意：schema中type、name、fields是必须的。而fields中定义的字段必须包含name和type属性，其他属性可选。field的type属性是另一个schema对象。

#### 代码生成
###### 使用代码生成进行序列化或反序列化
* 编译schema为java类（代码生成）：
    
     1. 指定目录：
        java -jar C:\Users\cguisheng\Desktop\avro-tools-1.8.2.jar compile schema D:\workspaces\avrotest\avro\user.avsc D:\workspaces\avrotest\src\main\java\       
        
     2. 当前目录（会根据schema中定义的namespace生成对应的包路径）：
        java -jar C:\Users\cguisheng\Desktop\avro-tools-1.8.2.jar compile schema D:\workspaces\avrotest\avro\user.avsc .

**注意**: 如果使用**avro maven**插件的话，无需手动执行schema编译，maven编译时插件会自动对已经配置的资源目录中的.avsc文件执行代码生成。

* 创建对象的两种方式

    1. 通过构造函数
    2. 通过builder构造器  
    
    
        User user1 = new User();
        user1.setName("Alyssa");
        user1.setFavoriteNumber(256);
        // 此时 color null
        
        // 带参构造
        User user2 = new User("Ben", 7, "red");
        
        // 通过 builder
        User user3 = User.newBuilder()
                     .setName("Charlie")
                     .setFavoriteColor("blue")
                     .setFavoriteNumber(null)
                     .build();
    
    区别：通过构造函数创建对象性能优于builder（builder创建对象实际也是先调用了构造函数创建对象）。通过builder创建对象时，每个属性都必须显式设置，即使该属性为null。
    如：user1的favoriteColor并没有被赋值，但是user3的favoriNumber必须被显式地设置为null否则报错。

* 序列化

        // 序列化 user1, user2 和 user3 到磁盘
        // DatumWriter类：将Java对象转换为内存中的序列化格式
        // SpecificDatumWriter 类：和生成的类（user）一起使用，并从生成的类中提取schema
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
        
        // DataFileWriter类：将序列化记录以及模式写入dataFileWriter.create(user1.getSchema(), new File("users.avro"))方法调用中指定的文件。
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
        dataFileWriter.create(user1.getSchema(), new File("users.avro"));
        
        // 通过调用dataFileWriter.append方法将用户写入文件。
        dataFileWriter.append(user1);
        dataFileWriter.append(user2);
        dataFileWriter.append(user3);
        // 完成写操作时，关闭数据文件。
        dataFileWriter.close();


* 反序列化

        // 从磁盘中反序列化user，与序列化的操作类似
        // SpecificDatumWriter类：将内存中序列化的项转换成我们生成的类的实例
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        // 将userDatumReader和之前生成的users.avro文件传递给DataFileReader，读取磁盘中文件数据
        DataFileReader<User> dataFileReader = new DataFileReader<User>(new File("users.avro"), userDatumReader);
        User user = null;
        while (dataFileReader.hasNext()) {
            // 通过将用户对象传递给next() 来重用它。减少多个项目的文件分配和垃圾回收象。
            user = dataFileReader.next(user);
            System.out.println(user);
        }


###### 不使用代码生成进行序列化或反序列化
    avro中的数据总是和其对应的schema一起存储，说明我们不需要得到schema就可以读取序列化的项。
    所以我们可以在不生成代码的情况下进行序列化和反序列化。
* 创建users  
  
        // 首先使用解析器Parser读取schema定义文件，并创建Schema对象。
        Schema schema = new Schema.Parser().parse(new File("user.avsc"));
        
        // 使用该schema对象创建一些users
        GenericRecord user1 = new GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        // favorite color 设为null，因为该字段的类型为复杂类型["String", null]，即可以二者选一
        
        GenericRecord user2 = new GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");

* 序列化
                
    // 不使用代码生成时进行对象的序列化和反序列化和使用代码生成的情况类似，不同的地方在于此处使用通用的（generic）reader和writer。
       
       // 序列化 user1 和 user2 到磁盘
       File file = new File("users2.avro");
       // 不使用代码生成时，使用GenericDatumWriter：它需要schema来确定如何写入繁星记录，并验证是否存在不为空的字段。
       DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
       // DataFileWriter用于向 dataFileWriter.create(schema, file);中指定的文件中写入序列化记录和schema(模式)
       DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);
       dataFileWriter.create(schema, file);
       // 通过append方法将数据写入文件
       dataFileWriter.append(user1);
       dataFileWriter.append(user2);
       // 关闭资源
       dataFileWriter.close();
       
* 反序列化  

        // 从磁盘反序列化user对象到GenericRecord对象中
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
        GenericRecord user = null;
        while (dataFileReader.hasNext()) {
            // 将user对象传递给dataFileReader.next(user);实现重用，减少垃圾回收，提高性能
            user = dataFileReader.next(user);
            System.out.println(user);
        }
        // 输出：
        // {"name": "Alyssa", "favorite_number": 256, "favorite_color": null}
        // {"name": "Ben", "favorite_number": 7, "favorite_color": "red"}
        
---

# avro规范
#### schema声明
  schema由下面中的一种json格式表示：
  * 命名了一个已经定义了的类型的json字符串
  * 一个格式如下的json对象： <br>
    {"type":"typeName", ...attributes...} <br>
    其中typeName是基本类型或派生类型名称，允许未在文档中定义的属性作为元数据，但是必须不能影响序列号数据的格式。
  * 一个JSON数组，表示嵌入类型的联合。
  
#### 基本类型
  基本类型的名称集合：
    * null: 没有值
    * boolean: 二进制值
    * int: 32位有符号整数
    * long: 64位有符号整数
    * float: 单精度（32位）IEEE 754浮点数
    * double: 双精度（64位）IEEE 754浮点数
    * bytes: 8位无符号字节序列
    * string: unicode字符序列
  基本类型没有指定的属性。<br>
  基本类型名称也是定义的类型名称。因此例如，schema的"string"等效于{"type": "string"}。
  
  
  例子：使用以下内容定义值为64位的链接list：
   
    {
        "type": "record",
        "name": "LongList",     
        "aliases": ["LinkedLongs"],
        "fields": [
            { "name": "value", "type": "long" },   // 每一个都必须是long
            { "name": "next", "type": ["null", "LongList"] }   // 可以为null或LongList
        ]
    }
        
  Enums
  Enums使用type名为"enum"，支持以下属性：
  * name: 提供枚举名称的json字符串（必须）。
  * namespace，一个限定名称（包名）的JSON字符串。
  * aliases: 一个JSON字符串数组，为此枚举提供备用名称（可选）。
  * doc: schema的文档说明（可选）。
  * symbols: 列出json字符串元素的json数组。（必须）枚举中的所有元素都必须是唯一的，每个元素必须与正则表达式[A-Za-z _] [A-Za-z0-9 _] *（与名称相同的要求）匹配。
  
  例子，扑克牌可以定义为：
  
    {
        "type": "enum",
        "name": ""Suit,
        "symbols": ["SPADES", "HEARTS", "DIAMONS", "CLUBS"]
        
    }
    
  Arrays
  数组使用type名为"array"并支持以下属性：
  * items: 数组的元素的schema
  
  例子，一个string数组可以被定义为：
  
    {
        "type": "array",
        "items": "string"
    }
    
    
  