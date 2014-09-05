---
layout: post
title: Scala程序设计 读书笔记
tagline: code better
tags : ["programming scala","scala"]
---

### Chapter 1 简介

30行代码：并发访问Web，分析收盘价

import scala.actors._
import Actor._

val symbols = List( "AAPL", "GOOG", "IBM", "JAVA", "MSFT")
val receiver = self
val year = 2008

symbols.foreach { symbol =>
  actor { receiver ! getYearEndClosing(symbol, year) }
}

val (topStock, highestPrice) = getTopStock(symbols.length)

printf("Top stock of %d is %s closing at price %f\n", year, topStock, highestPrice)

def getYearEndClosing(symbol : String, year : Int) = {
  val url = "http://ichart.finance.yahoo.com/table.csv?s=" +
    symbol + "&a=11&b=01&c=" + year + "&d=11&e=31&f=" + year + "&g=m"
    
  val data = io.Source.fromURL(url).mkString
  val price = data.split("\n")(1).split(",")(4).toDouble  
  (symbol, price)
} 

def getTopStock(count : Int) : (String, Double) = {
  (1 to count).foldLeft("", 0.0) { (previousHigh, index) =>
    receiveWithin(10000) {
      case (symbol : String, price : Double) =>
        if (price > previousHigh._2) (symbol, price) else previousHigh
    }
  }
} 

### Chapter 2 起步

scala可以直接当做脚本运行，甚至可以这样运行：scala -e "println(\"hello \" + args(0))" world

如果编译成了class文件，只要scala-library.jar加入了classpath中，也可以用java运行


### Chapter 3 步入正轨

#### RichString

scala字符串有时会被隐式转换为scala.runtime.RichString 这样就可以无缝使用capitalize、lines、reverse等方法
不过注意：
"mom".reverse == "mom"的结果是false，因为最终比较的是RichString和String的实例
"mom".reverse.toString == "mom" 才为true

"""可以创建RichString，不过会带上缩进，可以用stripMargin方法，会去掉|前所有的空白
创建正则表达式推荐使用RichString，比较一下"""\d2:\d2""" 和 "\\d2:\\d2"，那个更可读些？

#### 方法优先级

scala中没有运算符优先级？是的
那么2+3*4是怎么算的呢？

scala使用的是方法优先级，由方法名的第一个字符决定，下面从低到高列出了：
所有字符
|
^
&
< >
= !
:
+ -
* / %
所有其他特殊字符

#### scala 给 java程序员的惊喜

1. 赋值的运算结果是Unit，所以不要这样写：a=b=c
2. ==在scala中相当于equals，eq方法相当于java的==
3. ;是可选的，但是代码块的{之前必须加上（不过我们为什么要用代码块呢？）
4. scala的代码修饰符protected等与java不同
5. 避免显式return：def check : Boolean = return true 这里的方法返回类型如果不写，就会有编译错误

### Chapter 4 scala的类

### Chapter 5 自适应类型

关于Nothing
def madMethod() = { throw new IllegalArgumentException() }
println(getClass().getDeclaredMethod("madMethod", null).
  getReturnType().getName())


_* 可以将数组展开成离散值：
def max(values: Int*) = values.foldLeft(values(0)) { Math.max }
println(max(numbers: _*))

#### 类型参数的可变性

def playWithPets[T <: Pet](pets: Array[T]) = 
  println("Playing with pets: " + pets.mkString(", "))     

T <: Pet 表示T代表的类继承自Pet，即确定T的上界为Pet

def copyPets[S, D >: S](fromPets: Array[S], toPets: Array[D]) = { //...  
}
D >: S 表示D的下界为S，即S必须是D或者D的子类

### Chapter 6 函数值和闭包

#### 从普通函数迈向高级函数

一个求和函数，用java怎么实现？
  public int sum(int number) {
    int result = 0;
    for(int i = 1; i <= number; i++) {
      result += i; 
    }
    return result;
  }

如果在对其中的偶数计数呢？可以复制之前的方法体，修改一下
这是使用普通函数最好的结果了，但是会产生重复代码，降低重用性。

在scala中，可以把一个匿名函数作为参数传给另一个函数，有这个函数对范围进行循环。
这样传入不同的逻辑就可以完成不同的任务。这种以函数为参数的函数称为高阶函数。
这样做减少了重复代码，增强了可重用性，使代码简洁。
def totalResultOverRange(number: Int, codeBlock: Int => Int) : Int = {
  var result = 0
  for (i <- 1 to number) {
    result += codeBlock(i)    
  }
  
  result
}                         
println(totalResultOverRange(11, i => i))
println(totalResultOverRange(11, i => if (i % 2 == 0) 1 else 0))


foldLeft 可以简写为 /:
val array = Array(2, 3, 5, 1, 6, 4)
val sum = (0 /: array) { (sum, elem) => sum + elem }
val max = (Integer.MIN_VALUE /: array) { 
  (large, elem) => Math.max(large, elem)
}
还可以进一步简化：
val max = (Integer.MIN_VALUE /: array) { Math.max }

#### curry 化

是语法糖，把函数放在括号外传入

scala> def foo(a:Int)(b:Int)(c:Int) {}
foo: (a: Int)(b: Int)(c: Int)Unit

scala> foo _
res5: Int => (Int => (Int => Unit)) = <function1>

首先定义了一个函数foo，然后通过foo_创建一个偏应用函数【即函数有若干个参数未绑定】
它显示了三个串起来的转换，链中的每一个函数都接收一个Int，返回一个偏应用函数
不过最后一个返回Unit

#### 偏应用函数

绑定部分实参
def log(date: Date, message: String) { ... }
val logWithDateBound = log(new Date, _ : String)

先用一个值绑定date，上面的第二个参数_未绑定，结果为一个偏应用函数
scala实际会创建一个新类，有一个特殊的apply方法。

#### 闭包

到目前为止，用于函数和代码块的变量和值是绑定的，或者说你清楚知道它们是局部变量或是参数。
此外，还可以创建没有绑定变量的代码块。调用函数之前，必须绑定它们；不过，可以再局部变量和参数列表之外绑定变量。这就是闭包。

def loopThrough(number: Int)(closure: Int => Unit) {
  for (i <- 1 to number) { closure(i) }
}

var result = 0
val addIt = { value:Int => result += value }

loopThrough(10) { addIt } // result = 55

代码块绑定了一个外部的变量。绑定并不是获取变量值的一个副本，而是绑定到变量本身。

### Chapter 7 Trait和类型转换

#### 选择性混入

有一个trait
trait Friend {    
  val name: String
  def listen() = println("Your friend " + name + " is listening")
}             

类Cat并没有混入这个trait
class Cat(val name: String) extends Animal 

这段代码会报错：
val friend : Friend = new Cat("Alf") // ERROR

但是可以在创建实例的时候用with标记：
val snowy = new Cat("Snowy") with Friend
val friend : Friend = snowy

#### 用trait实现装饰模式

abstract class Check {
  def check() : String = "Checked Application Details..."
}

trait CreditCheck extends Check {
  override def check() : String = "Checked Credit..." + super.check()
}
trait EmploymentCheck extends Check {
  override def check() : String = "Checked Employment..." + super.check()
}
trait CriminalRecordCheck extends Check {
  override def check() : String = "Check Criminal Records..." + super.check()
}

在trait中通过super调用的方法会经历一个延迟绑定的过程。
这个并不是对基类的调用，而是对其左边混入的trait的调动；如果这个trait是混入的最左trait，则会解析为被混入类的方法。
val apartmentApplication = new Check with CreditCheck with CriminalRecordCheck
会输出：
Check Criminal Records...Checked Credit...Checked Application Details...

可以用trait混入横切关注点。无需大量类接口和层次就可以构建出高度可扩展的代码。

#### 隐式转换

import java.util._

class DateHelper(number: Int) {
  def days(when: String) : Date = {
    var date = Calendar.getInstance()
    when match {
      case "ago" => date.add(Calendar.DAY_OF_MONTH, -number)
      case "from_now" => date.add(Calendar.DAY_OF_MONTH, number)
      case _ => date
    }
    date.getTime()
  }
}

implicit def convertInt2DateHelper(number: Int) = new DateHelper(number)

val ago = "ago"              
val from_now = "from_now"

val past = 2 days ago
val appointment = 5 days from_now

println(past)     
println(appointment)

也可以写在一个类中，导入DateHelper时，scala自动在当前范围和导入范围找到转换器[注：scala一次至多应用一个隐式转换]
import java.util._

class DateHelper(number: Int) {                     
  def days(when: String) : Date = {
    var date = Calendar.getInstance()
    when match {
      case DateHelper.ago => date.add(Calendar.DAY_OF_MONTH, -number)
      case DateHelper.from_now => date.add(Calendar.DAY_OF_MONTH, number)
      case _ => date
    }
    date.getTime()
  }
}

object DateHelper {
  val ago = "ago"              
  val from_now = "from_now"
  
  implicit def convertInt2DateHelper(number: Int) = new DateHelper(number)
}

用法
import DateHelper._
val past = 2 days ago
val appointment = 5 days from_now

println(past)     
println(appointment)

### Chapter 8 使用容器

Set操作： ++ 并 ** 交
Map操作： filterKeys filter；get和apply的区别：get返回Option、apply抛异常

X()=b 等价于 X.update(b)；如果参数多余1个，可以将除了最后一个参数外的所有参数放在括号中X(a)=b 等价于 X.update(a, b)
val newFeeds1 = feeds.update("Venkat Subramaniam", "agiledeveloper.com/blog")

一般的，对可变的Map使用update比较有意义
val mutableFeeds = scala.collection.mutable.Map(
    "Scala Book Forum" -> "forums.pragprog.com/forums/87")
mutableFeeds("Groovy Book Forum") = "forums.pragprog.com/forums/55"

List操作：:: ::: filter forall exists map foldleft【/:()】

for定义：
for([pattern <- generator; definition*]+; filter*)
  [yield] expression

### Chapter 9 模式匹配和正则表达式

scala 可以使用java的enum，不过要像这样使用
object DayOfWeek extends Enumeration { 
  val SUNDAY = Value("Sunday")
  ...
}

def activity(day: DayOfWeek.Value) {                 
  day match {
    case DayOfWeek.SATURDAY => println("Hangout with friends")
    case _ => println("...code for fun...")
  }
}

activity(DayOfWeek.SATURDAY)
activity(DayOfWeek.MONDAY)

注意@可以给通配符命名
def processItems(items: List[String]) {
  items match {
    case List("red", "blue", _*) => println("colors red, blue, ... ")
    case List("apple", "orange", otherFruits @ _*) => 
      println("apples, oranges, and " + otherFruits)
  }
}

case语句中，小写字母开头是变量名定义，大写字母开头是常量，括号结尾是类
class Sample {
  val max = 100
  val MIN = 0

  def process(input: Int) {
    input match {
      case max => println("Don't try this at home") // Compiler error
      case MIN => println("You matched min")
      case _ => println("Unreachable!!")
    }
  }
}
可以改为
class Sample {
  val max = 100
  val MIN = 0

  def process(input: Int) {
    input match {
      case this.max => println("You matched max")
      case MIN => println("You matched min")
      case _ => println("Unmatched")
    }
  }
}

对空参数case类，需要带上括号，不然就会被认为是其伴生对象
import scala.actors._
import Actor._

case class Apple()
case class Orange()
case class Book ()

class ThingsAcceptor {
  def acceptStuff(thing: Any) {
    thing match {
      case Apple() => println("Thanks for the Apple")
      case Orange() => println("Thanks for the Orange")
      case Book() => println("Thanks for the Book")
      case _ => println("Excuse me, why did you send me a " + thing)
    }
  }
}

val acceptor = new ThingsAcceptor
acceptor.acceptStuff(Apple())
acceptor.acceptStuff(Book())
acceptor.acceptStuff(Apple)
注意最后一个调用没有带(),将会匹配_












