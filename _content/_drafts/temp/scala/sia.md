


Chap 2
A closure is any function that closes
over the environment in which it’s defined. For example, closure will keep track of any
variable changes outside the function that are being referred to inside the function.

通过实现break 来介绍闭包
Listing 2.1 breakable, break, and install functions
val breakException = new RuntimeException("break exception")
def breakable(op: => Unit) {
	try {
	op
	} catch { case _ => }
}
def break = throw breakException
def install = {
	val env = System.getenv("SCALA_HOME")
	if(env == null) break
	println("found scala home lets do the real work")
}

breakable(install)
或
breakable {
	val env = System.getenv("SCALA_HOME")
	if(env == null) break
	println("found scala home lets do the real work")
}
In Scala if the last argument of a function is of function type, you can pass it as closure.
This syntax sugar is useful in creating DSLs.

Array方法
scala.collection.mutable.ArrayLike
Predef implicitly converts Array to scala.collection.mutable
.ArrayOps. ArrayOps is the subclass of ArrayLike, so ArrayLike is more like
the interface for all the additional methods available to Array type collections.

What is this scala.collection.immutable.$colon$colon?
scala> myList.getClass
res42: java.lang.Class[_] = class
scala.collection.immutable.$colon$colon
That’s because scala.collection.immutable.List is an abstract class, and it
comes with two implementations: the scala.Nil class and scala.::.

:: :+

6 “HttpClient Overview,” July 3, 2012, http://mng.bz/Q4Bf.
7 Roy Thomas Fielding, “Architectural Styles and the Design of Network-based Software Architectures,” doctoral
dissertation, University of California, Irvine, 2000. http://mng.bz/2Xa4.
8 Roy T. Fielding and Richard N. Taylor, “Principled Design of the Modern Web Architecture,” University of
California, Irvine, 2000, http://mng.bz/188g.

scala> val scalaList = List(1, 2, 3)
scalaList: List[Int] = List(1, 2, 3)
scala> val javaList = java.util.Arrays.asList(scalaList.toArray)
javaList: java.util.List[Array[Int]] = [[I@67826710]
scala> val javaList = java.util.Arrays.asList(scalaList.toArray:_*)
javaList: java.util.List[Int] = [1, 2, 3]

Listing 2.9 RestClient.scala 略

Chap4
class MongoClient(val host:String, val port:Int) {
private val underlying = new Mongo(host, port)
def this() = this("127.0.0.1", 27017)
}


How do you add a setter method to a class?
class Person(var firstName:String, var lastName:String,
	private var _age:Int) {
	def age = _age
	def age_=(newAge: Int) = _age = newAge
}

The assignment interpretation is interesting in
Scala, and it can mean different things based on context. For example, assignment
to a function application like f(args) = e is interpreted as f.update(args).

class MongoClientV2(val host:String, val port:Int)
	extends Mongo(host, port){
	require(host != null, "You have to provide a host name")
	def this() = this("127.0.0.1", 27017)
}
As shown in the previous example, you can also inline the definition of the primary
constructor of a superclass. One drawback of this approach is that you can no longer
validate the parameters of the primary constructor before handing it over to the
superclass.

The _root_ package in Scala
在嵌套package中才有用
rename import java.sql.{Date => SqlDate} 
hide   import java.sql.{Date => _ }

The Factory pattern in Scala： object

package com.scalainaction.mongo
import com.mongodb.{DB => MongoDB}
class DB private(val underlying: MongoDB) {
}
object DB {
def apply(underlying: MongoDB) = new DB(underlying)
}

91 Package object
The only things you can put in a package are classes, traits, and objects. But with
the help of the package object, you can put any kind of definition in a package,
such as a class.

package object bar {
val minimumAge = 18
def verifyAge = {}
}


package com.scalainaction.mongo
import com.mongodb.{DB => MongoDB}
import scala.collection.convert.Wrappers._
class DB private(val underlying: MongoDB) {
	def collectionNames = for(name <- new JSetWrapper(underlying.getCollectionNames)) yield name
}

使用
import com.scalainaction.mongo._
def client = new MongoClient
def db = client.createDB("mydb")
for(name <- db.collectionNames) println(name)

完整的mongodb 客户端：
Listing 3.3 Completed MongoClient.scala
package com.scalainaction.mongo
import com.mongodb._
class MongoClient(val host:String, val port:Int) {
	require(host != null, "You have to provide a host name")
	private val underlying = new Mongo(host, port)
	def this() = this("127.0.0.1", 27017)
	def version = underlying.getVersion
	def dropDB(name:String) = underlying.dropDatabase(name)
	def createDB(name:String) = DB(underlying.getDB(name))
	def db(name:String) = DB(underlying.getDB(name))
}

Listing 3.4 DB.scala
package com.scalainaction.mongo
import com.mongodb.{DB => MongoDB}
import scala.collection.convert.Wrappers._
class DB private(val underlying: MongoDB) {
	def collectionNames = for(name <- new JSetWrapper(underlying.getCollectionNames)) yield name
}
object DB {
	def apply(underlying: MongoDB) = new DB(underlying)
}

Mixin with Scala traits
Listing 3.5 ReadOnly collection trait
import com.mongodb.{DBCollection => MongoDBCollection }
import com.mongodb.DBObject
trait ReadOnly {
	val underlying: MongoDBCollection
	def name = underlying getName
	def fullName = underlying getFullName
	def find(doc: DBObject) = underlying find doc
	def findOne(doc: DBObject) = underlying findOne doc
	def findOne = underlying findOne
	def getCount(doc: DBObject) = underlying getCount doc
}
NOTE The difference between def and val is that val gets evaluated when an
object is created, but def is evaluated every time a method is called.

The DBObject parameter is nothing but a key-value map provided by the Mongo
Java driver, and you’re going to use the class directly. In the full-blown driver implementation,
you’ll probably want to wrap that class too, but for the toy driver you can
live with this bit of leaky abstraction.

Listing 3.6 Administrable and Updatable traits
trait Administrable extends ReadOnly {
	def drop: Unit = underlying drop
	def dropIndexes: Unit = underlying dropIndexes
}
trait Updatable extends ReadOnly {
	def -=(doc: DBObject): Unit = underlying remove doc
	def +=(doc: DBObject): Unit = underlying save doc
}

使用这些trait
class DBCollection(override val underlying: MongoDBCollection) extends ReadOnly

private def collection(name: String) = underlying.getCollection(name)
def readOnlyCollection(name: String) = new DBCollection(collection(name))
def administrableCollection(name: String) = new DBCollection(collection(name)) with Administrable
def updatableCollection(name: String) = new DBCollection(collection(name)) with Updatable

P97 改进后的代码：
Listing 3.7 Completed DB.scala
package com.scalainaction.mongo
import com.mongodb.{DB => MongoDB}
import scala.collection.convert.Wrappers._
class DB private(val underlying: MongoDB) {
	private def collection(name: String) = underlying.getCollection(name)
	def readOnlyCollection(name: String) = new DBCollection(collection(name))
	def administrableCollection(name: String) = new DBCollection(collection(name)) with Administrable
	def updatableCollection(name: String) = new DBCollection(collection(name)) with Updatable
	def collectionNames = for(name <- new JSetWrapper(underlying.getCollectionNames)) yield name
}
object DB {
	def apply(underlying: MongoDB) = new DB(underlying)
}

Listing 3.8 DBCollection.scala
package com.scalainaction.mongo
import com.mongodb.{DBCollection => MongoDBCollection }
import com.mongodb.DBObject
class DBCollection(override val underlying: MongoDBCollection) extends ReadOnly
trait ReadOnly {
	val underlying: MongoDBCollection
	def name = underlying getName
	def fullName = underlying getFullName
	def find(doc: DBObject) = underlying find doc
	def findOne(doc: DBObject) = underlying findOne doc
	def findOne = underlying findOne
	def getCount(doc: DBObject) = underlying getCount doc
}
trait Administrable extends ReadOnly {
	def drop: Unit = underlying drop
	def dropIndexes: Unit = underlying dropIndexes
}
trait Updatable extends ReadOnly {
	def -=(doc: DBObject): Unit = underlying remove doc
	def +=(doc: DBObject): Unit = underlying save doc
}

Listing 3.9 Test client for driver QuickTour.scala
import com.scalainaction.mongo._
import com.mongodb.BasicDBObject
def client = new MongoClient
def db = client.db("mydb")
for(name <- db.collectionNames) println(name)
val col = db.readOnlyCollection("test")
println(col.name)
val adminCol = db.administrableCollection("test")
adminCol.drop
val updatableCol = db.updatableCollection("test")
val doc = new BasicDBObject()
doc.put("name", "MongoDB")
doc.put("type", "database")
doc.put("count", 1)
val info = new BasicDBObject()
info.put("x", 203)
info.put("y", 102)
doc.put("info", info)
updatableCol += doc
println(updatableCol.findOne)
updatableCol -= doc
println(updatableCol.findOne)
for(i <- 1 to 100) updatableCol += new BasicDBObject("i", i)
val query = new BasicDBObject
query.put("i", 71);
val cursor = col.find(query)
while(cursor.hasNext()) {
	println(cursor.next());
}

P97 为了提高性能，将查询结果写在内存中，可以这样写： http://en.wikipedia.org/wiki/Memoization
trait Memoizer extends ReadOnly {
	val history = scala.collection.mutable.Map[Int, DBObject]()
	override def findOne = {
		history.getOrElseUpdate(-1, { super.findOne })
	}
	override def findOne(doc: DBObject) = {
		history.getOrElseUpdate(doc.hashCode, { super.findOne(doc) })
	}
}

使用时可以这样：
def readOnlyCollection(name: String) = new DBCollection(collection(name)) with Memoizer
def administrableCollection(name: String) = new DBCollection(collection(name)) with Administrable with Memoizer
def updatableCollection(name: String) = new DBCollection(collection(name)) with Updatable with Memoizer

3.6.1 Class linearization : how stackable traits are implemented in Scala
Trait class files on JVM
Depending on how you define a trait, the Scala compiler generates class files differ- ently. When you define a trait with only a method declaration and without any method body, it produces a Java interface. You could use javap –c <class file name> to inspect class files generated by Scala. For example, trait Empty { def e: Int } will produce the following class file:
public interface Empty{ public abstract int e();
}
When a trait declares concrete methods or code, Scala generates two class files: one for the interface (as shown in the previous code) and a new class file that contains the code. When a class extends a trait, the variables declared in the trait are copied to the class file, and the method defined in the trait becomes a façade method in the class. These façade methods in the class will call the methods defined in the trait code class.
trait会创建一个facade类

For the Query class, you’ll wrap your good old friend DBObject and expose meth- ods like sort, skip, and limit so that users can specify query options:
case class Query(q: DBObject, option: QueryOption = NoOption) {
  def sort(sorting: DBObject) = Query(q, Sort(sorting, option))
  def skip(skip: Int) = Query(q, Skip(skip, option))
  def limit(limit: Int) = Query(q, Limit(limit, option))
}
Here each method creates a new instance of a query object with an appropriate query option so that, like a fluent interface (http://martinfowler.com/bliki/Fluent Interface.html), you can chain the methods together as in the following:
var rangeQuery = new BasicDBObject("i", new BasicDBObject("$gt", 20))
var richQuery = Query(rangeQuery).skip(20).limit(10)


看看是怎么实现的：
trait ReadOnly {
  val underlying: MongoDBCollection
  def name = underlying getName
  def fullName = underlying getFullName
  //Find method takes query object
￼  def find(query: Query): DBCursor = {
    def applyOptions(cursor:DBCursor, option: QueryOption): DBCursor = {
      option match {
        case Skip(skip, next) => applyOptions(cursor.skip(skip), next)
        case Sort(sorting, next)=> applyOptions(cursor.sort(sorting), next) 
        case Limit(limit, next) => applyOptions(cursor.limit(limit), next) 
        case NoOption => cursor
￼      }
    }
    applyOptions(find(query.q), query.option)
  }
  
def find(doc: DBObject): DBCursor = underlying find doc 
def findOne(doc: DBObject) = underlying findOne doc 
def findOne = underlying findOne
def getCount(doc: DBObject) = underlying getCount doc
}

测试的代码
import com.scalainaction.mongo._
import com.mongodb.BasicDBObject
def client = new MongoClient
def db = client.db("mydb")
val col = db.readOnlyCollection("test")
val updatableCol = db.updatableCollection("test")
for(i <- 1 to 100) updatableCol += new BasicDBObject("i", i)
val rangeQuery = new BasicDBObject("i", new BasicDBObject("$gt", 20)) 
val richQuery = Query(rangeQuery).skip(20).limit(10)
val cursor = col.find(richQuery)
while(cursor.hasNext()) {
  println(cursor.next());
}

对case class
scala> case class Person(firstName:String, lastName:String)
可以使用带名称的参数
scala> val p = Person(lastName = "lastname", firstName = "firstname") 
p: Person = Person(firstname,lastname)

但是如果有重载，且名称改变了的话，则会报错：
scala> trait Person { def grade(years: Int): String }
defined trait Person
scala> class SalesPerson extends Person { def grade(yrs: Int) = "Senior" } defined class SalesPerson
scala> val s = new SalesPerson
s: SalesPerson = SalesPerson@42a6cdf5
scala> s.grade(yrs=1)
res17: java.lang.String = Senior
scala> s.grade(years=1)
<console>:12: error: not found: value years
       s.grade(years=1)
当然这里的原因是s的类型是SalesPerson，如果强制声明类型的话倒也不会出错
scala> val s: Person = new SalesPerson
s: Person = SalesPerson@5418f143
scala> s.grade(years=1)
res19: String = Senior
P107 

case class还有一个copy方法

109 modifier 略

3.10 value class为了给已有的类型添加新的特性
Nonetheless this is a very nice way to add extension methods to an existing type.
看到例子再说

3.11 使用隐世转换实现--> 达到range的效果
val oneTo10 = 1 --> 10
This will fail because there is no --> method defined for Int. We can easily fix this by following two simple steps:
 Create a type that has a --> method defined for the Int type 
 Provide an implicit conversion
Let’s create a class that defines a --> method for Int and creates a Range of integers:
scala> class RangeMaker(left: Int) {
| def -->(right: Int) = left to right |}
defined class RangeMaker
scala> val range: Range = new RangeMaker(1).-->(10)
range: Range = Range(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
定义好之后，使用implicit
implicit def int2RangeMaker(left: Int): Range = new RangeMaker(left)

由于隐世转换很常用，scala引入了隐世类型，下面代码可以达到刚刚的效果：
implicit class RangeMaker(left: Int) {
  def -->(right: Int): Range = left to right
}
Behind the scenes, the compiler will “desugar” the implicit class into a simple class and an implicit conversion method, as we did earlier. Note that implicit classes must have a primary constructor with one argument.
Looking up an appropriate implicit conversion takes time, but it’s not much of an issue because it happens at compile time. The only runtime cost comes from creating an additional instance of RangeMaker for each implicit conversion. The good news is that we can avoid the runtime cost by turning our implicit classes into value classes:
implicit class RangeMaker(val left: Int) extends AnyVal {
  def -->(right: Int): Range = left to right
}

P114-115 class hierarchy

















