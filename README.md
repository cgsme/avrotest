# avrotest
---
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
avro模式使用json定义，模式由简单类型（null, boolean, int, long, float, double, bytes, and string）、<br>
和复杂类型（record, enum, array, map, union, and fixed）组成

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
        // favorite color 为null
        
        GenericRecord user2 = new GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");