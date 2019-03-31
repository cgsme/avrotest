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
          
    以下是avro的maven插件 (用于执行代码生成):
    
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
编译schema为java类（代码生成）：
    
     1. 指定目录：
        java -jar C:\Users\cguisheng\Desktop\avro-tools-1.8.2.jar compile schema D:\workspaces\avrotest\avro\user.avsc D:\workspaces\avrotest\src\main\java\       
        
     2. 当前目录（会根据schema中定义的namespace生成对应的包路径）：
        java -jar C:\Users\cguisheng\Desktop\avro-tools-1.8.2.jar compile schema D:\workspaces\avrotest\avro\user.avsc .

**注意**: 如果使用**avro maven**插件的话，无需手动执行schema编译，maven编译时插件会自动对已经配置的资源目录中的.avsc文件执行代码生成。

* 创建对象的两种方式

    1. 通过构造函数
    2. 通过builder构造器  
    
    区别：通过构造函数创建对象性能优于builder（builder创建对象实际也是先调用了构造函数创建对象）。通过builder创建对象时，
