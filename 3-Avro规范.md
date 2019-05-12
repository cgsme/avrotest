## avro规范
[官方规范原文](http://avro.apache.org/docs/current/spec.html)
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
* string: unicode字符序列<br>
  基本类型没有指定的属性。<br>
  基本类型名称也是定义的类型名称。因此例如，schema的"string"等效于{"type": "string"}。
        
#### 复杂类型
avro支持6种复杂类型：records, enums, arrays, maps, unions 和 fixed。<br>

_Records_:<br>
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
        "symbols": ["黑桃", "红桃", "方块", "梅花"]
    }
    
  Arrays
  数组使用type名为"array"并支持以下属性：
  * items: 数组的元素的schema
  
  例子，一个string数组可以被定义为：
  `{
        "type": "array",
        "items": "string"
   }`
    
  
  Maps
  Maps使用类型名称“map”表示，并支持以下一个属性：
  * values：map的值的schema。
  
  例如，从string到long的映射可以声明为： `{"type":"map", "values":"long"}`
  
  Unions
  如上所述，Unions使用Json数组表示。例如，["null"，"string"]声明一个可以是null或string的schema。<br>
  （注意，当为类型为union的记录字段指定默认值时，默认值的类型必须与union的第一个元素匹配。因此，对于包含"null"的联合，通常首先会列出"null"，因为此类联合的默认值通常为null。）<br>
  除了命名类型record，fixed和enum之外，Unions可能不包含多个具有相同类型的模式。例如，不允许包含两种数组类型或两种映射类型的Unions，但允许使用两种具有不同名称的类型。<br>
  工会可能不会立即包含其他unions。
  
  Fixed
  Fixed使用类型名称"fixed"并支持两个属性：
  * name: 名称（必须）
  * namespace, 限定名称的字符串（可选）
  * aliases: 一个JSON字符串数组，为此枚举提供备用名称（可选）。
  * size: 一个整数，指定每个值的字节数（必须）。
  
  例如，可以声明16字节数量为：
  
    {"type": "fixed", "size": 16, "name": "md5"}
    
    
    
#### Names
  Record，enums和fixed都是命名类型。每个都由一个由两部分组成的全名：name和namespace。名称的相等性在全名上定义。<br>
  全名和记录字段的名称部分和枚举符号的名称部分必须满足：
  * 由[A-Za-z_]开始
  * 随后只包含[A-Za-z0-9_]
  
  命名空间是这种名称的点分隔序列。空字符串也可以用作命名空间以指示空命名空间。名称（包括字段名称和枚举符号）以及全名的相等性区分大小写。
  
  在record，enum和fixed的定义中，全名由以下几种方式确定：
  * 指定了name和namespace。例如，可以使用 `"name"："X"，"namespace"："org.foo"` 来表示全名 `org.foo.X.`
  * 
  

