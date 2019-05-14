## avro规范
[官方规范原文](http://avro.apache.org/docs/current/spec.html)

#### schema声明
  schema由下面中的一种json格式表示：
  * 具有一个已经定义了的类型的json字符串
  * 一个格式如下的json对象： <br>
    {"type":"typeName", ...attributes...} <br>
    其中typeName是基本类型或派生类型名称，允许未在文档中定义的属性作为元数据，但是必须不能影响序列号数据的格式。
  * 一个JSON数组，表示嵌入类型的联合（Union）。
  
#### 基本类型
基本类型的名称集合：
* null: 没有值
* boolean: 二进制值
* int: 32位有符号整数
* long: 64位有符号整数
* float: 单精度（32位）IEEE 754浮点数
* double: 双精度（64位）IEEE 754浮点数
* bytes: 8位无符号字节序列
* string: unicode字符序列<br>
  基本类型没有指定的属性。<br>
  基本类型名称也是定义的类型名称。因此例如，schema的"string"等效于{"type": "string"}。
        
#### 复杂类型
avro支持6种复杂类型：records, enums, arrays, maps, union 和 fixed。<br>

_Records_记录:<br>
Records使用record为类型名，并具有3个属性：
* name: 一个json字符串，表示record的名称（必须）
* namespace: 限定名称的json字符串。
* doc: 文档信息，给使用该schema的用户提示。（可选）
* aliases: 给record设置`别名`的json字符串数组。（可选）
* fields: 展示field的json数组，每一个field都是一个json对象，并具有以下属性：<br>
    * name: 描述field名称的json字符串（必须）。
    * doc: 给用户写的field描述的json字符串。（可选）
    * type: 一个定义schema的json对象或者命名记录定义的json字符串。（必须）
    * default: field的默认值，在读取缺少此字段的实例时使用（可选）。根据下表所示，default允许的值取决于field的schema类型。union字段的默认值对应于联合中的第一个架构。<br>
    bytes和fix字段的默认值是Unicode代码点0-255映射到无符号8位字节值0-255的JSON字符串。
    
  例子：使定义值为64位的链接list：
   
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
  
    `{
        "type": "enum",
        "name": ""Suit,
        "symbols": ["黑桃", "红桃", "方块", "梅花"]
    }`
    
  Arrays 数组
  数组使用type名为"array"并支持以下属性：
  * items: 数组的元素的schema
  
  例子，一个string数组可以被定义为：
  `{
        "type": "array",
        "items": "string"
   }`
    
  
  Maps 
  Maps使用类型名称"map"表示，并支持以下一个属性：
  * values：map的值的schema。
  
  例如，从string到long的映射可以声明为： `{"type":"map", "values":"long"}`
  
  Unions
  如上所述，union使用Json数组表示。例如，["null"，"string"]声明一个可以是null或string的schema。<br>
  （注意，当为类型为union的记录字段指定默认值时，默认值的类型必须与union的第一个元素匹配。因此，对于包含"null"的联合，通常首先会列出"null"，因为此类联合的默认值通常为null。）<br>
  除了命名类型record，fixed和enum之外，Unions可能不包含多个具有相同类型的模式。例如，不允许包含两种数组类型或两种map类型的Union，但允许使用两种具有不同名称的类型。<br>
  union可能不会立即包含其他union。
  
  Fixed
  Fixed使用类型名称"fixed"并支持两个属性：
  * name: 名称（必须）
  * namespace, 限定名称的字符串（可选）
  * aliases: 一个JSON字符串数组，为此枚举提供备用名称（可选）。
  * size: 一个整数，指定每个值的字节数（必须）。
  
  例如，可以声明16字节数量为：
  
    {"type": "fixed", "size": 16, "name": "md5"}
    
    
    
#### Names 名称
  Record，enums和fixed都是命名类型。每个都由一个由两部分组成的全名：name和namespace。名称的相等性在全名上定义。<br>
  全名和记录字段的名称部分和枚举符号的名称部分必须满足：
  * 由[A-Za-z_]开始
  * 随后只包含[A-Za-z0-9_]
  
  命名空间（namespace）是这种名称的点分隔序列。空字符串也可以用作命名空间以指示空命名空间。名称（包括字段名称和枚举符号）以及全名的相等性区分大小写。
  
  在record，enum和fixed的定义中，全名由以下几种方式确定：
  * 指定了name和namespace。例如，可以使用 `"name"："X"，"namespace"："org.foo"` 来表示全名 `org.foo.X.`
  * 指定了全名。如果指定的名称包含一个"."，则假定它是一个全名，并且忽略指定的任何命名空间。例如，使用 `"name" ："org.foo.x"` 表示全名org.foo.x。
  * 只指定名称，即不包含"."的名称。在这种情况下，名称空间取自最紧密的封闭模式或协议。例如，如果指定了`"name"："x"`，并且这发生在org.foo.y的记录定义字段中，
  则全名为org.foo.x。如果没有封闭命名空间，则使用空命名空间。
  
对先前定义的名称的引用与上面后两种情况相同：如果它们包含一个 . ，则它们是一个全名；如果它们不包含一个. ，则命名空间是封闭定义的命名空间。

基本类型名称没有命名空间，它们的名称不能在任何命名空间中定义

架构或协议不能包含多个全名的定义。此外，在使用名称之前必须定义它（"before"在深度优先，从左到右遍历JSON解析树，其中协议的types属性始终被视为"before"messages属性）。


#### Aliases 别名
命名类型和字段可能有别名。实现可以选择使用别名将writer的模式映射到reader的模式。这既有助于模式的发展，也有助于处理不同的数据集。

别名通过使用reader模式中的别名`重新编写writer`模式来起作用。例如，如果writer的模式被命名为"foo"，而reader模式被命名为"bar"，并且别名为"foo"，那么在读取时，实现就好像"foo"被命名为"bar"。
同样，如果数据是作为一个名为"x"的字段的记录写入的，并且是作为一个名为"y"、别名为"x"的字段的记录读取的，那么在读取时，实现的作用就像"x"被命名为"y"。

类型别名可以指定为完全限定的命名空间，也可以指定为相对于其别名的命名空间的别名。例如，如果名为"a.b"的类型的别名为"c"和"x.y"，则其别名的完全限定名为"a.c"和"x.y"。

#### 数据序列化
avro数据总是用它的schema序列化。存储avro数据的文件总是在同一个文件中包含该数据的schema。基于avro的远程过程调用（RPC）系统还必须保证数据的远程接收者拥有用于写入该数据的schema副本。

因为在读取数据时，用于写入数据的schema总是存在，所以avro数据本身没有用类型信息标记。解析数据需要schema。

通常，序列化和反序列化都是以[深度优先][]的方式进行的，从左到右遍历模式，在遇到基元类型时对它们进行序列化。

[深度优先]:https://baike.baidu.com/item/%E6%B7%B1%E5%BA%A6%E4%BC%98%E5%85%88%E7%AE%97%E6%B3%95/6909806


#### 编码
avro指定了两个序列化编码：binary和json。大多数应用程序将使用二进制编码，因为它更小、更快。但是，对于调试和基于Web的应用程序，可能JSON编码更合适。

#### 二进制编码
##### 基本类型
基本类型以二进制编码，如下所示：<br>
* null写为0字节。
* boolean被写成一个值为0（假）或1（真）的单字节。
* int和long值是使用[variable-length][] [zig-zag][]编码编写的。例子：
    value	hex
      0	    00
     -1	    01
      1 	02
     -2	    03
      2	    04
           ...
     -64	7f
      64 80 01
           ...
* float 被写入4个字节。将浮点转换为32位整数，使用与Java的floatToIntBits运算相同的方法，然后以小字节格式编码。
* double 写为8个字节。将双元转换成一个64位整数，使用与Java的doubleToLongBits相同的方法，然后以小字节格式编码。
* bytes被编码为一个long，后面跟着许多字节的数据。
* 字符串被编码为一个长的字符串，后面跟着许多字节的UTF-8编码字符数据。 
例如，三个字符串"f o o"将编码为长值3（编码为十六进制06），然后是"f"、"o"和"o"的utf-8编码（十六进制字节66 6f 6f）：06 66 6f 6f

[variable-length]: http://lucene.apache.org/java/3_5_0/fileformats.html#VInt
[zig-zag]: http://code.google.com/apis/protocolbuffers/docs/encoding.html#types

##### 复杂类型
复杂类型以二进制编码，如下所示：<br>

Records:

Record的编码方式是按照字段声明的顺序对其值进行编码。即记录的编码只是其字段编码的串联。字段值根据其schema进行编码。<br>
例如，record schema:

    {
        "type": "record",
        "name": "test",
        "fields" : [
            {"name": "a", "type": "long"},
            {"name": "b", "type": "string"}
        ]
    }
    	  
该record的一个实例，a字段的值为27（编码为hex 36），b字段的值为"foo"（编码为hex bytes 06 66 6f 6f），将简单地编码为这些值的串联，即hex字节序列：36 06 66 6f 6f




Enums

枚举由int编码，表示schema中符号的零基位置。

例如，思考以下枚举：

    {"type": "enum", "name": "Foo", "symbols": ["A", "B", "C", "D"] }
    
这将由一个介于0和3之间的整数进行编码，其中0表示"A"，3表示"D"。




Arrays

数组被编码为一系列块。每个块由一个长的计数值组成，后面跟着许多数组项。计数为零的块表示数组的结尾。每个项都按照数组的项模式进行编码。





#### JSON编码
除了union之外，JSON编码与用于编码字段默认值的编码相同。

union的值在json中编码如下：
* 如果其类型为空，则将其编码为JSON空值。
* 否则，它被编码为具有一个名称/值对的JSON对象，该名称/值对的名称是类型的名称，其值是递归编码的值。对于avro的命名类型（record、fixed或enum），使用用户指定的名称；对于其他类型，则使用类型名称。

例如，union的schema["null"、"string"、"foo"]，其中foo是记录名，将编码：
* null为null
* 字符串"a"作为{"string":"a"}
* foo实例为{"foo":{…}}，其中{…}表示foo实例的JSON编码。
注意，正确处理JSON编码的数据仍然需要schema。例如，JSON编码不区分int和long、float和double、record和map、enums和string等。


#### 单对象编码
在某些情况下，单个avro序列化对象将被存储更长的时间。一个非常常见的例子是在Apache Kafka主题中存储几个星期的avro记录。

在schema更改后的期间内，此持久性系统将包含使用不同schema编写的记录。因此，需要知道使用哪个schema来编写记录以正确支持schema演化。
在大多数情况下，schema本身太大，无法包含在消息中，因此这种二进制包装格式更有效地支持用例。

##### 单对象编码规范



#### 排序方式
avro定义了数据的标准排序顺序。这允许一个系统写入的数据被另一个系统高效地排序。这可能是一个重要的优化，因为排序顺序比较有时是每个对象操作最频繁的。
还要注意的是，AVRO二进制编码的数据可以在不将其反序列化为对象的情况下有效地进行排序。

只有当数据项具有相同的schema时，才可以比较它们。使用schema的深度优先、从左到右遍历递归实现成对比较。遇到的第一个不匹配决定了项的顺序。

根据以下规则比较具有相同schema的两个项目。
* null数据总是相等的。
* boolean数据在true之前以false进行排序
