### 背景介绍

> The world is concurrent
Things in the world don't share data
Things communicate with messages
Things fail"

> —— Joe Armstrong

性能是所有程序开发人员无法回避的问题，在众多提升性能的手段中，使程序并行化是其中最有效的手段之一。然而优秀的性能表现并非能免费获得，即便在当今的多核时代，一个单线程程序也不会因部署到多核机器上而使性能自动提升。然而对于大多数主流编程语言来讲，编写一个多线程程序是会有很多坑的，比如死锁、资源分配、状态共享、调试、异步调用等。

举一个java的例子，我们要实现一个读写锁，一开始的实现可能是这样的：

	public class ReadWriteLock{
		private int readers = 0;
		private int writers = 0;
		private int writeRequests = 0;
	
		public synchronized void lockRead() 
			throws InterruptedException{
			while(writers > 0 || writeRequests > 0){
				wait();
			}
			readers++;
		}
	
		public synchronized void unlockRead(){
			readers--;
			notifyAll();
		}
	
		public synchronized void lockWrite() 
			throws InterruptedException{
			writeRequests++;
	
			while(readers > 0 || writers > 0){
				wait();
			}
			writeRequests--;
			writers++;
		}
	
		public synchronized void unlockWrite() 
			throws InterruptedException{
			writers--;
			notifyAll();
		}
	}

嗯，看上去不是很简单嘛，小case

等等，这个代码没有考虑读锁的重入，哦，还有写锁的重入，对了，读锁是不是可以升级到写锁，写锁应该也能够降回读锁吧，考虑了这些因素后，简化过来的代码是这样的：

	public class ReadWriteLock{
		private Map<Thread, Integer> readingThreads =
			new HashMap<Thread, Integer>();
	
		private int writeAccesses    = 0;
		private int writeRequests    = 0;
		private Thread writingThread = null;
	
		public synchronized void lockRead() 
			throws InterruptedException{
			Thread callingThread = Thread.currentThread();
			while(! canGrantReadAccess(callingThread)){
				wait();
			}
	
			readingThreads.put(callingThread,
				(getReadAccessCount(callingThread) + 1));
		}
	
		private boolean canGrantReadAccess(Thread callingThread){
			if(isWriter(callingThread)) return true;
			if(hasWriter()) return false;
			if(isReader(callingThread)) return true;
			if(hasWriteRequests()) return false;
			return true;
		}
	
	
		public synchronized void unlockRead(){
			Thread callingThread = Thread.currentThread();
			if(!isReader(callingThread)){
				throw new IllegalMonitorStateException(
					"Calling Thread does not" +
					" hold a read lock on this ReadWriteLock");
			}
			int accessCount = getReadAccessCount(callingThread);
			if(accessCount == 1){ 
				readingThreads.remove(callingThread); 
			} else { 
				readingThreads.put(callingThread, (accessCount -1));
			}
			notifyAll();
		}
	
		public synchronized void lockWrite() 
			throws InterruptedException{
			writeRequests++;
			Thread callingThread = Thread.currentThread();
			while(!canGrantWriteAccess(callingThread)){
				wait();
			}
			writeRequests--;
			writeAccesses++;
			writingThread = callingThread;
		}
	
		public synchronized void unlockWrite() 
			throws InterruptedException{
			if(!isWriter(Thread.currentThread()){
			throw new IllegalMonitorStateException(
				"Calling Thread does not" +
				" hold the write lock on this ReadWriteLock");
			}
			writeAccesses--;
			if(writeAccesses == 0){
				writingThread = null;
			}
			notifyAll();
		}
	
		private boolean canGrantWriteAccess(Thread callingThread){
			if(isOnlyReader(callingThread)) return true;
			if(hasReaders()) return false;
			if(writingThread == null) return true;
			if(!isWriter(callingThread)) return false;
			return true;
		}
	
	
		private int getReadAccessCount(Thread callingThread){
			Integer accessCount = readingThreads.get(callingThread);
			if(accessCount == null) return 0;
			return accessCount.intValue();
		}
	
	
		private boolean hasReaders(){
			return readingThreads.size() > 0;
		}
	
		private boolean isReader(Thread callingThread){
			return readingThreads.get(callingThread) != null;
		}
	
		private boolean isOnlyReader(Thread callingThread){
			return readingThreads.size() == 1 &&
				readingThreads.get(callingThread) != null;
		}
	
		private boolean hasWriter(){
			return writingThread != null;
		}
	
		private boolean isWriter(Thread callingThread){
			return writingThread == callingThread;
		}
	
		private boolean hasWriteRequests(){
			return this.writeRequests > 0;
		}
	}

现在心中是不是有一种万马奔腾的感觉？好像打开潘多拉魔盒，一个读写锁竟然能衍生出这么多问题。（例子来源于[这里](http://tutorials.jenkov.com/java-concurrency/read-write-locks.html)，中文翻译来自[这里](http://ifeve.com/read-write-locks/)）

问题的关键就是大部分语言的并发模型是共享内存（shared-memory）型的，这种方式需要对共享的数据访问进行很多限制。于是计算机科学家开始找寻另外一种解决方案，即消息传递（message-passing）方式——Actor模型由此诞生。

Actor这个模型由Carl Hewitt在1973年提出，[核心理念](http://www.infoq.com/cn/news/2009/01/actor-in-groovy/)是：

- 系统中的所有事物都可以扮演一个Actor
- Actor之间完全独立
- 在收到消息时Actor所采取的所有动作都是并行的，在一个方法中的动作没有明确的顺序
- Actor由标识和当前行为描述
- Actor可能被分成原始（primitive）和非原始（non primitive）类别
- 非原始Actor有：
  - 由一个邮件地址表示的标识
  - 当前行为由一组知识（acquaintances）（实例变量或本地状态）和定义Actor在收到消息时将采取的动作组成
- 消息传递是非阻塞和异步的，其机制是邮件队列（mail-queue）
- 所有消息发送都是并行的

虽然Actor概念题的比较早，但由于当时对并发、分布式需求还没有今天这么普遍，当时可谓英雄无用武之地。后来Joe Armstrong根据个人兴趣发明了Erlang就天生支持Actor模型，并在通信领域大获成功。

Erlang是一个结构化，动态类型编程语言，内建并行计算支持。使用Erlang编写出的应用运行时通常由成千上万个轻量级进程组成，并通过消息传递相互通讯。进程间上下文切换对于Erlang来说仅仅只是一两个环节，比起C程序的线程切换要高效得多了。

另外一个使用Erlang的优势是编写分布式应用要简单的多，因为它的分布式机制是透明的：对于程序来说并不知道自己是在分布式运行。

文章开头引用的[英文](http://www.zhihu.com/question/24717624)就是Joe Armstrong关于Erlang的总结，意思是“我们的世界是并行的。世间万物并不共享数据，它们仅通过消息传递进行通讯，并且随时可能发生故障。”关于actor模型的错误处理机制可以参考他的演讲[《Fault Tolerance 101》](http://www.infoq.com/presentations/fault-tolerance-101-erlang)

### Akka

坦白说，Erlang作为一门新的语言，对于java程序员【其实是大部分程序员】来说还是有学习难度的，（尤其是“单次赋值” 每个变量只能跟数据绑一次，所以，不像一般程序设计语言的变量可以多次指定为不同的值。）幸运的是，已经有很多人将Actor模型移植到[其他语言](http://en.wikipedia.org/wiki/Actor_model#Actor_libraries_and_frameworks)中，而在jvm上，[Akka](akka.io)就是事实的标准。

作为Actor模型的一个卓越实现，Akka提供了这些特性：

- Actors
  - 对并发/并行程序的简单的、高级别的抽象。
  - 异步、非阻塞、高性能的事件驱动编程模型。
  - 非常轻量的事件驱动处理（1G内存可容纳约270万个actors）。
- 容错性
  - 使用“let-it-crash”语义和监管者树形结构来实现容错。非常适合编写永不停机、自愈合的高容错系统。监管者树形结构可以跨多个JVM来提供真正的高容错系统。
- 位置透明性
  - Akka的所有元素都为分布式环境而设计：所有actor都仅通过发送消息进行互操作，所有操作都是异步的。
- 事务性actors
  - 事务性Actor是actor与STM(Software Transactional Memory)的组合。它使你能够使用自动重试和回滚来组合出原子消息流。

Akka已经成功运用在多个领域，任何需要高吞吐率和低延迟的系统都可以考虑使用Akka。

如果要详细介绍Akka的话，需要一整本书的内容，这里将引用一个简单的例子，让大家能够管窥Akka的强大。Akka目前的版本是2.3.5，中文资料不多，广谈翻译的[akka文档](http://www.gtan.com/akka_doc/index.html)版本是2.0的，其中的很多内容仍然是值得参考的。

这里将引用的是其中的[Pi值计算的例子](http://www.gtan.com/akka_doc/intro/getting-started-first-scala.html)【这里将介绍核心代码，详细步骤和API介绍请参考原链接。】

#### 需求

计算PI是一项CPU密集的操作，我们将使用Akka Actor来编写一个可以垂直扩展到多个处理器核上的并发解决方案。算法公式如下：

![](http://www.gtan.com/akka_doc/_images/pi-formula.png)

#### 设计

我们要做的设计是由一个`主`actor来启动整个计算过程，创建一组`工作`actor. 整个工作会被分割成具体的小段, 各小段会以轮转（round-robin）的方式发送到不同的`工作`actor. `主`actor等待所有的工作actor完全各自的工作并将其回送的结果进行汇总。当计算完成以后，`主`actor将结果发送给 `监听器`actor, 由它来输出结果。

#### 代码详解

##### 消息

根据设计需求，我们需要4种不同的消息：

- Calculate – 发送给 主 actor 来启动计算。
- Work – 从 主 actor 发送给各 工作 actor，包含工作分配的内容。
- Result – 从 工作 actors 发送给 主 actor，包含工作actor的计算结果。
- PiApproximation – 从 主 actor发送给 监听器 actor，包含pi的最终计算结果和整个计算耗费的时间。

发送给actor的消息应该永远是不可变的，以避免共享可变状态。scala的消息代码是这样的：

	sealed trait PiMessage
	case object Calculate extends PiMessage
	case class Work(start: Int, nrOfElements: Int) extends PiMessage
	case class Result(value: Double) extends PiMessage
	case class PiApproximation(pi: Double, duration: Duration)

下面我们由底向上看一下各个Actor的代码

##### Worker

其功能是，接收到`Work`消息，就了解到起始的下标值和需要累加的总元素数，我们只要按照之前算法规定的公式求和，得到结果。然后使用`Result`将结果返回即可。

	class Worker extends Actor {
	  def calculatePiFor(start: Int, nrOfElements: Int): Double = {
	    var acc = 0.0
	    for (i ← start until (start + nrOfElements))
	      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
	    acc
	  } 
	  def receive = {
	    case Work(start, nrOfElements) ⇒
	      sender ! Result(calculatePiFor(start, nrOfElements))
	  }
	}

##### 主actor

主actor会稍微复杂一些。 在它的构造方法里我们创建一个round-robin的路由器来简化将工作平均地分配给工作actor们的过程，先做这个:

	val workerRouter = context.actorOf(
	  Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "workerRouter")

现在我们有了一个路由，可以在一个单一的抽象中表达所有的工作actor。现在让我们创建主actor. 传递给它三个整数变量：

- nrOfWorkers – 定义我们会启动多少工作actor
- nrOfMessages – 定义会有多少整数段发送给工作actor
- nrOfElements – 定义发送给工作actor的每个整数段的大小

主actor获取了这些设置之后，其功能就是收到开始消息后，创建Worker执行计算工作；收到结果就进行累加，知道所有的Worker都完成。完整代码如下：

	  class Master(nrOfWorkers: Int, nrOfMessages: Int, nrOfElements: Int, listener: ActorRef) extends Actor {
	 
	    var pi: Double = _
	    var nrOfResults: Int = _
	    val start: Long = System.currentTimeMillis
	 
	    val workerRouter = context.actorOf(
	      Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "workerRouter")
	 
	    def receive = {
	      case Calculate ⇒
	        for (i ← 0 until nrOfMessages) workerRouter ! Work(i * nrOfElements, nrOfElements)
	      case Result(value) ⇒
	        pi += value
	        nrOfResults += 1
			// 计算完成，发送结果和总时间
	        if (nrOfResults == nrOfMessages) {
	          listener ! PiApproximation(pi, duration = (System.currentTimeMillis - start).millis)
	        }
	    }
	  }

##### 结果监听者

监听者很简单，当它接收到从 Master发来的PiApproximation ，就将结果打印出来并关闭整个 Actor系统。

	class Listener extends Actor {
	  def receive = {
	    case PiApproximation(pi, duration) ⇒
	      println("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s"
	        .format(pi, duration))
	      context.system.shutdown()
	  }
	}

##### 程序入口

入口就是创建主actor和结果监听者，并启动系统。

	object Pi extends App {
	    val system = ActorSystem("PiSystem")
	    val listener = system.actorOf(Props[Listener], name = "listener")
	    val master = system.actorOf(Props(
		  new Master(nrOfWorkers = 4, nrOfElements = 10000, nrOfMessages = 10000, listener)
	      ), name = "master")
	    master ! Calculate
	  }
	}

可以看到，通过修改nrOfWorkers，我们就能增加actor，而Akka会自动找到空闲的cpu并利用它执行actor的任务，很方便的实现了并行任务的横向扩展。

### 使用Akka构建分布式的日志服务

鉴于Akka功能这么强大，移动联盟就尝试使用它构建了一个分布式的日志服务，结果发现构建这样的服务基本是信手拈来，而且具有很好的扩展性。

##### 需求

移动联盟的SDKServer每天都从移动联盟的SDK中接收到各种信息，并把这些信息落地，每天生成的日志大小有近60GB，为了能够快速的响应变化，使用Akka技术构建了一个简单的离线分析工具。




利用Akka的


http://forum.scalachina.com/forum.php?mod=viewthread&tid=3714
http://www.gtan.com/akka_doc/intro/why-akka.html
http://www.gtan.com/akka_doc/intro/getting-started-first-scala.html
http://blog.csdn.net/jmppok/article/details/17264495
http://www.infoq.com/cn/news/2009/01/actor-in-groovy/
http://blog.madhukaraphatak.com/scala-for-android/




在 `main/script` 目录下，可以直接运行

	scala script.scala

但是不能运行 `scalac script.scala`，因为`script.scala`没有**类**定义

### scala 的编译模式

在 `main/scala` 目录下 `Hello.scala`

	object Hello extends App {
	  println("hello")
	}

这是一个拥有`main`方法的scala类，可以编译运行