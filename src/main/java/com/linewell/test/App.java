package com.linewell.test;

import com.linewell.avro.User;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

/**
 * avro测试类
 *
 * @author cguisheng
 * @date 2019-04-10
 */
public class App {


    public static void main(String[] args) {

        // 利用代码生成进行序列化和反序列化
//        withCodeGeneration();

        // 不利用代码生成进行序列化和反序列化
        withoutCodeGeneration();

    }

    // 不利用代码生成进行序列化和反序列化
    public static void withoutCodeGeneration() {
        try {
            // 首先使用解析器Parser读取schema定义文件，并创建Schema对象。
            Schema schema = new Schema.Parser().parse(new File("C:\\Users\\cguisheng\\IdeaProjects\\avrotest\\src\\main\\avro\\user.avsc"));
            // 使用该schema对象创建一些users
            GenericRecord user1 = new GenericData.Record(schema);
            user1.put("name", "Alyssa");
            user1.put("favorite_number", 256);
            // favorite color 设为null，因为该字段的类型为复杂类型["String", null]，即可以二者选一

            GenericRecord user2 = new GenericData.Record(schema);
            user2.put("name", "Ben");
            user2.put("favorite_number", 7);
            user2.put("favorite_color", "red");


            // ============ 序列化begin ==========================
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
            // ============ 序列化end


            // ============ 反序列化begin
            // 从磁盘反序列化user对象
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
            // ============ 反序列化end

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 利用代码生成进行序列化和反序列化
    public static void withCodeGeneration() {
        User user1 = new User();
        user1.setName("曹gs");
        user1.setFavoriteNumber(256);
        // favoriteColor = null

        // 通过构造函数
        User user2 = new User("Ben", 7, "red");

        // 通过build构造，会自动设置默认值
        User user3 = User.newBuilder()
                .setName("曹gssss")
                .setFavoriteColor("blue")
                .setFavoriteNumber(null)
                .build();

        try {
            // ========================= 序列化 begin =================================
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
            // ========================= 序列化 end =================================


            // ========================= 反序列化 begin =================================
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
            // ========================= 反序列化 end =================================
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
