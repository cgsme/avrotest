# avrotest
##### 编译schema为java类（代码生成）：
    
     1. 指定目录：
        java -jar C:\Users\cguisheng\Desktop\avro-tools-1.8.2.jar compile schema D:\workspaces\avrotest\avro\user.avsc D:\workspaces\avrotest\src\main\java\       
        
     2. 当前目录（会根据schema中定义的namespace生成对应的包路径）：
        java -jar C:\Users\cguisheng\Desktop\avro-tools-1.8.2.jar compile schema D:\workspaces\avrotest\avro\user.avsc .

**注意**: 如果使用**avro maven**插件的话，无需手动执行schema编译，maven编译时插件会自动对已经配置的资源目录中的.avsc文件执行代码生成。

