gc

一个博客的jvm分类
http://shellblog.sinaapp.com/?cat=171

有一些系列文章深入浅出的介绍了java的内存机制
Java HotSpot VM中的JIT编译

Java HotSpot VM中的JIT编译
成为Java GC专家系列(1) ——Java垃圾回收机制
成为Java GC专家系列(2) ——监控Java垃圾回收
成为Java GC专家系列(3) ——如何优化Java垃圾回收
成为Java GC专家系列(4) ——Apache的MaxClients参数对系统的影响

JVM调优总结
图解Java GC 日志
聊聊JVM的年轻代
聊聊内存泄露

还有很好的连接
http://www.cubrid.org/blog/dev-platform/understanding-jvm-internals/
http://www.cubrid.org/blog/dev-platform/how-to-monitor-java-garbage-collection/
Understanding JVM Internals

http://www.cubrid.org/blog/tags/Java/


http://docs.oracle.com/javase/specs/#1732

Getting Started with the G1 Garbage Collector
http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/G1GettingStarted/index.html

Java HotSpot Garbage Collection
http://www.oracle.com/technetwork/java/javase/tech/index-jsp-140228.html

Java SE 6 HotSpot[tm] Virtual Machine Garbage Collection Tuning
http://www.oracle.com/technetwork/java/javase/gc-tuning-6-140523.html


http://stackoverflow.com/questions/895444/java-garbage-collection-log-messages

tomcat查看GC
http://blog.csdn.net/jimmy1980/article/details/4968308


http://lydawen.iteye.com/blog/1049244

2246.940: [GC 2246.940: [DefNew: 105861K->1270K(118016K), 0.0074023 secs] 235056K->130464K(511232K), 0.0076789 secs] [Times: user=0.01 sys=0.00, real=0.02 secs] 
2254.701: [Full GC (System) 2254.701: [Tenured: 129194K->129633K(393216K), 0.5826171 secs] 148077K->129633K(511232K), [Perm : 71213K->71213K(71424K)], 0.5829274 secs] [Times: user=0.58 sys=0.00, real=0.58 secs]
 
上述如果对GC稍有了解，基本能看出：红色表示GC发生的时间，DefNew 是指年轻代的GC;而第二行的Tenured是老年代的GC，Perm 是永久区，也就是存放class信息的区域，其它参数，通过文档可能了解各项参数代表的含义。上述日志基本可以对应如下格式：
 
 [GC [<collector>: <starting occupancy1> -> <ending occupancy1>, <pause time1> secs] <starting occupancy3> -> <ending occupancy3>, <pause time3> secs]
<collector>                      GC收集器的名称
<starting occupancy1>    新生代在GC前占用的内存
<ending occupancy1>     新生代在GC后占用的内存
<pause time1>                 新生代局部收集时jvm暂停处理的时间
<starting occupancy3>    JVM Heap 在GC前占用的内存
<ending occupancy3>     JVM Heap 在GC后占用的内存
 <pause time3>                GC过程中jvm暂停处理的总时间

Java GC 垃圾回收 性能优化  
http://blog.163.com/jackswu@yeah/blog/static/140629123201112513324705/

★JVM的内存 
　　在Java虚拟机规范中（具体章节请看这里），提及了如下几种类型的内存空间：
　　◇栈内存（Stack）：每个线程私有的。
　　◇堆内存（Heap）：所有线程公用的。
　　◇方法区（Method Area）：有点像以前常说的“进程代码段”，这里面存放了每个加载类的反射信息、类函数的代码、编译时常量等信息。
　　◇原生方法栈（Native Method Stack）：主要用于JNI中的原生代码。

一个简单的案例
http://passover.blog.51cto.com/2431658/715294


java中 堆和栈的区别
http://www.javatutorialhub.com/java-stack-heap.html
Java Tutorial 16 – Stack and Heap
Read more at http://www.javatutorialhub.com/java-stack-heap.html#ZGQFJU5jLZBQUt8p.99

Points to Remember:
When a method is called , a frame is created on the top of stack.
Once a method has completed execution , flow of control returns to the calling method and its corresponding stack frame is flushed.
Local variables are created in the stack
Instance variables are created in the heap & are part of the object they belong to.
Reference variables are created in the stack.

中文介绍
http://www.cnblogs.com/whgw/archive/2011/09/29/2194997.html
http://uule.iteye.com/blog/1417299
http://blog.csdn.net/lyerliu/article/details/6311709



http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html

http://javarevisited.blogspot.com/2013/01/difference-between-stack-and-heap-java.html
http://programmers.stackexchange.com/questions/65281/stack-and-heap-memory-in-java


