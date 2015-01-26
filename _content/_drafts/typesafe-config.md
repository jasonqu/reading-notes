# Typesafe Config介绍

### Why Typesafe Config

**THE config library** 非常好用的API，用过之后不会再想使用其他配置工具

* 纯java实现，无任何依赖
* 支持各种格式配置的融合: Java properties, JSON, and a human-friendly JSON superset
* 可以通过文件、urls、classpath加载配置
* 支持多层嵌套的配置方式：树形配置
* 识别Java system properties, 如java -Dmyapp.foo.bar=10
* 可以转换时间，大小等单位。如 "512k"、"10 seconds"
* 类型转换，比如yes可以转换为true，数字之间也可以在内部做转换
* JSON superset features:
  * comments
  * includes
  * substitutions ("foo" : ${bar}, "foo" : Hello ${who})
  * properties-like notation (a.b=c)
  * less noisy, more lenient syntax
  * substitute environment variables (logdir=${HOME}/logs)
* 基于不可变对象，不用担心多线程问题

#### 获取

使用gradle

    compile 'com.typesafe:config:1.2.1'

### HOCON (Human-Optimized Config Object Notation)

config使用的是HOCON的文件格式，这种文件格式类似于json，很灵活但没有歧义，能引用，能替换，能注释，能兼容老的properties等格式

The following features are desirable, to support human usage:

* less noisy / less pedantic syntax
* ability to refer to another part of the configuration (set a value to another value)
* import/include another configuration file into the current file
* a mapping to a flat properties list such as Java's system properties
* ability to get values from environment variables
* ability to write comments

约束：utf8编码

#### 注释

	# 单行注释
	a = 1 // 单行注释
	b = 2 # 单行注释
	c = [3, # 行内注释 # 4]

#### 赋值与嵌套

##### 普通赋值

	a : 1
	a = 1

##### （对象）嵌套赋值

	{
	    "foo" : { "a" : 42 },
	    "foo" : { "b" : 43 }
	}
	
	{
	    "foo" : { "a" : 42, "b" : 43 }
	}
	
	foo.bar : 42
	foo { bar : 42 }
	
	foo.bar.baz : 42
	foo { bar { baz : 42 } }
	
	a.x : 42, a.y : 43
	a { x : 42, y : 43 }

##### 多种格式

	{
	    "foo" : {
	        "bar" : 10,
	        "baz" : 12
	    }
	}
	
	foo : {
	    bar : 10,
	    baz : 12
	}
	
	foo {
	    bar = 10
	    baz = 12
	}
	
	foo.bar=10
	foo.baz=12
	
	foo.bar=10, foo.baz=12

##### 多行赋值

	logo = """hello
	world"""

##### 数组与对象合并

	// one object
	a : { b : 1, c : 2 }
	// two objects that are merged via concatenation rules
	a : { b : 1 } { c : 2 }
	// two fields that are merged
	a : { b : 1 }
	a : { c : 2 }
	
	// one array
	a : [ 1, 2, 3, 4 ]
	// two arrays that are concatenated
	a : [ 1, 2 ] [ 3, 4 ]
	// a later definition referring to an earlier
	// (see "self-referential substitutions" below)
	a : [ 1, 2 ]
	a : ${a} [ 3, 4 ]
	
	path = [ /bin ]
	path = ${path} [ /usr/bin ]

##### 对象的继承

	data-center-generic = { cluster-size = 6 }
	data-center-east = ${data-center-generic} { name = "east" }

##### 合并还是覆盖

	{ a : { x : 1 } } (first priority)
	{ a : 42 } (fallback)
	{ a : { y : 2 } } (another fallback)
	# result in { a : { x : 1 } }
	
	{ a : { x : 1 } } (first priority)
	{ a : { y : 2 } } (fallback)
	{ a : 42 } (another fallback)
	# result in { a : { x : 1, y : 2 } }

#### 替换

使用这样的语法 `${pathexpression} or ${?pathexpression}` 


	animal.favorite = dog
	key : ${animal.favorite} is my favorite animal

`pathexpression`是绝对路径，替换是config对象构建的最后一步。

#### Include

	include "foo"
	
	include "foo.properties"
	include "foo.json"
	include "foo.conf"

#### 内置转换API

自动转换，[时间单位](https://github.com/typesafehub/config/blob/master/HOCON.md#duration-format)（ns、ms、s、m、h etc），[文件大小](https://github.com/typesafehub/config/blob/master/HOCON.md#size-in-bytes-format)（kB、MB etc）

### 文件载入顺序

依次载入合并

* 引用 jar 包中的 reference.conf 库引用配置
* 引用 jar 包中的 application.{conf,json,properties} 应用配置
* 本地 reference.conf 库引用配置
* 本地 application.{conf,json,properties} 应用配置
* system properties

### 在java代码中使用

	# 配置内容
	foo=42
	dev.foo=57
	prod.foo=10

	import com.typesafe.config.ConfigFactory
	
	// 载入根配置
	Config conf = ConfigFactory.load();
	int foo1 = conf.getInt("dev.foo");
	
	// 载入某个对象
	Config prod = conf.getConfig("prod");
	int foo2 = foo.getInt("foo");
	
	Config devConfig = conf
	                     .getConfig("dev")
	                     .withFallback(originalConfig)
	
	// handle default
	// boolean getBoolean(String path, boolean fallback)


### 调试

使用下面的方法，会把最终所有的配置信息打印出来

	logger.debug(myConfig.root().render())

输出结果中包含所有载入的配置，如果使用了conf文件，会将对应文件的行信息也打印出来：

	{
	    # system properties
	    "awt" : {
	        # system properties
	        "toolkit" : "sun.awt.windows.WToolkit"
	    },
	    # complex1.conf: 2
	    # these are our own config values defined by the app
	    "complex-app" : {
	        # complex1.conf: 3
	        "something" : "This value comes from complex-app's complex1.conf"
	    },
	    ...
	}

### 源代码

* https://github.com/typesafehub/config