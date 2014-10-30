package chap1

object MyScala extends App {
println( "hello")
}

/*
用括号 等同与 apply 方法

创建随即文件的方式是 生成一个随机的BigInt 然后将它转换为36进制 的字符串

chap2
用:paste 实现提示符换行
print println printf

for循环 for(i <- 0 until s.length) print(s(i));
* scala 没有break和continue使用
* 
* 高级for
* for(i <- 1 to 3; from = 4 -i; j <- from to 3) print(10 * i + j)
* 13 22 23 31 32 33
* 
* ML and Haskell can infer the types of recursive function, using the Hindley-Milner algorithm.
* 用这个算法处理子类型的面向对象语言是一个科研命题
* 
* 默认参数
* def decorate(str: String, left: String = "[", right: String = "]") =
    left + str + right
* decorate("Hello") = "[Hello]"
* decorate("Hello", "<<<", ">>>")
* decorate("Hello", ">>>[")
* decorate(left = "<<<", str = "Hello", right = ">>>")
* decorate("Hello", right = "]<<<") // calls decorate("Hello", "[", "]<<<")
* 
* 变长参数
* def sum(args: Int*) = {
    var result = 0
    for (arg <- args.elements) result += arg
    result
}
*
* val s = sum(1, 4, 9, 16, 25)
* val s = sum(1 to 5) // Error
* val s = sum(1 to 5: _*) // Consider 1 to 5 as an argument sequence
* 
* 
* 定义过程
* def box(s : String) { // Look carefully: no =
*  val border = "-" * s.size + "--\n" 
* println(border + "|" + s + "|\n" + border)
}
* -------
  |Hello|
  -------
* 
* 延迟初始化
* val words = io.Source.fromFile("/usr/share/dict/words").mkString 
* // Evaluated as soon as words is defined 定义时取值
lazy val words = io.Source.fromFile("/usr/share/dict/words").mkString
*  // Evaluated the first time words is used 第一次使用时取值
def words = io.Source.fromFile("/usr/share/dict/words").mkString
*  // Evaluated every time words is used 每次被使用时取值
* 
* 
* 
* Chap 3
* 定长数组 java 中的array
* scala> new Array[Int](10)
res33: Array[Int] = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
* 
* scala> Array(12 , "asd")
res34: Array[Any] = Array(12, asd)
* 
* 变长数组 ArrayBuffer java中的ArrayList
* 尾端添加和删除是高效的
* import scala.collection.mutable.ArrayBuffer
* scala> val b = ArrayBuffer[Int]()
b: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer()
scala> b += 10
res38: b.type = ArrayBuffer(10)
scala> b += (2345)
res39: b.type = ArrayBuffer(10, 2345)
scala> b += (2,3,4,5)
res40: b.type = ArrayBuffer(10, 2345, 2, 3, 4, 5)
scala> b ++= Array(1,2);
res42: b.type = ArrayBuffer(10, 2345, 2, 3, 4, 5, 1, 2)
scala> b.trimEnd(5)
scala> b
res44: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(10, 2345, 2)
scala> 
scala> b.insert(2, 6)
scala> b
res46: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(10, 2345, 6, 2)
scala> b.insert(2, 1,2,3)
scala> b
res48: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(10, 2345, 1, 2, 3, 6, 2)
scala> b.remove(2,3)
scala> b
res50: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer(10, 2345, 6, 2)

* 区间
* 
* scala> 0 until 10
res51: scala.collection.immutable.Range = Range(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
scala> 0 until (10, 2)
res52: scala.collection.immutable.Range = Range(0, 2, 4, 6, 8)
scala> 0 until (10, 2) reverse
warning: there were 1 feature warning(s); re-run with -feature for details
res53: scala.collection.immutable.Range = Range(8, 6, 4, 2, 0)
* 
* 
* scala> (0 until 10).filter(_ % 2 == 0).map(2*_)
res55: scala.collection.immutable.IndexedSeq[Int] = Vector(0, 4, 8, 12, 16)
* 
* 常用方法
* max sum
* val b = ArrayBuffer(1, 7, 2, 9)
  val bSorted = b.sortWith(_ < _)
// b is unchanged; bSorted is ArrayBuffer(1, 2, 7, 9)
* 
* 此时b不变，要想改变b
* val a = Array(1, 7, 2, 9)
  scala.util.Sorting.quickSort(a)
// a is now Array(1, 2, 7, 9)
* 
* scala> a.mkString(" ");
res61: String = 1 7 2 9
* 
* 
* scala数组和java数组是一样的，可直接互换
* 但是可变数组需要一个隐式转换
* 
* ArrayBuffer to List接口
* import scala.collection.JavaConversions.asJavaList // Or simply import collection.JavaConversions._
  val commands = scala.collection.mutable.ArrayBuffer("ls", "-al", "/home/cay")
  val pb = new ProcessBuilder(commands)
* 
* List to ArrayBuffer 准确的说是Buffer
* import scala.collection.JavaConversions.asScalaBuffer
  val cmds : scala.collection.mutable.Buffer[String] = pb.commands()
// You can't use ArrayBuffer—the wrapped object is only guaranteed to be a Buffer
* 
* 
* 
* Chap 4
* scala> val m = Map(1 ->2, 2 ->4)
m: scala.collection.immutable.Map[Int,Int] = Map(1 -> 2, 2 -> 4)

* scala> m(2)
res76: Int = 4

scala> m(10)
java.util.NoSuchElementException: key not found: 10
* 
* scala> m.getOrElse(10, 0)
res78: Int = 0
* 
* scores("Bob") = 10 // Updates the existing value for the key "Bob", assuming scores is mutable 
* scores("Fred") = 7 // Adds a new key/value pair to scores (assuming it is mutable)
* scores += ("Bob" -> 10, "Fred" -> 7)
* scores -= "Alice"
* val newScores = scores + ("Bob" -> 10, "Fred" -> 7) // New map with update
* 
* var scores = ...
  scores = scores + ("Bob" -> 10, "Fred" -> 7)
* 
* 循环
* scores.keySet // A set such as Set("Bob", "Cindy", "Fred", "Alice")
* for (v <- scores.values) println(v) // Prints 10 8 7 10 or some permutation thereof
* 
* for ((k, v) <- map) yield (v, k)
* 
* TreeMap
* val scores = scala.collections.immutable.SortedMap("Alice" -> 10,
    "Fred" -> 7, "Bob" -> 3, "Cindy" -> 8)
* 
* LinkedMap
* val months = scala.collections.mutable.LinkedHashMap("January" -> 1,
    "February" -> 2, "March" -> 3, "April" -> 4, "May" -> 5, ...)
* 
* 互操作
* java Map to scala map
* import scala.collection.JavaConversions.mapAsScalaMap
  val scores: scala.collection.mutable.Map[String, Int] = new java.util.TreeMap[String, Int]

* property to scala map
  import scala.collection.JavaConversions.propertiesAsScalaMap
  val props: scala.collection.Map[String, String] = System.getProperties()
￼
* scala to java
import scala.collection.JavaConversions.mapAsJavaMap
import java.awt.font.TextAttribute._ // Import keys for map below 
* val attrs = Map(FAMILY -> "Serif", SIZE -> 12) // A Scala map 
* val font = new java.awt.Font(attrs) // Expects a Java map
* 
* 
* 元组 tuple
* scala> (1, "z", 12.3)
res83: (Int, String, Double) = (1,z,12.3)
* 
* 获取元祖信息
* scala> t _0
<console>:10: error: value _0 is not a member of (Int, String, Double)
scala> t _1
res87: Int = 1
scala> t _2
res88: String = z
scala> t _3
res89: Double = 12.3
* 
* 一次从元组中取多个值
* scala> val (first, second, _) = t
first: Int = 1
second: String = z

* 会忽略第三个参数
* 
* scala> "New York".partition(_.isUpper) 
res90: (String, String) = (NY,ew ork)

* 
* 使用zip方法绑定元组值
* scala> val pairs = Array("<", "-", ">").zip(Array(2, 10, 2))
pairs: Array[(String, Int)] = Array((<,2), (-,10), (>,2))
scala> for ((s, n) <- pairs) Console.print(s * n) // prints <<---------->>
<<---------->>
* 
* 可以将一个键集合和一个值集合组成一个map
* keys.zip(values).toMap
* 
* scala> "Hello" zip "World"
res96: scala.collection.immutable.IndexedSeq[(Char, Char)] = Vector((H,W), (e,o), (l,r), (l,l), (o,d))
* 
* 
* Chap 5
* class Person {
  var age = 0
}
* getter age
* setter age_=
* 
* fred.age
* fred.age = 21 // fred.age = (21)
* 
* takayuematoMacBook-Air:src gyqgd$ scalac chap1/Person.scala 
takayuematoMacBook-Air:src gyqgd$ ls chap1/
MyScala.scala   Person.class    Person.scala
takayuematoMacBook-Air:src gyqgd$ javap -private chap1.Person
Compiled from "Person.scala"
public class chap1.Person extends java.lang.Object{
    private int age;
    public int age();
    public void age_$eq(int);
    public chap1.Person();
}

* 
* 重定义getter setter
* class Person {
private var privateAge: Int = 0 // Make private and rename
    def age = privateAge
    def age_=(newValue: Int) {
if (newValue > privateAge) privateAge = newValue; // Can't get younger }
}
* 效果是age只能增大，不能减小
*  val fred = new Person
  fred.age = 30
  fred.age = 21
  println(fred.age) // 30
* 
* val的域 只有getter
*
*  
* 私有字段 private private[this]
* class Person {
private var name
def comesBefore(otherPerson : Person) = name < otherPerson.name ...
}
* 
* private[this] var name // access someObject.name is not allowed
* 
* java bean
* import scala.reflect.BeanProperty
* class Person {
    @BeanProperty var name : String = _
}
* 
* 
* class Person (@BeanProperty val name : String, @BeanProperty var age : Int){
}
* 
* takayuematoMacBook-Air:src gyqgd$ javap -private chap1.Person
Compiled from "Person.scala"
public class chap1.Person extends java.lang.Object{
    private final java.lang.String name;
    private int age;
    public java.lang.String name();
    public int age();
    public void age_$eq(int);
    public void setAge(int);
    public java.lang.String getName();
    public int getAge();
    public chap1.Person(java.lang.String, int);
}
* 
* 
* Chap 6
* 单例
* object Accounts {
    private var lastNumber = 0
    def newUniqueNumber() { lastNumber += 1; lastNumber }
}
* 
* 伴生对象
* 为对象提供类static方法
* 
*  对象直接扩展类或trait 来作为缺省对象Objects Extending a Class or Trait
* abstract class UndoableAction(val description: String) {
    def undo(): Unit
    def redo(): Unit
}
* object DoNothingAction extends UndoableAction("Do nothing") {
    override def undo() {}
    override def redo() {}
}
* val actions = Map("open" -> DoNothingAction, "save" -> DoNothingAction, ...) 
* // 表示还没有实现 Open and save not yet implemented
* 
* 
* apply 方法一般在伴生对象中定义 可以用来替代构造器
* Array("Mary", "had", "a", "little", "lamb")
* Array(Array(1, 7), Array(2, 9))
* 
* 应用程序对象
* object MyScala extends App {
println( "hello")
}
* 可以展示时间选项
* scala -Dscala.time chap1.MyScala
hello
[total 2ms]
* 
* scala用Enumeration trait替换enum对象
* object TrafficLightColor extends Enumeration {
    val Red, Yellow, Green = Value
}
* val Red = Value(0, "Stop")
val Yellow = Value(10) // Name "Yellow" 
* val Green = Value("Go") // ID 11
* 
* 
* 使用别名
* object TrafficLightColor extends Enumeration { 
* type TrafficLightColor = Value
val Red, Yellow, Green = Value
}
* 
* TrafficLightColor(0) // Calls Enumeration.apply
TrafficLightColor.withName("Red")
* 
* 
* 
* chap 7
* 包 略
* import
* 通配符 import java.net._
* 任何地方都可以引入声明
* 一次引入多个 import java.awt.(Color, Font)
* 
* 重命名
* import java.util.{HashMap => _, _}
  import scala.collection.mutable._
Now HashMap unambiguously refers to scala.collection.mutable.HashMap 
* since java.util.HashMap is hidden.
* 这样HashMap 无二义性的指向scala.collection.mutable.HashMap 
* 
* 隐式引入
* import java.lang._
  import scala._
  import Predef._
* 
* 
* chap 8  继承
* scala 类型检查和转换
* Scala                 Java
obj.isInstanceOf[Cl]    (Cl) obj or null if obj is not an instance of C
obj instanceof Cl       classOf[Cl]
obj.asInstanceOf[Cl]    Cl.class
* 
* 不过在scala中最好使用模式匹配
* p match {
  case s: Student => ... // Process s as a Student 
  case _ => ... // p wasn't a Student
}
* 
* 构造顺序 与 提前定义
* class Creature {
    val range: Int = 10
    val env: Array[Int] = new Array[Int](range)
}
* class Ant extends Creature {
    override val range = 2
}
* class Bug extends { override val range = 3
} with Creature
* 
* P100 scala 继承层级
* scala的equals方式是Any 类定义的，参数是Any
* 
* 
* 
* chap 9
* import scala.io.Source
  val source = Source.fromFile("myfile.txt", "ISO-8859-1")
// The first argument can be a string or a java.io.File
// You can omit the encoding if you know that the file uses the default platform encoding 
* val lines = source.getLines
* for (l <- lines) process l
*  
* val lines = source.getLines.toArray
  val contents = source.mkString
* 
* val source1 = Source.fromURL("http://horstmann.com", "UTF-8")
  val source2 = Source.fromString("Hello, World!")
// Reads from the given string—useful for debugging 
 * val source3 = Source.stdin // Reads from standard input
* 
* 二进制读取要用java的类和方法
* 写入也要用java的PrintWriter
* 
* out.printf 需要AnyRef 类型
* 可以使用String format 方法
* out.printf("%6d %10.2f",
quantity.asInstanceOf[AnyRef], price.asInstanceOf[AnyRef]) // Ugh
* 
* out.print("%6d %10.2f".format(quantity, price))
* 
* 
* 文件系统访问可以使用java.nio,file.Files 来处理
* import java.nio.file._
  implicit def makeFileVisitor(f: (Path) => Unit) = new SimpleFileVisitor[T] {
    override def visitFile(p: Path, attrs: attribute.BasicFileAttributes) = {
      f(p)
      FileVisitResult.CONTINUE
    }
}
* Files.walkFileTree(dir.toPath, (f: Path) => println(f))
* 
* scala shell 很简单
* import sys.process._
* "ls -al .." ! // 打印控制台
* val result = "ls -al .." !! // 获取字符串
* "ls -al .. #| grep a" ! // 管道
* 
* 重定向
* #> #>> <# <##
* 
* 可以用scala的ProcessBuilder
* val p = Process(cmd, new File(dirname), ("LANG", "en_US"))
* "echo 42" #| p!
* 
* 
* regex
* 只要调用string的r方法就可以获得其Regex对象
* 最好使用原始字符串
* val wsnumwsPattern = """\s+[0-9]+\s+""".r
// A bit easier to read than "\\s+[0-9]+\\s+".r
* 
* for (matchString <- numPattern.findAllIn("99 bottles, 98 bottles")) 
  process matchString
* 
* val matches = numPattern.findAllIn("99 bottles, 98 bottles").toArray
    // Array(99, 98)
* 
*  val m1 =  wsnumwsPattern.findFirstIn("99 bottles, 98 bottles")
    // Some(" 98 ") // 结果是Option[String]
* 
* numPattern.findPrefixOf("99 bottles, 98 bottles")
    // Some(99)
  wsnumwsPattern.findPrefixOf("99 bottles, 98 bottles")
    // None
* 
*  numPattern.replaceFirstIn("99 bottles, 98 bottles", "XX")
    // "XX bottles, 98 bottles"
  numPattern.replaceAllIn("99 bottles, 98 bottles", "XX")
    // "XX bottles, XX bottles"
* 
* 匹配组 需要提取器
* val numitemPattern = "([0-9]+) ([a-z]+)".r
val numitemPattern(num, item) = "99 bottles" // sets num to "99", item to "bottles"
for (numitemPattern(num, item) <- numitemPattern.findAllIn("99 bottles, 98 bottles")) process num and item
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
*/
