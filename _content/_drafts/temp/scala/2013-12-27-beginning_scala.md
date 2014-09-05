---
layout: post
title: Beginning Scala 学习笔记
tagline: code better
tags : ["beginning scala","scala"]
---

### Introduction

I see that it’s more important for me to take small elements and compose
them together into complex systems. I understand that if a method always returns the same
output given the same input, I can safely glue that function together with other functions into a
very complex structure.

### CHAPTER 1 About Scala and How to Install It

简单讲就是静态语言如scala好于动态语言如ruby。并提供了很多链接，但是这些链接大多都不能用了
http://lampwww.epfl.ch/~odersky/
http://homepages.inf.ed.ac.uk/wadler/
http://www.lexspoon.org/
http://lampwww.epfl.ch/~phaller/
https://sites.google.com/site/burakemir/
http://blogs.msdn.com/b/dsyme/
http://lambda-the-ultimate.org/

http://www.scala-lang.org/community/
http://blog.goodstuff.im/
http://metacircular.wordpress.com/
http://www.joelonsoftware.com/articles/ThePerilsofJavaSchools.html







### CHAPTER 2 Scala Syntax, Scripts, and Your First Scala Programs

#### scalac 和 fsc

> scalac File1.scala File2.scala
> fsc File1.scala File2.scala
fsc is a separate compiler process that continues to run, waiting for new compilation jobs,
even after the compilation process is finished. This results in much faster compilation
times, but if you’re on a machine with limited RAM, you might not want to keep the
compilation process alive.

#### sample sum.scala:

import scala.io._
def toInt(in: String): Option[Int] =
try {
	Some(Integer.parseInt(in.trim))
} catch {
	case e: NumberFormatException => None
}

def sum(in: Iterator[String]) = {
	val ints = in.flatMap(s => toInt(s))
	ints.foldLeft(0)((a, b) => a + b)
}

println("Enter some numbers and press ctrl-D (Unix/Mac) ctrl-C (Windows)")
val input = Source.fromInputStream(System.in)
println("Sum "+sum(input.getLines))

原书例子已经不可运行
在简化
import scala.io._
def toInt(in: String): Option[Int] =
try {
	Some(Integer.parseInt(in.trim))
} catch {
	case e: NumberFormatException => None
}

println("Enter some numbers and press ctrl-D (Unix/Mac) ctrl-C (Windows)")
val input = Source.fromInputStream(System.in)
println("Sum "+input.getLines.flatMap(toInt).foldLeft(0)(_+_))



#### import

import 一个包下的类
import scala.xml._
Import statements are made in the scope of prior imports. The following statement imports the scala.xml.transform package:
import transform._

也可以指明import的类
import scala.collection.mutable.HashMap
import scala.collection.immutable.{TreeMap, TreeSet}
import scala.util.parsing.json.{JSON => JsonParser}   // import and rename



#### Class, Trait, and Object Definition
The advantage of Scala’s object over Java’s static
mechanism is that a Scala object is an instance of a class and can be passed as a parameter
to methods.

class和trait 可以用extends和with进行组合
object也可以extends class和trait

import can be used inside
any code block, and the import will be active only in the scope of that code block.
class Frog {
	import scala.xml._
	def n: NodeSeq = NodeSeq.Empty
}

也可以直接实现scala static import
scala> object Moose {
def bark = "woof"
}
defined module Moose
scala> import Moose._
import Moose._
scala> bark


#### Scala’s Class Hierarchy
In Scala, everything (except a method) is an instance of a class.

Int, Long, Double, Float, Boolean, Char, Short, and Byte. They are all subclasses of AnyVal
Java’s void called Unit, which is also a subclass of AnyVal
None is an Option[Nothing]
Any is the root of the Scala class hierarchy
AnyVal is the root of Scala’s objectification of the JVM’s primitives
AnyRef means the same thing as Java’s Object but has a few extra compiler-managed methods

#### Method Declaration 方法声明

一个简单声明
def foo(a: Int): String = a.toString

You can pass the type of a parameter or the return type as a parameter.

scala> def list[T](p: T): List[T] = p :: Nil
list: [T](p: T)List[T]
scala> list(1)
res1: List[Int] = List(1)
scala> list("hello")
res2: List[String] = List(hello)

the last parameter in the list may be repeated—a variable-length argument
it is a Seq of the type of the variable-length argument

def largest(as: Int*): Int = as.reduceLeft((a, b) => a max b)
A variable-length argument method may be called as follows:
largest(1)
largest(2, 3, 99)
largest(33, 22, 33, 22)

You can mix type parameters with variable-length arguments:
def mkString[T](as: T*): String = as.foldLeft("")(_ + _.toString)

And you can put bounds on the type parameters. In this case, the types that are passed
in must be Number or a subclass of Number:
def sum[T <: Number](as: T*): Double = as.foldLeft(0d)(_ + _.doubleValue)


Methods can be declared within any code scope, except at the top level, where classes,
traits, and objects are declared. Methods can reference any variables in their scope:
def readLines(br: BufferedReader) = {
	var ret: List[String] = Nil
	def readAll(): Unit = br.readLine match {
		case null =>
		case s => ret ::= s ; readAll()
	}
	readAll()
	ret.reverse
}


#### Variable Declaration

lazy会在第一次access的时候赋值

赋值时可以直接使用tuple赋值
val (i1: Int, s1: String) = Pair(33, "Moof")

Variable definitions can be code blocks as well. This comes in handy when defining val
variables, and the logic required to compute the value is non-trivial.
val x3: String = {
	val d = new java.util.Date()
	d.toString()
}

#### P57 call by name 方法的调用方式

方法调用方式除了java提供的 call-by-reference or call-by-value之外还提供了一个新的方式：
call-by-name, which passes a code block
to the callee. Each time the callee accesses the parameter, the code block is executed and
the value is calculated.

Call-by-name allows you to pass parameters that might take a long
time to calculate but may not be used. For example, in a call to the logger you can use callby-
name, and the express to print is only calculated if it’s going to be logged. Call-by-name
also allows you to create flow of control structures such as while, doWhile, and so on.

就是在调用的时候才会计算输入的参数，如果输入参数计算很费时的时候，可以这样使用
比如log的时候，另外也可以用这种方式创建类似while的控制结构
比如一个nano方法，打印并返回当前的nano时间
def nano() = {
	println("Getting nano")
	System.nanoTime
}

然后我们比较一下使用call by name和不知用它的区别
def delayed(t: => Long) = {
	println("In delayed method")
	println("Param: "+t)
	t
}

def notDelayed(t: Long) = {
	println("In not delayed method")
	println("Param: "+t)
	t
}

如果把nano分别传给这两个方法，gettingnano的打印次序是不一样的





#### P58 Functions, apply, update, and Compiler Magic
Scala is a functional language, which means that you can pass functions to methods and
return them from methods and functions. A function is a block of code that takes parameters
and returns a value. In Scala, functions are instances. Given that Scala is constrained
by the Java Virtual Machine, it cannot pass a pointer to an arbitrary block of code, so Scala
implements functions as anonymous inner classes that have a particular interface. When
you pass a function, you’re just passing an object with a certain trait (interface).

The trait that defines functions that take one parameter and return a value is
Function1[A, B]
where A is the parameter type and B is the return type.
All functions have an apply method, which is the method that applies, or invokes, the
function.
Function1.apply(p: A): B

Thus, you can define a method that takes a function and invokes the function with the
parameter 42:
def answer(f: Function1[Int, String]) = f.apply(42)
或
def answer(f: Function1[Int, String]) = f(42)

u(33) = "Hello"
会调用u的update方法

update重载
scala> class Update {
	def update(what: String) = println("Singler: "+what)
	def update(a: Int, b: Int, what:String) = println("2d update")
}
u() = "Foo"
u(3, 4) = "Howdy"


Scala has a shorthand for describing functions, so the following two descriptions
are the same:
Function1[Int, String]
Int => String

def answer(f: Int => String) = f(42)






#### Case Class

case class Stuff(name: String, age: Int)
默认实现了equals toString 等方法，相当于下面的代码：
Case classes can be instantiated without the use of the new statement.

class Stuff(val name: String, val age: Int) {
	override def toString = "Stuff("+name+","+age+")"
	override def hashCode = name.hashCode + age
	override def equals(other: AnyRef) = other match {
		case s: Stuff => this.name == s.name && this.age == s.age
		case _ => false
	}
}
object Stuff {
	def apply(name: String, age: Int) = new Stuff(name, age)
	def unapply(s: Stuff) = Some((s.name, s.age))
}

#### Basic Pattern Matching

Basic Pattern Matching 已经很强大了

Stuff("David", 45) match {
	case Stuff("David", 45) => true
	case _ => false
}

Stuff("David", 45) match {
	case Stuff("David", _) => "David"
	case _ => "Other"
}

Stuff("David", 45) match {
	case Stuff("David", howOld) => "David, age: "+howOld
	case _ => "Other"
}

Stuff("David", 45) match {
	case Stuff("David", age) if age < 30 => "young David"
	case Stuff("David", _) => "old David"
	case _ => "Other"
}

x match {
	case d: java.util.Date => "The date in milliseconds is "+d.getTime
	case u: java.net.URL => "The URL path: "+u.getPath
	case s: String => "String: "+s
	case _ => "Something else"
}
等价于下面的java
if (x instanceOf Date) return "The date in milliseconds is "+((Date) x).getTime();
if (x instanceOf URL) return "The URL path: "+((URL) x).getPath();
if (x instanceOf String) return "String "+((String) x);
return "Something else";

#### if/else and while

【注意】
The result of if and while expressions is always Unit. The result of if/else is based on the type of each part
of the expression.
val i: Int = if (exp) 1 else 3





#### for循环

for {i <- 1 to 3} println(i)
for {i <- 1 to 3; j <- 1 to 3} println(i * j)

def isOdd(in: Int) = in % 2 == 1
for {i <- 1 to 5 if isOdd(i)} println(i)

for {i <- 1 to 5; j <- 1 to 5 if isOdd(i * j)} println(i * j)

val lst = (1 to 18 by 3).toList
for {i <- lst if isOdd(i)} yield i

for {i <- lst; j <- lst if isOdd(i * j)} yield i * j

#### throw, try/catch/finally, and synchronized

The try/catch construct is different. First, it’s an expression that results in a value. This
makes it possible to wrap a call in a try/catch and assign a default value if the call fails.


try {
	file.write(stuff)
} catch {
	case e: java.io.IOException => // handle IO Exception
	case n: NullPointerException => // handle null pointer
}

Here’s an example of calling Integer.parseInt and defaulting to 0 if an exception is
thrown:
try{Integer.parseInt("dog")} catch {case _ => 0}


#### Scala vs. Java vs. Ruby

Scala and Ruby are pure object-oriented languages.
You can define a method that takes a function that transforms an Int to an Int

def with42(in: Int => Int) = in(42)

and pass a function that is applying the + method to 33:

with42(33 +)












### Chapter 3 Collections and the Joy of Immutability


#### Scala List, Tuple, Option, and Map Classes

Range 是延迟初始化的
(1 to Integer.MAX_VALUE - 1).take(5)

#### List[T]

List 方法
初始化 下面三个方法是相同的
1 :: 2 :: 3 :: Nil
new ::(1, new ::(2, new ::(3, Nil)))
List(1,2,3)

List(1, 44.5, 8d) 类型是AnyVal
List[Number](1, 44.5, 8d)

加元素
99 :: x
x ::: y

filter remove
List(1,2,3).filter(x => x % 2 == 1)
List(1,2,3).remove(x => x % 2 == 1)
"99 Red Balloons".toList.filter(Character.isDigit)


take while
"Elwood eats mice".takeWhile(c => c != ' ')

map
List("A", "Cat").map(_.toLowerCase)
List("A", "Cat").map(_.length)
ist(a,d,e).map(n => <li>{n.first}</li>)

sort
List(99, 2, 1, 45).sort(_ < _)

First Name of Valid Persons, Sorted by Age
def validByAge(in: List[Person]) = in.filter(_.valid).
sort(_.age < _.age). map(_.first)

reduce函数
List(8, 6, 22, 2).reduceLeft(_ max _) 
reduceLeft类型和list类型一致
List(1,2,3,4).foldLeft(0) (_ + _)
List(1,2,3,4).foldLeft(1) (_ * _)
foldleft类型和seed类型一致


构造多级List
val n = (1 to 3).toList
n.map(i => n.map(j => i * j))
此时结果是
List[List[Int]] = List(List(1, 2, 3), List(2, 4, 6), List(3, 6, 9))
可以使用flatmap来将多级队列变为一级
n.flatMap(i => n.map(j => i * j))


n.filter(isEven).flatMap(i => n.filter(isOdd).map(j => i * j))
和
for {i <- n if isEven(i); j <- n if isOdd(j)} yield i * j
在bytecode层级上是一样的

例子 罗马数字到阿拉伯数字转化 P66


#### Tuple 元组

def sumSq(in: List[Double]): (Int, Double, Double) =
in.foldLeft((0, 0d, 0d))((t, v) => (t._1 + 1, t._2 + v, t._3 + v * v))
多个返回值的时候很有用

也可以写为这样，更加可读
ef sumSq(in: List[Double]) : (Int, Double, Double) =
in.foldLeft((0, 0d, 0d)){
case((cnt,sum,sq),v)=> (cnt+1,sum+v,sq+v*v)}

二元组 map的元素
scala> Tuple2(1,2) == Pair(1,2) 
scala> Pair(1,2) == (1,2) 
scala> (1,2) == 1 -> 2



#### Map[K, V] 哈希表
var p = Map(1 -> "David", 9 -> "Elwood")
结果是一个Map[Int,String]

修改p 可以用这样的方式
p = p + 8 -> "Archer"
或
p += 8 -> "Archer"
p ++= List(5 -> "Cat", 6 -> "Dog")

直接使用apply方法，在没有值得时候抛出异常
scala> p(9)
res12: java.lang.String = Elwood
scala> p(88)
java.util.NoSuchElementException: key not found: 88

但是使用get方法，如果没有值将得到None，否则是一个Some
scala> p.get(88)
res10: Option[java.lang.String] = None
scala> p.get(9)
res11: Option[java.lang.String] = Some(Elwood)

也可以加一个默认值
scala> p.getOrElse(99, "Nobody")
res55: java.lang.String = Nobody
scala> p.getOrElse(1, "Nobody")
res56: java.lang.String = David

获取一个key集的结果集
scala> 1 to 5 flatMap(p.get)
res53: Seq.Projection[java.lang.String] = RangeG(David)

其他方法
-= --= 是remove
p -= 9
p --= List(8, 6)

contains exists 前面的参数是集合参数，后面是一个bool的方法
p.contains(1)

p.keys.reduceLeft(_ max _)
p.values.reduceLeft(_ max _)

def removeInvalid(in: Map[Int, Person]) = in.filter(kv => kv._2.valid)


#### Option[T]
Option[T] provides a container for zero or one element of a given type. Option provides a very powerful alternative to Java’s null. An Option[T] can be either Some[T] or None. None is an object. There is a single instance of None in your Scala program, so it’s kind of like null. But None has methods on it, so you can invoke map, flatMap, filter, foreach, and so on no matter whether you have Some or None.
是一个有Some或None 对象的类，其中None有各种方法，可以用来避免NPE

如有一个从数据库中获取实体的方法
def findPerson(key: Int): Option[Person]
可以直接有一个获取年龄的方法
def ageFromKey(key: Int): Option[Int] = findPerson(key).map(_.age)
另个方法都是返回Some或None

We can cascade mapping/flatMapping of Option without explicitly testing for
None.

看一下在类型转化上的应用
import java.lang.{Boolean => JBool}
def tryo[T](f: => T): Option[T] = try {Some(f)} catch {case _ => None}

def toInt(s: String): Option[Int] = tryo(s.toInt)
def toBool(s: String) = tryo(JBool.parseBoolean(s))

有了这些helper方法，我们可以这样：
实例 从一个Map获取Person对象 P97 有java代码对比
def personFromParams(p: Map[String, String]): Option[Person] = 
	for {name <- p.get("name")
	ageStr <- p.get("age")
	age <- toInt(ageStr) 
	validStr <- p.get("valid") 
	valid <- toBool(validStr)}
yield new Person(name, age, valid)

In my code, any method that can logically fail (e.g., looking something up in a Map,
converting a String to an Int) returns an Option. It is up to the calling code to determine
what to do.


Some
scala> Some(3).get
res57: Int = 3
But be careful, because if the Option is None, an exception will be raised: 
scala> None.get
java.util.NoSuchElementException: None.get

Like Map, Option has a getOrElse method that returns a default value if the contents are undefined:
scala> Some(3).getOrElse(44)
res59: Int = 3

Option has map, flatMap, filter, and foreach methods


#### XML Creation and Manipulation

XML
scala> <b id="greeting">Hello World</b>
res1: scala.xml.Elem = <b id="greeting">Hello World</b>
scala> <b ns:hi='hello'>Hello</b>
res2: scala.xml.Elem = <b ns:hi="hello">Hello</b>

Scala 将xml表述为Seq[Node]，Node是NodeSeq的超类，而NodeSeq是Seq[Node]的子类。
这样所有的collection方法和for语法都可以用在xml集合上
abstract class Node extends NodeSeq
abstract class NodeSeq extends AbstractSeq[Node] with immutable.Seq[Node] with SeqLike[Node, NodeSeq] with Equality

Scala represents XML as a Seq[Node], and Node is a superclass of NodeSeq, which is a subclass of Seq[Node]. That means all the collections methods that we’ve been exploring are available on XML collections including map, flatMap, filter, and foreach. This also means that XML can be used in for comprehensions. We’ll explore that in the next subsection.
scala> def len(seq: Seq[_]) = seq.length
len: (seq: Seq[_])Int

scala> len(<b>Hello</b>)
res1: Int = 1
scala> len(List(1,2,3))
res11: Int = 3

XML 可以被动态嵌入attribute和element生成
def now = System.currentTimeMillis.toString
<b time={now}>Hello World</b>

attribute的取值可以是String NodeSeq和Option[NodeSeq],如果Option的结果是None，则整个attribute都不会展示：
def isOdd(in: Long) = in % 2L == 1L

import scala.xml._
def oddTime: Option[NodeSeq] = System.currentTimeMillis match {
case t if isOdd(t) => Some(Text(t.toString))
case _ => None
}
<b time={oddTime}>Sometimes</b>
如果有some，结果是
res6: scala.xml.Elem = <b time="1230679058437">Sometimes</b>
如果是None是：
res8: scala.xml.Elem = <b>Sometimes</b>


<b>The time is {new java.util.Date}</b>
如果将嵌入的scala代码返回一个NodeSeq，则直接嵌入；否则返回结果会被转成String，在装成scala.xml.Text的。

<stuff>{(1 to 3).map(i => <v id={i.toString}>#{i}</v>)} </stuff>


需要注意的是if 后面必须有 else，因为没有else的if的返回是Unit，而转成一个空String
2.10中已经修复
<b>{if (true) "dogs"}</b> wrong
<b>{if (true) "dogs" else ""}</b> right

Scala correctly escapes characters from your Scala expressions:
scala> <b>{"Hello & Goodbye"}</b>
res3: scala.xml.Elem = <b>Hello &amp; Goodbye</b>
scala> <b attr={"Hello < Zoo"}/>
res6: scala.xml.Elem = <b attr="Hello &lt; Zoo"></b>

but if you’re trying to embed a script in your XML, it might not be the right thing:
You can use PCData to embed unescaped characters in your XML: 
val info = """
var x = "";
if (true && false) alert('Woof');
"""
<script>{info}</script>

scala> <script>{PCData(info)}</script>


#### Parsing XML

import scala.xml._
val xml = XML.load("http://www.w3school.com.cn/example/xmle/plant_catalog.xml")

all the <ZONE> tags in the document
xml \\ "ZONE"
\\ 会匹配文档中的所有节点，\ 会匹配节点的子节点中的所有节点，
如 xml \ "PLANT" \ "ZONE"
The \\ (double backslash) operator finds all the tags with the given label in the document. The \ (single backslash) operator finds all the tags with the given label that are direct children of the current tag.

如果query最前面是@，则会匹配所有的attribute
Also, if the first
character of the query String is an @, the query is performed against the attributes in the
tag. Let’s find all the <a> tags that refer to external resources:

(xml \\ "a").map(_ \ "@href").map(_.text).filter(_ startsWith "http:")
res11: Seq[String] = ArrayBuffer(http://liftweb.net, http://scala-lang.org, ...

Example
val x2 = <x>{(1 to 3).map(i => <i>{i}</i>)}</x>
x2 \ "i"
(x2 \ "i").map(_.text)
(x2 \ "i").map(_.text.toInt)
(x2 \ "i").map(_.text.toInt).foldLeft(0)(_ + _)


#### Modifying XML

xml提供了一些时间复杂度为logn的方法

首先创建一个修改的rule，如果处理的xml节点中有instruction attribute并且值为remove，则去掉：
import scala.xml._
import scala.xml.transform._
val removeIt = new RewriteRule {
	override def transform(n: Node): NodeSeq = n match {
		case e: Elem if (e \ "@instruction").text == "remove" => NodeSeq.Empty
		case n => n 
	}
}

val xmlBooks =
<books instruction="update">
  <book instruction="remove" name="book1" status=""/>
  <book instruction="add" name="book2" status=""/>
</books>

new RuleTransformer(removeIt).transform(xmlBooks)

我们在看一个添加节点的例子：
val addIt = new RewriteRule {
override def transform(n: Node): NodeSeq = n match {
		case e: Elem if (e \ "@instruction").text == "add" =>
			new Elem(e.prefix, e.label,
				e.attributes.remove("instruction"),
				e.scope,
				transform(e.child) ++ <added>I added this</added> :_*)
		case n => n
	}
}

还可以同时用几个rule
new RuleTransformer(addIt, removeIt).transform(xmlBooks)

#### Concurrency Without Synchronization
主要是展示了使用AtomicReference中compareAndSet功能的例子
P111 Listing 3-8. Multics.scala




### Chapter 4 函数

#### A Function Is an Instance
函数就是对象
scala> val f: Int => String = x => "Dude: "+x
f: Int => String = <function1>

可以作为参数传入，例如w42表示对传入的函数f，使用42作为参数：
scala> def w42(f: Int => String) = f(42)
w42: (f: Int => String)String
scala> w42(f)
res4: String = Dude: 42

几种调用方式，最常用的是最后一种
scala> w42((i:Int) => f(i))
scala> w42(i => f(i))
scala> w42(f(_))
scala> w42(f)
最终desuger后的方法都是这样的：
w42(new Function1[Int, String] { 
def apply(i: Int) = f(i)
})

scala> w42(new Function1[Int, String] { def apply(i: Int) = f(i)})
scala> w42 {i => (1 to i).mkString(",")}

注意def开始的w42的定义是方法，如果直接将它赋给一个函数会报错
scala> val x = w42
<console>:24: error: missing arguments for method w42;
follow this method with `_' if you want to treat it as a partially applied funct
ion
       val x = w42
               ^

scala> val x = w42 _
x: (Int => String) => String = <function1>

#### Partial Application and Functions

偏应用和函数

方法是对象上的feature，而函数是对象，继承于FunctionNN trait。在编译时，有很多语法糖使编写函数可以很简短

scala借鉴了ML和Haskell，对他们来说，(Int, Int) => String 就是 Int => Int => String，可以很容易的通过应用第一个参数而创建一个新的函数，这叫做Partial Application。
This is called partial application because some of the parameters are passed to the function rather than all the parameters being applied to the function.

scala需要特殊语法处理来支持Partial Application
这是普通的处理过程：
scala> def plus(a: Int, b: Int) = "Result is: "+(a + b)
plus: (a: Int, b: Int)String

scala> val p = (b: Int) => plus(42, b)
p: Int => String = <function1>
因为Partial Application非常有用，所以scala提供了额外的特殊语法：
scala> def add(a: Int)(b: Int) = "Result is: "+(a + b)
add: (a: Int)(b: Int)String

scala> add(1)(2)
res19: String = Result is: 3

可以传入代码段或者函数
scala> add(1){new java.util.Random().nextInt(100)}
res20: String = Result is: 16

It also allows you to easily promote a method to a partially applied function very easily:
因为提供了一个参数的add就是一个偏函数：
scala> add(1)_
res28: Int => String = <function1>
scala> w42(add(1))
res21: String = Result is: 43

#### Functions and Type Parameters

方法可以使用类型参数，用类型来限定方法的参数或返回值
但是函数不能使用类型参数，当函数定义时，函数的参数和返回值的类型必须是确定的。

Methods can have type parameters. Type parameters define the type of other parameters or of the method’s return value. Note that functions cannot take type parameters. The parameter and return types of a function must be defined when the function is created.

我们先定义一个方法
scala> def t42[T](f: Int => T): T = f(42)
t42: [T](f: Int => T)T
这时，t42会根据传入的函数的不同类型来返回不同的类型：
scala> t42(f)
res23: String = Dude: 42
scala> t42(1 +)
warning: there were 1 feature warning(s); re-run with -feature for details
res24: Int = 43

可以显示定义为这样
t42[Int](1 +)

如果我们强行给t42赋值：
scala> val a = t42
<console>:8: error: missing arguments for method t42;
follow this method with `_' if you want to treat it as a partially applied funct
ion
       val a = t42
               ^

scala> val a = t42 _
a: (Int => Nothing) => Nothing = <function1>

scala> val f = t42[Int] _
f: (Int => Int) => Int = <function1>

scala> f("" +)
<console>:10: error: type mismatch;
 found   : String
 required: Int
              f("" +)
                   ^


#### Functions Bound to Variables in Scope

函数在创建时会和scope上下文中的变量绑定，这样可以很方便的使函数具有状态：
Functions are bound to the variables in the scope in which the function is created. This can come in very handy as it allows you to carry state around with them. For example, let’s create a variable, foo, and assign it a value
scala> val foo = "dog"
foo: String = dog

scala> val whoTo = (s: String) => s+" "+foo
whoTo: String => String = <function1>

scala> whoTo("I love my")
res25: String = I love my dog

这里是一个val，如果是vars呢？
scala> var foo = "dog"
foo: String = dog

scala>  val whoTo = (s: String) => s+" "+foo
whoTo: String => String = <function1>

scala> whoTo("I walk my")
res6: String = I walk my dog

scala> foo = "cat"
foo: String = cat

scala> whoTo("I walk my")
res7: String = I walk my cat


Functions can be bound to vars and vals. 
函数甚至也可以修改变量
scala> var strs: List[String] = Nil
strs: List[String] = List()
定义一个函数，返回string，同时有修改strs的副作用
scala> val strF = (s: String) => {strs ::= s; s+" Registered"}
strF: String => String = <function1>

scala> strF("a")
res26: String = a Registered
scala> strF("b")
res27: String = b Registered
scala> strs
res28: List[String] = List(b, a)

scala> List("p", "q", "r").map(strF)
res29: List[String] = List(p Registered, q Registered, r Registered)
scala> strs
res30: List[String] = List(r, q, p, b, a)


#### Putting Functions in Containers

函数是对象，所以对象的操作也可以应用到函数上，如把函数放在容器中：
Let’s create a function, bf, which takes an Int and returns a function:

scala> def bf: Int => Int => Int = i => v => i + v
bf: Int => (Int => Int)
scala> val fs = (1 to 5).map(bf).toArray
fs: Array[Int => Int] = Array(<function1>, <function1>, <function1>, <function1>
, <function1>)
We’ve got an Array[Int => Int],然后可以使用了：

scala> fs(0)(1)
res0: Int = 2
scala> fs(4)(3)
res2: Int = 8

It’s theoretically cool that functions are instances that can be manipulated like any
other instance. There are practical uses of putting functions in Maps and Lists. Functions
represent blocks of code—instructions on how to do something that is within a particular
context and that is bound to variables in a particular scope. The ability to bind functions
to events, such as the user clicking a button on a screen, which may occur in the future,
provides a powerful way to build interactive, event-based applications.

#### Functions and Interactive Applications

Callbacks are very common in interactive applications
看看scala提供了哪些便利
假设有一个提供GUID的方法：
def randomName = "I"+Math.abs((new java.util.Random).nextLong)
假设trait JavaScript包含了browser中执行的js命令
然后我们创建一个通过GUID生成一个js的Map：
var callbacks: Map[String, () => JavaScript] = Map()
最后创建一个方法来注册函数，并生成相应button的HTML。当按钮被点击的时候，server获取ajax请求，返回相应的js函数
def register(f: () => JavaScript) = {
val name = randomName
callbacks += name -> f
<button onclick={"invokeSeverCall('"+name+"')"}>ClickMe</button>
}
处理ajax的代码可以这样写：
def handleAjax(guid: String): HttpResponse =
functionMap.get(guid).map(f => f()) match {
	case Some(javaScript) => JavaScriptResponse(javaScript)
	case _ => Http404Response()
}
这是lift中处理服务器请求相应的一个方式


#### Building New Functions

可以通过函数组合的方式创建新的函数：
这是个语法解析的例子：
sealed trait Expr
case class Add(left: Expr, right: Expr) extends Expr
case class Mul(left: Expr, right: Expr) extends Expr
case class Val(value: Int) extends Expr
case class Var(name: String) extends Expr

创建一个方法使之能够处理这些表达式：1 + 1, Add(Val(1), Val(1)), 3 * (1 + 1), Mul(Val(3),
Add(Val(1), Val(1)), and a * 11, Mul(Var("a"), Val(11)).
其中使用了vars来获取变量的值

def calc(expr: Expr, vars: Map[String, Int]): Int = expr match {
	case Add(left, right) => calc(left, vars) + calc(right, vars)
	case Mul(left, right) => calc(left, vars) * calc(right, vars)
	case Val(v) => v
	case Var(name) => vars(name)
}

现在将方法调用变为函数，这样就可以通过传入表达式将函数一次解析好，多次使用了：
Having a function allows us to
pass around the logic that the expression represents. It also means that we don’t have to
interpret the tree of Exprs each time

def buildCalc(expr: Expr): Map[String, Int] => Int = expr match {
	case Add(left, right) =>
		val lf = buildCalc(left)
		val rf = buildCalc(right)
		m => lf(m) + rf(m)
	case Mul(left, right) =>
		val lf = buildCalc(left)
		val rf = buildCalc(right)
		m => lf(m) * rf(m)
	case Val(v) => m => v
	case Var(name) => m => m(name)
}
这里传出的函数只需要使用几次apply方法，而不需要再使用模式匹配了
而对传出的函数，编译器也会做一些优化

#### Call-by-Name, Call-by-Value, and General Laziness

在java中，传入的参数必须先经过计算，例如日志中就会出现很多这样的情况：
if (logger.level().intValue() >= INFO.intValue()) {
	logger.log(INFO, "The value is "+value);
}

Call-by-name 提供了延迟赋值的能力，所以可以不带那些恼人的判断了
def log(level: Level, msg: => String) =
	if (logger.level.intValue >= level.intValue) logger.log(level, msg)
这里的msg实际是以function的形式传入的
所做的只需要见=>加在类型之前，如将foo(s: String)是call-by-reference，而foo(s: => String)是call-by-name

但是这样不是会使效率变低，从而抵消了带来的性能改进吗？
In the JVM, the cost of creating an
object that never escapes the current thread and is very short-lived is zero or very near
zero. The JVM may also inline the log method such that the test is performed without an
actual method call. The result is that your code will run as quickly with the Scala code as it
will with the Java code that has the repeated test for log level.

使用call-by-name的场景还有延迟初始化，和多次取值
例如一个方法会不定时返回String，我们想获得这个返回String的集合，直到方法返回null，可以这样写
def allStrings(expr: => String): List[String] = expr match {
	case null => Nil
	case s => s :: allStrings(expr)
}
他就可以这样使用：
allStrings(new BufferedReader(new FileReader("foo.txt")).readLine)


#### Build Your Own Control Structures

scala只有很少的控制结构：try/catch/finally, if/else, and while，但是可以利用call-by-name特性创建控制结构
例如C#中有一个很好的特性using，可以自动关闭资源，而不用编写try finally：
using (TextReader textReader = new StreamReader(filename))
{
return textReader.ReadLine();
}

我们也构建两个控制结构

object Control {
  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B = 
  try {
    f(param)
  } finally {
    param.close()
  } 

  import scala.collection.mutable.ListBuffer

  def bmap[T](test: => Boolean)(block: => T): List[T] = {
    val ret = new ListBuffer[T]
    while(test) ret += block
    ret.toList
  }
}

注意using的定义，使用了泛型
其中A泛型要求必须定义close方法，这叫做structural typing 结构类型，scala允许通过结构，而不是类来定义类型，这有点像js或duck typing语言Ruby python

using使用很简单：
using(new BufferedReader(otherReader)) {
	reader => reader.readLine()
}

另一个控制结构bmap就是根据条件决定是否加到一个List中的方法，与前面的allStrings类似


#### JDBC Looping

根据前面的控制结构，我们可以写出jdbc的处理代码：

import java.sql.*;
import java.util.ArrayList;
public class Person {
	private String name;
	private int age;
	private boolean valid;
	public Person(String n, int a, boolean v) {
		name = n;
		age = a;
		valid = v;
	}
	public static ArrayList<Person> findPeople(Connection conn)
		throws SQLException {
		Statement st = conn.createStatement();
		try {
			ResultSet rs = st.executeQuery("SELECT * FROM person");
			try {
				ArrayList<Person> ret = new ArrayList<Person>();
				while (rs.next()) {
					ret.add(new Person(rs.getString("name"),
					rs.getInt("age"),
					rs.getBoolean("valid")));
				}
				return ret;
			} finally {
				rs.close();
			}
		} finally {
			st.close();
		}
	}
}

用scala和我们编写的控制结构来处理是这样的：
case class Person(name: String, age: Int, valid: Boolean)
object Person {
	import Control._
	import java.sql._
	def findPeople(conn: Connection): List[Person] =
	using(conn.createStatement){st =>
		using (st.executeQuery("SELECT * FROM person")){rs =>
			bmap(rs.next){
				new Person(rs.getString("name"), rs.getInt("age"), rs.getBoolean("valid"))
			}
		}
	}
}


### Chapter 5 Pattern Matching

#### Basic Pattern Matching

def fibonacci(in: Int): Int = in match {
	case n if n <= 0 => 0
	case 1 => 1
	case n => fibonacci(n - 1) + fibonacci(n - 2)
}

Under the hood, the compiler may optimize the pattern1 and minimize
the number of tests, cache test results, and even cache guard results.
1. A huge thanks to David MacIver for improving Scala’s pattern-matching code

#### Matching Any Type

def myMules(name: String) = name match {
	case "Elwood" | "Madeline" => Some("Cat")
	case "Archer" => Some("Dog")
	case "Pumpkin" | "Firetruck" => Some("Fish")
	case _ => None
}

可以用scalac -print MyMules.scala 来查看编译出来的java源代码

#### More Pattern Matching

Patterns can match across different types in the same statement:
def test1(in: Any): String = in match {
	case 1 => "One"
	case "David" | "Archer" | Some("Dog") => "Walk"
	case _ => "No Clue"
}

#### Testing Data Types

java中的类型检查是很恼人的，模式匹配可以匹配类型

def test2(in: Any) = in match {
	case s: String => "String, length "+s.length
	case i: Int if i > 0 => "Natural Int"
	case i: Int => "Another Int"
	case a: AnyRef => a.getClass.getName
	case _ => "null"
}

#### Case Classes

case class Person(name: String, age: Int, valid: Boolean)
默认的是immutable的，但是也可以改为可变的：
case class MPerson(var name: String, var age: Int)

模式匹配和case class结合起来很强大，可以很方便的使用case class的提取信息：
def older(p: Person): Option[String] = p match {
	case Person(name, age, true) if age > 35 => Some(name)
	case _ => None
}


#### Pattern Matching in Lists

scala的List由cons组成，cons包含一个value和一个指向另一个list的reference，可能是另一个cons或Nil。
list是immutable的，所以一个list的tail可能被多个list引用。
cons cell是一个case class

(x :: rest) match { // note the symmetry between creation and matching
	case xprime :: restprime => println(xprime); println(restprime)
}

#### Pattern Matching and Lists

将所有odd的数加和
def sumOdd(in: List[Int]): Int = in match {
	case Nil => 0
	case x :: rest if x % 2 == 1 => x + sumOdd(rest)
	case _ :: rest => sumOdd(rest)
}

清除连续的元素
def noPairs[T](in: List[T]): List[T] = in match {
	case Nil => Nil
	case a :: b :: rest if a == b => noPairs(a :: rest)
	// the first two elements in the list are the same, so we’ll
	// call noPairs with a List that excludes the duplicate element
	case a :: rest => a :: noPairs(rest)
	// return a List of the first element followed by noPairs
	// run on the rest of the List
}

清除跟随者ignore的元素
def ignore(in: List[String]): List[String] = in match {
	case Nil => Nil
	case _ :: "ignore" :: rest => ignore(rest)
	case x :: rest => x :: ignore(rest)
}

获取所有的String
def getStrings(in: List[Any]): List[String] = in match {
	case Nil => Nil
	case (s: String) :: rest => s :: getStrings(rest)
	case _ :: rest => getStrings(rest)
}
然而更好的方式是使用function的模式，后面会看到


#### Nested Pattern Matching in Case Classes

首先看case class
case class MarriedPerson(override val name: String,
	override val age: Int,
	override val valid: Boolean,
	spouse: Person) extends Person(name, age, valid)

override val是一个不好的写法，也是scala的缺点之一

伴侣中有一个是大于35的就输出
def mOlder(p: Person): Option[String] = p match {
	case Person(name, age, true) if age > 35 => Some(name)
	case MarriedPerson(name, _, _, Person(_, age, true))
		if age > 35 => Some(name)
	case _ => None
}


#### Examining the Internals of Pattern Matching

下面将实现一下List的模式匹配来展示scala的内部逻辑
以来的语法功能有：
1. scala可以让操作符成为方法名或类名：Foo, Foo_?,foo32, ?, ?:
2. 以: 结尾的方法是从右向左计算的，所以 3 :: Nil 等同于 Nil.::(3)

所以我们有下面的代码【因为有环形依赖，所以不能再REPL中运行】
class MList[+T] {
	def ?:[B >: T](x: B): MList[B] = new ?:(x, this)
}
case object MNil extends MList[Nothing]
case class ?:[T](hd: T, tail: MList[T]) extends MList[T]

首先看下MList，他的类型参数是+T，表示MList是协变的
然后有个方法?:和cons的名称相同，有类型约束B >: T，表示B必须是T的父类【包括T本身】
所以如果你有一个MList[String],并加入一个Int的cell，则返回的类型参数将是两者的父类：Any
如果你有一个MList[Number],并加入一个Int的cell，则返回的类型参数将是两者的父类：Number

MNil是一个单例 —— MList[Nothing]，其中Nothing 是所有其他类的子类
因为MList是协变的，所以MNil是所有其他类的子类
如果有一个String,并使用?:加入MNil中，会合为一个MList[String]，因为String是两者的父类。

cons cell ?:没有什么好说的，我们现在用一下吧：

def tryMList(in: MList[Any]) = in match {
	case 1 ?: MNil => "foo"
	case 1 ?: _ => "bar"
	case _ => "baz"
}

#### Pattern Matching As Functions

Scala patterns are syntactic elements of the language when used with the match operator
在使用match操作符的时候 scala模式是语法元素
不过你可以把pattern matching作为参数传给其他方法
scala会将它表以为一个PartialFunction[A,B] 偏函数 —— Function1[A,B]的子类
所以可以把它看做是一个只有一个参数的函数，从而可以将下面的代码：
list.filter(a => a match {
	case s: String => true
	case _ => false
})
简写为这样：
list.filter {
	case s: String => true
	case _ => false
}

pattern是对象，所以也可以被存起来

除了apply方法外，PartialFunction还提供了isDefinedAt方法
可以用来看看一个值能不能被指定pattern所匹配，不匹配会抛出一个MatchError

例如一个过滤器要特殊处理某些url的请求时候，可以这样写：
def handleRequest(req: List[String])(
	exceptions: PartialFunction[List[String], String]): String =
	if (exceptions.isDefinedAt(req)) exceptions(req) else
	"Handling URL "+req+" in the normal way"

handleRequest("foo" :: Nil) {
	case "api" :: call :: params => doApi(call, params)
}
def doApi(call: String, params: List[String]): String =
	"Doing API call "+call

PartialFunction可以使用orElse组合在一起：
val f1: PartialFunction[List[String], String] = {
	case "stuff" :: Nil => "Got some stuff"
}
val f2: PartialFunction[List[String], String] = {
	case "other" :: params => "Other: "+params
}
val f3 = f1 orElse f2
handleRequest("a" :: "b" :: Nil)(f3)

通过这种方法可以很好地用declarative way来处理复杂的filter任务


#### Object-Oriented and Functional Tensions

What happens when a library is deployed but it’s
necessary to add new functionality to subclasses? How do we retrofit the defined-in-stone
library classes to add this functionality?

Put more concretely, if we have a library of
shapes—circle, square, rectangle—that each have an area method but hide all their other
data, how do we add a perimeter method to the shapes?
如果我们有一个shape库，有circle, square, rectangle类，除了提供area方法外没有暴露其他内容，我们怎样增加一个perimeter方法

首先是OO的类库
trait OShape {
def area: Double
}
class OCircle(radius: Double) extends OShape {
def area = radius * radius * Math.Pi
}
class OSquare(length: Double) extends OShape {
def area = length * length
}
class ORectangle(h: Double, w: Double) extends OShape {
def area = h * w
}

相比之下的pattern-matching的实现：
trait Shape
case class Circle(radius: Double) extends Shape
case class Square(length: Double) extends Shape
case class Rectangle(h: Double, w: Double) extends Shape
object Shape {
	def area(shape: Shape): Double = shape match {
		case Circle(r) => r * r * Math.Pi
		case Square(l) => l * l
		case Rectangle(h, w) => h * w
	}
}

目前看OO更胜一筹，但是如果需要增加一个perimeter方法呢？
用pattern matching很简单
def perimeter(shape: Shape) = shape match {
	case Circle(r) => 2 * Math.Pi * r
	case Square(l) => 4 * l
	case Rectangle(h, w) => h * 2 + w * 2
}

但对OO来说需要暴露很多信息：
trait OShape {
def area: Double
}
class OCircle(radius: Double) extends OShape {
	def area = radius * radius * Math.Pi
	def getRadius = radius
}
class OSquare(length: Double) extends OShape {
	def area = length * length
	def getLength = length
}
class ORectangle(h: Double, w: Double) extends OShape {
	def area = h * w
	def getHeight = h
	def getWidth = w
}

一般的，类库设计者不能够料想到将来需要的方法
visitor 模式允许在hierarchy已经确定的情况下添加一些功能：
例如一个汽车的visitor：
trait OCarVisitor {
def visit(wheel: OWheel): Unit
def visit(engine: OEngine): Unit
def visit(body: OBody): Unit
	def visit(car: OCar): Unit
}
trait OCarElement {
	def accept(visitor: OCarVisitor): Unit
}
class OWheel(val name: String) extends OCarElement {
	def accept(visitor: OCarVisitor) = visitor.visit(this)
}
class OEngine extends OCarElement {
	def accept(visitor: OCarVisitor) = visitor.visit(this)
}
class OBody extends OCarElement {
	def accept(visitor: OCarVisitor) = visitor.visit(this)
}
class OCar extends OCarElement {
	val elements = List(new OEngine, new OBody, new OWheel("FR"),
	new OWheel("FL"), new OWheel("RR"), new OWheel("RL"))
	def accept(visitor: OCarVisitor) =
	(this :: elements).foreach(_.accept(visitor))
}

类库设计者在固定了hierarchy的同时也考虑到了扩展，为每一个组件提供了visitor接口
这里有很多boilerplate代码【相比之下ruby python不需要这样，他们的hierarchy甚至在OCarVisitor定义的时候都不固定】
并且visit的时候，每个数据都要被visit到

让我们看看pattern-matching实现：
trait CarElement
case class Wheel(name: String) extends CarElement
case class Engine() extends CarElement
case class Body() extends CarElement
case class Car(elements: List[CarElement]) extends CarElement
def doSomething(in: CarElement): Unit = in match {
	case Wheel(name) =>
	case Engine() =>
	case Body() =>
	case Car(e) => e.foreach(doSomething)
}

More generally, Burak Emir, one of Scala’s authors, wrote an excellent paper on the
intersection of pattern matching and object-oriented design. See http://library.epfl.ch/theses/?nr=3899.
http://infoscience.epfl.ch/record/109881
http://infoscience.epfl.ch/record/109881/files/EPFL_TH3899.pdf


### Chapter6 Actors and Concurrency

#### A Different Approach to Concurrency: Look Ma, No Locks

Actors are threadless, stackless units of execution that process messages (events) serially
Actors process incoming messages and encapsulate their state. At this point, Actors sound a lot
like OOP message sending and encapsulation, and it turns out this is the case.

Smalltalk, Objective-C, Ruby, JavaScript, and Python are unityped or duck-typed
languages.5 Instances in each of those languages is of the same type. You can send any
message or invoke any method on any instance. The ability for an instance to process a
method or message is determined at runtime. Scala, on the other hand, is a statically typed
language where the class of every instance is known at compile time and the availability
of a method on a given instance can be verified at compile time.

Like instances in duck-typed languages, Actors process messages at runtime, and there’s
no compile-time checking to see whether the Actor can process a particular message. The
key differences between Actors and duck-typed instances are that Actors always process
messages asynchronously and may not process messages in the order that they were delivered.

Because the Actor does not expose state and can only be modified or queried via
messages, and because messages are processed serially, there’s no reason to assert locks
on internal Actor state. Thus, Actors are lockless at the application level, yet thread-safe.

附注：
2. http://carlhewitt.info/
3. http://en.wikipedia.org/wiki/Actor_model
4. http://www.brics.dk/~hosc/local/HOSC-11-4-pp399-404.pdf
5. The term “duck-typed” comes from the phrase “If it walks like a duck and quacks like a duck, it must
be a duck.” If an instance can process the walk and quack messages, it must be a duck.

#### Defining an Actor

Actors are defined in two parts. First, you define the messages that an Actor can receive,
and second you define the Actor itself.
两部分：消息和处理

Actors can receive any message that can be pattern matched in Scala.
这些都是有效的消息：
a ! "Hello"
a ! 42
a ! ("Add", 1)
a ! List(1,2,3)

但是一般使用case class是最好的，可以进行编译校验
case class Add(i: Int)
a ! Add(1)
就好于
a ! ("Add", 1)

因为当我们将类改为 case class Add(i: Double) 并重编译后，相应的消息都是类型安全的了

6. Philip Wadler refers to duck-typed languages as “unityped,” which seems to me to be very descriptive.
See http://jjinux.blogspot.com/2008/01/haskell-well-typed-programs-cant-be.html.

##### Defining an Actor

定义一个简单的Actor

class BasicActor extends Actor {
	def act = loop {
		react {
			case s => println("Got a message: "+s)
		}
	}
}
注意这里的loop，如果不用loop需要这样定义：
class LoopActor extends Actor {
	def act =
	react {
		case s: String =>
			println("Got a string: "+s)
			act
		case x =>
			println("Got a message: "+x)
			act
	}
}
如果不在最后调用act，则会处理一条消息后就停止

##### Would You Like State With That?

Erlang does not support objects or any
concept of mutable private fields, so Erlang Actors must carry all state on the stack and
must explicitly and recursively call the handler with the current state

我们看一个统计消息获取数的actor：
class SomeState extends Actor {
	private var cnt = 0
	def act = loop {
		react {
			case _ => cnt += 1
				println("Received "+cnt+" messages")
		}
	}
}

这里cnt保存着状态，且不会出现并发问题。如果我们不要可变的变量，可以这样写：
class NoState extends Actor {
	def act = run(0)
	private def run(cnt: Int): Unit =
	react {
		case _ =>
			val newCnt = cnt + 1
			println("Received "+newCnt+" messages")
			run(newCnt)
	}
}
一般的，似乎前者要更加自然一些

##### Instantiating an Actor

创建actor需要以下步骤：实例化一个actor，start启动它，然后就可以发消息了
val actor = new SomeState
actor.start
actor ! "Hello"

actor可以是object，从而形成单例。
//有时往往将this.start写在构造函数中

#### Implementing a Listener

附注：这个例子具的并不是太好，没感觉出actor的好处，listener的bug也很容易就修复了
首先看一下listener模式，然后我们用actor重写一下：

首先是simpleListener，有一个bug

trait MyListener {
  def changed(event: Foo, count: Int): Unit
}

class Foo {
  private var listeners: List[MyListener] = Nil
  private var count = 0

  def access() = synchronized {
    notifyListeners
    count += 1 // bugfix 这里的顺序反了，应该放在notifyListeners前
    count
  }

  private def notifyListeners = synchronized {
    listeners.foreach(_.changed(this, count))
  }

  def addListener(who: MyListener): Unit = synchronized {
    listeners ::= who
  }
}

class FooListener(foo: Foo) extends MyListener {
  foo.addListener(this)

  def changed(event: Foo, count: Int): Unit = {
    println(count)
    if (count < 10) event.access()
  }
}

运行方式：
val f = new Foo
val fl = new FooListener(f)
f.access

然后是Actor实现相同的功能：

import scala.actors.Actor
import Actor._

case class Add(who: Actor)
case class Changed(what: AFoo, count: Int)
case object Access

class AFoo extends Actor {
  private var listeners: List[Actor] = Nil
  private var count = 0

  def act = loop {
    react {
      case Add(who) => listeners = who :: listeners
      case Access => access()
    }
  }

  private def access() = {
    notifyListeners
    count += 1
  }

  private def notifyListeners =
  listeners.foreach(a => a ! Changed(this, count))
}

class AFooListener(afoo: AFoo) extends Actor {
  afoo ! Add(this)

  def act = loop {
    react {
      case Changed(f, cnt) => changed(f, cnt)
    }
  }

  def changed(eventFrom: AFoo, count: Int): Unit = {
    println(count)
    if (count < 10) eventFrom ! Access
  }
}

运行方式：
val af = new AFoo
af.start
val afl = new AFooListener(af)
afl.start
af ! Access

#### Actors: More Than Write-Only

It’s possible to send a message to an Actor and synchronously wait for a reply:
可以发送个消息之后，同步的等待返回：

ChatServer2 !? GetMessages
这个代码会向ChatServer2发送消息，返回是Some(response)
val msgs: Option[List[String]] = ChatServer2 !? GetMessages match {
	case Messages(msg) => Some(msg)
	case _ => None
}

也可以加一个超时timeout，这里是500ms
val msgs2: Option[List[String]] = ChatServer2 !? (500, GetMessages) match {
	case Some(Messages(msg)) => Some(msg)
	case Some(_) => None // got a response we didn't understand
	case None => None // timeout
}

我们看一下ChatServer的实现：
case object GetMessages
case class Messages(msg: List[String])
object ChatServer2 extends Actor {
	private var chats: List[String] = Nil
	def act = loop {
		react {
			case s: String => chats = s :: chats
			case GetMessages => reply(Messages(chats))
		}
	}
	this.start // make sure we start the chat server
}

这里的GetMessages很像一个getter


#### Beyond the Basics


##### Protocol Handler

One of the things we learned is that Scala turns patterns into
partial functions. Unsurprisingly, the react method takes a PartialFunction as a parameter.
scala的模式匹配被转义成了partial function，而react方法接收的参数也是偏函数
片函数是对象，所以我们可以做很多事情，包括组合等

我们将编写一个chat server，当有3个chat以上时才允许动态的添加listener

import scala.actors.Actor
import Actor._

case object GetMessages
case class Messages(msg: List[String])
case class Remove(who: Actor)
case class Add(who: Actor)

object ChatServer3 extends Actor {
  private var chats: List[String] = Nil
  private var listeners: List[Actor] = Nil

  def act = loop {
    react(calcReact)
  }

  private def calcReact = {
    // 这个方法和actor的状态无关
    val handle: PartialFunction[Any, Unit] = {
      case s: String => chats = s :: chats
        notifyListeners()

      case GetMessages => reply(Messages(chats))
    }

    val mgt: PartialFunction[Any, Unit] =
    if (chats.length < 3)
      Map.empty
    else {
      case Add(who) => listeners = who :: listeners
        who ! Messages(chats)

      case Remove(who) => listeners -= who
    }

    handle orElse mgt
  }

  private def notifyListeners() {
    listeners.foreach(a => a ! Messages(chats))
  }

  this.start()
}


#### Actors, Atomic Updates, and Transactions

ACID stands for atomic, consistent, isolated, and durable. Atomic means that if the transaction
succeeds, all the rows affected by the transaction are changed at the same instant
in the view of other transactions. Consistent means that all of the rows will be updated if
the transaction succeeds. Isolated means that until the transaction is committed, no other
transactions in the system see any updated rows. Durable means that the transaction is
written to media, disk, before the commit is done. In this section, I’m going to build a
transaction system that is ACI but not D. There will be no durable writes to disk.

Performing an Atomic Update

import scala.actors.Actor
import Actor._

case object GetInfo
case class Info(i: Map[String, Int])
case class SetInfo(n: String, v: Int)
case class Update(n: String, f: Option[Int] => Int)

object XAct1 extends Actor {
  private var info: Map[String, Int] = Map()

  def act = loop {
    react {
      case GetInfo => reply(Info(info))
      case SetInfo(n, v) => info += n -> v
      case Update(n, f) => info += n -> f(info.get(n))
    }
  }

  this.start
}

注意这里的update是传入一个方法来做更新操作，相当于提供了一个回调
可以试验一下：
XAct1 ! SetInfo("Apple", 4)
XAct1 ! SetInfo("Orange", 5)
println XAct1 !? GetInfo
XAct1 ! Update("Apple", v => (v getOrElse 0) + 2)
println XAct1 !? GetInfo


#### Performing Transactions

这个例子有点复杂，似乎没有OO的清楚

#### Composing Actors

We are going to build
two generic traits and compose them together into a specific Actor to provide chat server
functionality that we’ve built in a monolithic fashion earlier in the chapter.
使用组合的方式重新实现chat server

Why not build a generic listener and
then compose it with a trait that contains business logic into a single Actor?
首先实现一个通用的listener：

import scala.actors.Actor
import Actor._

trait Buildable {
  def handler: PartialFunction[Any, Unit] = Map.empty
}

case class Add(who: Actor)
case class Remove(who: Actor)

trait ListenerMgt extends Buildable {
  private var listeners: List[Actor] = Nil

  override def handler = super.handler orElse {
    case Add(who) =>
      listeners = who :: listeners
      who ! updateMessage
    case Remove(who) => listeners -= who
  }

  protected def updateListeners() {
    val m = updateMessage
    listeners.foreach(a => a ! m)
  }

  protected def updateMessage: Any
}

case object GetInfo

trait GetMgt extends Buildable {
  override def handler = super.handler orElse {
    case GetInfo => reply(updateMessage)
  }

  protected def updateMessage: Any
}

case class Messages(msgs: List[String])

object Chat extends Actor with ListenerMgt with GetMgt {
  private var msgs: List[String] = Nil
  def act = loop {
    react(handler orElse {
        case s: String => msgs ::= s
          updateListeners()
      })
  }

  protected def updateMessage = Messages(msgs)

  this.start
}

class Listen extends Actor {
  def act = loop {
    react {
      case Messages(m) => println("Got "+m)
    }
  }

  this.start
}

还是有点云里雾里。。。

有点学术化了
Philipp Haller and Martin Odersky, “Event Based Programming without Inversion of Control,” http://
lampwww.epfl.ch/~odersky/papers/jmlc06.pdf and Philipp Haller and Martin Odersky, “Actors that
Unify Threads and Events,” http://lamp.epfl.ch/~phaller/doc/haller07coord.pdf.








### Chapter 7 Traits and Types and Gnarly Stuff for Architects

之前都是从“library-consumer”的角度看问题的，现在要从“library-producer”的角度看问题。
Scala’s traits, type system, flexible syntax, and implicit
conversions give amazingly powerful tools to architects and library designers to build
libraries that are simultaneously easy and safe to use.

I can write logic-oriented
tests rather than tests that try to fool my code and make sure that I’m guarding against
nulls or other parameter and type-related problems.1

Among enthusiasts of other statically typed languages with rich type systems (Standard ML, Haskell,
OCaml) the “architect style” is often referred to as “typeful programming,” referring exactly to this
distinction between “going with the type inference flow” and “using the type system deliberately to
encode important invariants.”


#### Library Pimping, Implicit Conversions, and Vampires

scala> "Hello".toList
res0: List[Char] = List(H, e, l, l, o)

String拥有了collection的方法，而String是final的，scala是怎样做到的呢？
Scala has a feature called implicit conversion

例如这样一个方法def millisToDays(in: Long): Int = (in / (1000L * 3600L * 24L)).toInt
不能直接应用Date为参数，我们可以定义一个隐式转换，在必要的时候自动调用：
We can define a method that will automatically be called when we need the conversion:
scala> implicit def dateToLong(d: Date) = d.getTime
dateToLong: (java.util.Date)Long
scala> millisToDays(new Date)

你可能认为隐式转换降低了类型安全，这在一些方面是正确的，所以要小心对待
不过就像int到long的隐式转换一样，有时这是很方便的
def m2[T <: Long](in: T): Int = (in / (1000L * 3600L * 24L)).toInt
scala> m2(33.toLong)
res8: Int = 0
此时必须调用toLong方法，显得很不实用

##### Library Pimping
Dr. Martin Odersky, ACM Fellow, Full Professor, coined the term in this blog post: http://www.artima.com/weblogs/viewpost.jsp?thread=179766.

有了隐式转换，万里长征才走了一半，为了给final class添加方法，我们还需要Library Pimping
The second
half of the journey is that the Scala compiler will look to a possible implicit conversion
from the type you have to a type with the method that you’re invoking. The Scala compiler
will insert code to call the implicit conversion and then call the method on the resulting
instance.
第二步就是scala编译器会自己找相关的隐式转换，并添加相应的方法，例如"Hello".toList会被转成：
Predef.stringWrapper("Hello").toList

The ability to add new methods to existing classes has a lot of value for making code
more readable and expressive. More importantly, implicit conversions make it possible to
define DSLs in Scala.

As a library producer, we can create syntactically pleasing ways of expressing concepts
in a type-safe way. Wouldn’t it be nice to express a time span as 3 days or 15 seconds?
Wouldn’t that make code a lot more readable than (3L * 24L * 3600L * 1000L)? Wouldn’t
it be great to set a timeout or a trigger with 2.hours.later? Let’s define a library using
implicit conversions.

import java.util.Date

object TimeHelpers {
  case class TimeSpanBuilder(val len: Long) {
    def seconds = TimeSpan(TimeHelpers.seconds(len))
    def second = seconds
    def minutes = TimeSpan(TimeHelpers.minutes(len))
    def minute = minutes
    def hours = TimeSpan(TimeHelpers.hours(len))
    def hour = hours
    def days = TimeSpan(TimeHelpers.days(len))
    def day = days
    def weeks = TimeSpan(TimeHelpers.weeks(len))
    def week = weeks
  }

  def seconds(in: Long): Long = in * 1000L
  def minutes(in: Long): Long = seconds(in) * 60L
  def hours(in: Long): Long = minutes(in) * 60L
  def days(in: Long): Long = hours(in) * 24L
  def weeks(in: Long): Long = days(in) * 7L

  implicit def longToTimeSpanBuilder(in: Long): TimeSpanBuilder =
  TimeSpanBuilder(in)

  implicit def intToTimeSpanBuilder(in: Int): TimeSpanBuilder =
  TimeSpanBuilder(in)

  def millis = System.currentTimeMillis

  case class TimeSpan(millis: Long) extends Ordered[TimeSpan] {
    def later = new Date(millis + TimeHelpers.millis)
    def ago = new Date(TimeHelpers.millis - millis)
    def +(in: TimeSpan) = TimeSpan(this.millis + in.millis)
    def -(in: TimeSpan) = TimeSpan(this.millis - in.millis)

    def compare(other: TimeSpan) = millis compare other.millis
  }

  object TimeSpan {
    implicit def tsToMillis(in: TimeSpan): Long = in.millis
  }

  class DateMath(d: Date) {
    def +(ts: TimeSpan) = new Date(d.getTime + ts.millis)
    def -(ts: TimeSpan) = new Date(d.getTime - ts.millis)
  }

  implicit def dateToDM(d: Date) = new DateMath(d)
}

这段代码包含了 TimeSpanBuilder
implicit def longToTimeSpanBuilder将long或int转为TimeSpanBuilder
case class TimeSpan可以比较 +-等
还有DateMath用来和Date计算的方法

使用它
import TimeHelpers._
1.days
5.days + 2.hours
(5.days + 2.hours).later
val d = new Date("January 2, 2005")
d + 8.weeks
val lng: Long = 7.days + 2.hours + 4.minutes

短短50行代码就定义了一个time span的好的DSL


##### Inviting Vampires Into Your Code

隐式转换很强大但也会带来危险的问题
我曾经加入用下面的代码，从而不用再判断Option了，但是如果出现None的时候就惨了
implicit def oToT[T](in: Option[T]): T = in.get

The Scala compiler will consider an implicit in the current scope if
• The implicit is defined in the current class or in a superclass.
• The implicit is defined in a trait or supertrait, or is mixed into the current class or a superclass.
• The implicit is defined on the companion object of the current target class.
• The implicit is available on an object that has been imported into the current scope.



#### Traits: Interfaces on Steroids

Let’s create a generic OOP listener trait. It can be mixed into any class, and that class
will have the methods implemented on it.

带方法实现的Listener trait：

case class ChangeEvent[OnType](on: OnType)

trait Listener[T] {
  this: T with Listener[T] =>

  type ChangeHandler = {def changed(c: ChangeEvent[T with Listener[T]]): Unit}
  private var listeners: List[ChangeHandler] = Nil

  def addListener(c: ChangeHandler) = synchronized {listeners = c :: listeners}

  def removeListener(c: ChangeHandler) = synchronized {listeners -= c}

  protected def updateListeners() = synchronized {
    val ch = ChangeEvent(this)
    listeners.foreach(i => i.changed(ch))
  }
}

在定义trait的时候没有办法确定T的类型，但是混入类中之后就可以确定了
ChangeHandler类型使用了structural type结构类型，这样任何有这个方法签名的对象实例都可以注册到这个listener中
Structural typing in Scala is achieved via reflection. In practice, there is a 2.5x performance penalty for
dispatching a structurally typed method vs. invoking a method on an interface. For very tight loops,
this is meaningful. For 99 percent of your code, it’s not meaningful.

我们实现这样一个类：

class Foo extends Listener[Foo] {
  private var _count = 0
  def count = synchronized{_count}
  def inc = synchronized{
    _count += 1
    updateListeners()
  }
}

我们可以这样测试：
val f = new Foo
object Bar {
def changed(c: ChangeEvent[Foo]) {println("changed: "+c.on.count)}
}
f.addListener(Bar)
f.inc
f.inc

##### Traits and Class Hierarchies

trait可以带方法实现，trait可以对类型进行限制的更加严格：
def foo(bar: Baz with Blarg with FruitBat)

例如我们对生物进行模拟：
abstract class LivingThing
abstract class Plant extends LivingThing
abstract class Fungus extends LivingThing
abstract class Animal extends LivingThing

trait HasLegs extends Animal {
def walk() {println("Walking")}
}

HasLegs扩展了类Animal，这意味着什么？意味着编译器只允许你将HasLegs混入Animal的子类中。
The HasLegs trait extends Animal. But Animal is a class, so what does it mean for a trait to
extend a class? It means that the compiler will only let you mix HasLegs into something
which subclasses from Animal.

trait HasWings extends Animal {
def flap() {println("Flap Flap")}
}

只有HasWings的才可以使用fiy，可以使用HasWings =>定义self type
如果类中没有混入这个trait，编译器将会报错：
trait Flies {
	this: HasWings =>
	def fly() {println("I'm flying")}
}

所以下面的类会编译成功
abstract class Bird extends Animal with HasWings with HasLegs

Self types深入：
http://www.scala-lang.org/old/node/124

#### Types—It’s Beyond Generic

We’ve seen how Scala allows complex modeling and compile-time type checking that
allows us to build complex class hierarchies. What about things that make a developer’s
life easier? What about things that make a developer, a library consumer, think that Scala
is as easy and flexible as a scripting language? In this section, we’re going to write a database
abstraction layer that allows a developer to define a table mapping simply as follows:
现在将要创建一个数据库抽象层来简化程序员的工作，利用这个层我们可以这样定义一个表的映射：

class MyTable extends Table[MyTable] {
	val table = "mytable"
	val id = IntColumn("id")
	val name = StringColumn("name")
	val birthday = DateColumn("birthday")
	type ColumnTypes = (Int, String, Date)
	def columns = id ~ name ~ birthday
}

这个类可以这样定义查询
MyTable.findAll(By(MyTable.id, 33))
这样定义查询就会有编译错误：
MyTable.findAll(By(MyTable.id, "33"))

这段代码是这样的：
略

分析一下：

##### The Column

trait BasicColumn[TableType <: Table[TableType], T] {
	def default: T
	def name: String
	def getField(rs: ResultSet): T
	def set(st: PreparedStatement, offset: Int, value: T)
}

略，代码有点太长了


#### Variance

public class Bad {
	public static void main(String[] argv) {
		Object[] a = argv;
		a[0] = new Object();
	}
}
这是一段错误的java代码。String[] 是Object[]的子类，但是却不能加入一个Object类，这是很令人疑惑的。
Defining the type variance for type parameters allows you to control how parameterized
types can be passed to methods.
定义类型的变化将允许我们更好的控制方法的参数类型

Variance comes in three flavors: invariant, covariant, and contravariant.
变性分为：不变、协变和逆变三种

##### Invariant Parameter Types

不变参数类型
scala中Array[T]是不变的，所以Array[String]和Array[Object]没有类型关系
一般对任何可变的量，其类型参数都应该是不变的

class Holder[T](var data: T)
def add(in: Holder[Int]) {in.data = in.data + 1}

val nh = new Holder[Number](33.3d)
def round(in: Holder[Number]) {in.data = in.data.intValue}
round(nh) 是Ok的
但是val dh = new Holder(33.3d) 是不OK的，因为类型参数不匹配

##### Covariant Parameter Types

协变是在类型参数前加+实现的，协变参数对不变的容器类型是有用的
A covariant type is useful for read-only containers.

List就是，List[String]是List[Any]的子类

class Getable[+T](val data: T)
def get(in: Getable[Any]) {println("It's "+in.data)}
val gs = new Getable("String")
get(gs)

We can make read-only classes
covariant. I guess that means that contravariance is good for write-only classes.
既然协变适用于read-only的类，那么逆变应该适用于write-only的类

##### Contravariant Parameter Types

让我们看一个只有写接口的类
class Putable[-T] {
def put(in: T) {println("Putting "+in)}
}
def writeOnly(in: Putable[String]) {in.put("Hello")}

val p = new Putable[AnyRef]
writeOnly(p)

略

##### Rules of Variance

So, we’ve successfully defined and used an invariant type. The invariant type was mutable,
so it both returned and was called with a particular type. We created a convariant type
which was an immutable holder of a value. Finally, we created a transformer that had
contravariant input and covariant output. Wait, that sounds like a function. That’s right,
Scala’s FunctionN traits have contravariant parameters and covariant results. This leads us
to the simple rules of variance:

• Mutable containers should be invariant.
• Immutable containers should be covariant.
• Inputs to transformations should be contravariant, and outputs from
transformations should be covariant.

#### Poignantly Killing Bunnies: Dwemthy’s Stairs

略


### Chapter 8 Parsers—Because BNF Is Not Just for Academics Anymore

#### Higher-Order Functions and Combinators

Scala’s parser
combinator library gives us a view of a powerful DSL, and it has its roots in a lot of computer
science and mathematics.Let’s look at higher-order functions (functions that take functions
as parameters), then at how higher-order functions can be combined to yield
powerful functionality.

我们通过高阶函数来了解scala的parser combinator

##### Higher-Order Functions

高阶函数以函数为参数，如

List(1, 2, 3).map(_ + 1)

以及如何组合函数
def plus1(in: Int) = in + 1
def twice(in: Int) = in * 2
val addDouble = plus1 _ andThen twice
List(1,2,3).map(addDouble)

##### Combinators

As in the rest of this book, here I’m dealing with the practicalities of combinators. There is a lot
of theory and math behind combinators. This Wikipedia article touches on them: http://
en.wikipedia.org/wiki/Combinator.

“What’s a parser combinator?” you ask.1 A combinator is a function that takes only other
functions as parameters and returns only functions. Combinators allow you to combine
small functions into big functions. In the case of the parser combinator library, you can
combine small functions that match individual characters or small groups of characters
into bigger functions that can parse complex documents.

组合器就是将小的函数组合为大函数的函数

例如对输入Seq[Char]，输入要么是true、要么是false，我们可以这样写：
def parse = (elem('t') ~ elem('r') ~ elem('u') ~ elem('e')) |
	(elem('f') ~ elem('a') ~ elem('l') ~ elem('s') ~ elem('e'))
The first call to elem returns a function that will attempt to match
the first character in an input stream to the letter “t.” If the first letter of the input stream
matches, then the function returns Parsers.Success; otherwise it returns a Parsers.NoSuccess.

The ~ method is called “and then,”

Scala’s parser combinator library has implicit conversions from Char into
Parser[Char], so we can write
def p3: Parser[Boolean] = ('t' ~ 'r' ~ 'u' ~ 'e' ^^^ true) |
('f' ~ 'a' ~ 'l' ~ 's' ~ 'e' ^^^ false)
The ^^^ method on Parser says, “If we match the input, return this constant.”

要在scala文件中运行可以这样写：
import scala.util.parsing.combinator._

class A extends JavaTokenParsers {
  def p: Parser[Boolean] = ('t' ~ 'r' ~ 'u' ~ 'e' ^^^ true) |
    ('f' ~ 'a' ~ 'l' ~ 's' ~ 'e' ^^^ false)
}

object A extends A {
  def main(args: Array[String]) {
    println(parseAll(p, "false"))
  }
}

再看一些parser的例子
def positiveDigit = elem('1') | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
def digit = positiveDigit | '0'
def long1: Parser[Long] = positiveDigit ~ rep(digit) ^^ {
  case (first: Char) ~ (rest: List[Char]) => (first :: rest).mkString.toLong
}
The ^^ method on Parser causes the
conversion function to be applied if the predicate succeeds. In this example, I was explicit
about the types, but the type inferencer will get it right.
如果成功了则调用^^ 后的方法

lazy val long2: Parser[Long] = positiveDigit ~ rep(digit) ^? {
  case first ~ rest if rest.length < 18 => (first :: rest).mkString.toLong
}
^?表示如果成功了，毁掉这个方法，但是不一定有返回
lazy val
We’ve also changed from a method to a lazy val. This is because the method does not
do the parsing; rather, the method combines smaller Parsers into a single Parser. This
building of the Parser need only happen once, and the resulting Parser can be used over
and over, even simultaneously on multiple threads.

#### The Calculator Parser

But first, let’s define a utility trait that will allow us to more easily run the Parsers
from the Scala REPL. The RunParser trait can be mixed into any Parser and adds a run
method.
import scala.util.parsing.combinator._
trait RunParser {
	this: RegexParsers =>
	type RootType
	def root: Parser[RootType]
	def run(in: String): ParseResult[RootType] = parseAll(root, in)
}

The RunParser trait can be mixed into a class that extends RegexParsers. By mixing
RunParser into your Parser, you can type MyParser.run("Thing to test") and see the result.
It’s a convenience trait.

可以计算+-*/的计算器，BNF:
<sumExpr> ::= <prodExpr> [("+" <prodExpr>) | ("-" <prodExpr>)]
<prodExpr> ::= <factor> [("*" <factor>) | ("/" <factor>)]
<factor> ::= <float> | ("(" <sumExpr> ")")

解析部分：
import scala.util.parsing.combinator._

object CalcSkel extends JavaTokenParsers with RunParser {
  lazy val sumExpr = multExpr ~ rep("+" ~ multExpr | "-" ~ multExpr)
  lazy val multExpr = factor ~ rep("*" ~ factor | "/" ~ factor)
  lazy val factor: Parser[Any] = floatingPointNumber | "(" ~ sumExpr ~ ")"
  type RootType = Any
  def root = sumExpr
}

CalcSkel.run("1")
CalcSkel.run("1 + 1")
运行出来的奇怪符号是Java Language Specification规定的
http://java.sun.com/docs/books/jls/third_edition/html/j3TOC.html

然后我们将结果给出来：
import scala.util.parsing.combinator._

object Calc extends JavaTokenParsers with RunParser {

  lazy val sumExpr = prodExpr ~
  rep("+" ~> prodExpr ^^ (d => (x: Double) => x + d) |
      "-" ~> prodExpr ^^ (d => (x: Double) => x - d)) ^^ {
    case seed ~ fs => fs.foldLeft(seed)((a, f) => f(a))
  }

  lazy val prodExpr = factor ~
  rep("*" ~> factor ^^ (d => (x: Double) => x * d) |
      "/" ~> factor ^^ (d => (x: Double) => x / d)) ^^ {
    case seed ~ fs => fs.foldLeft(seed)((a, f) => f(a))
  }

  lazy val factor: Parser[Double] =
  floatingPointNumber ^^ (_.toDouble) | "(" ~> sumExpr <~ ")"

  type RootType = Double

  def root = sumExpr
}

#### JSON Parsing

略

#### Twitter JSON Parsing

略


















