package com.linewell.test;

import com.linewell.avro.User;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        User user1 = new User();
        user1.setName("Alyssa");
        user1.setFavoriteNumber(256);
        // favoriteColor = null

        // 通过构造函数
        User user2 = new User("Ben", 7, "red");

        // 通过build构造，会自动设置默认值
        User user3 = User.newBuilder()
                .setName("charlie")
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
