java gc tuning

https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/

1 Introduction

A garbage collector (GC) is a memory management tool. It achieves automatic memory management through the following operations:

* Allocating objects to a young generation and promoting aged objects into an old generation.
为新生代分配内存，将老对象移至老生代

* Finding live objects in the old generation through a concurrent (parallel) marking phase. The Java HotSpot VM triggers the marking phase when the total Java heap occupancy exceeds the default threshold. See the sections Concurrent Mark Sweep (CMS) Collector and Garbage-First Garbage Collector.
当java堆大小超过限额时，触发并发标记查找年老代中的存活对象。

* Recovering free memory by compacting live objects through parallel copying. See the sections The Parallel Collector and Garbage-First Garbage Collector
通过并发拷贝和压缩存活对象来释放空间

一般不需要关注jvm gc，但是对大型、多线程具有高吞吐量的应用就必须关注了
particularly those with large amounts of data (multiple gigabytes), many threads, and high transaction rates.


Figure 1-1 Comparing Percentage of Time Spent in Garbage Collection

表示如果对单核机器，1%的时间被消耗在gc上，则对32核机器要消耗20%的吞吐；单核10%，则32核要耗掉75%的吞吐





2 Ergonomics
工效学; 人类工程学

Behavior-Based Tuning
java提供了两个目标：最小化暂停时间 和 最大化吞吐量

The application requires a heap large enough to at least hold all of the live data. In addition, a minimum heap size may preclude reaching these desired goals.


通过设定 Maximum Pause Time Goal 要限制最长的暂停时间
-XX:MaxGCPauseMillis=<nnn> 单位是 milliseconds

These adjustments may cause garbage collector to occur more frequently, reducing the overall throughput of the application. The garbage collector tries to meet any pause time goal before the throughput goal. In some cases, though, the desired pause time goal cannot be met.


Throughput Goal -XX:GCTimeRatio=<nnn>.例如该值为19，表示1/20的时间要被用在gc上
The time spent in garbage collection is the total time for both the young generation and old generation collections combined. If the throughput goal is not being met, then the sizes of the generations are increased in an effort to increase the time that the application can run between collections.

Footprint Goal
如果前两个目标达成，则jvm会尽量减少堆内存的大小

The pressure to achieve a throughput goal (which may require a larger heap) competes with the goals for a maximum pause time and a minimum footprint (which both may require a small heap).




3 Generations

java将内存分配和垃圾回收的复杂操作对开发者屏蔽了，但是当垃圾回收成为瓶颈时，还是需要理解底层的机制。垃圾回收器认为，通过调整一些参数来提高性能，而不需要修改抽象逻辑。

什么是垃圾 —— 不会被运行程序的任何指针指向的对象。
最简单的垃圾收集方式就是遍历可达对象，标记不可达的为垃圾，该方法时间复杂度与活跃对象数成正比，所以当存活对象很多时，会很慢。

现在虚拟机会进行分代收集，原理是大部分对象生命周期很短
Figure 3-1 Typical Distribution for Lifetimes of Objects

例如Iterator对象生命周期一般都很短
Iterator objects, for example, are often alive for the duration of a single loop.

大部分对象在年轻代
当年轻代满了，触发minor collection，如果年轻代中有很多垃圾，则minor collection会很快
然后存活对象会被转移到tenured generation
当tenured generation满了，就会触发major collection，整个堆都会被收集。


If garbage collection becomes a bottleneck, you will most likely have to customize the total heap size as well as the sizes of the individual generations. Check the verbose garbage collector output and then explore the sensitivity of your individual performance metric to the garbage collector parameters.
一般需要修改不同代大小即可

Figure 3-2 Default Arrangement of Generations, Except for Parallel Collector and G1

年轻代 有eden 和两个survivor 区，大部分对象在eden中分配，任何时候都会有一个survivor 区是空的


Performance Considerations
垃圾收集有两个性能度量
* Throughput 内存分配（一般不用优化分配）和垃圾收集占的时间
* Pauses 世界停止了

Footprint is the working set of a process, measured in pages and cache lines.
对内存和处理器受限系统比较重要

Promptness is the time between when an object becomes dead and when the memory becomes available, an important consideration for distributed systems, including Remote Method Invocation (RMI).


一般地，为不同代分配不同大小，需要折中
例如，年轻代很大，吞吐量达，但是暂停时间 footprint, promptness 受影响
缩小年轻代，会减少暂停时间，但是吞吐量受限

一个代的大小，不影响另一个带的回收效率
The sizing of one generation does not affect the collection frequency and pause times for another generation.



Measurement
在命令中加上 -verbose:gc  将会打印gc信息

例如

[GC 325407K->83000K(776768K), 0.2300771 secs]
[GC 325816K->83372K(776768K), 0.2454258 secs]
[Full GC 267628K->83769K(776768K), 1.8479984 secs]

表示两个minor gc和一个full gc
325407K->83000K 中的 83000K 表示minor收集后的对象，要么是tenured 代对象，要么被tenured 代对象引用
(776768K)表示目前的空堆大小，注意这个只包含一个survivor 区

使用  -XX:+PrintGCDetails 信息更详细，例如一个串行收集器

[GC [DefNew: 64575K->959K(64576K), 0.0457646 secs] 196016K->133633K(261184K), 0.0459067 secs]

DefNew部分 表示minor collection用0.0458s 收集了98%的年轻代 
整个堆减少了50%  (196016K->133633K(261184K))，并加了些时间变为0.0459s


使用 -XX:+PrintGCTimeStamps 参数将添加gc的开始时间：

111.042: [GC 111.042: [DefNew: 8128K->8128K(8128K), 0.0000505 secs]111.042: [Tenured: 18154K->2311K(24576K), 0.1290354 secs] 26282K->2311K(32704K), 0.1293306 secs]

启动111秒后开始gc，用时129ms





4 Sizing the Generations

-Xmx 表示vm 保留的空间
-Xms 表示vm启动时保留的空间，则其余部分标记为 "virtual" 
不同代的堆都可以涨到需要的 "virtual" 空间极限

Figure 4-1 Heap Parameters

注：以下参数除了“总大小”外不适用于并行收集


Total Heap

对gc影响最大的是总的可用内存，因为gc在代满时启动，throughput与可用内存成反比

HeapFreeRatio表示 堆free空间 和 堆大小的比值
可以用 -XX:MinHeapFreeRatio=<minimum> and -XX:MaxHeapFreeRatio=<maximum>, 来设定这个比值范围

大小由 -Xms<min> -Xmx<max>确定

Table 4-1 Default Parameters for 64-Bit Solaris Operating System
MinHeapFreeRatio 40  如果一个代中free空间小于40%，则 代 会扩张
MaxHeapFreeRatio 70  如果一个代中free空间大于70%，则 代 会收缩
-Xms 6656k
-Xmx calculated

一般地，
* 如果没有暂停问题，极可能大的分配总大小
* 将-Xms -Xmx设置为相同值，可以省却vm最大的决定，不过vm如果设置的很差，vm也无法优化
* 如果cpu增加，就增加堆，因为分配可以并行
* maximum heap size要比物理内存小

The Young Generation

young越大，minor越少；但是如果堆大小固定，会减少major次数

 -XX:NewRatio=3 表示 young : tenured = 1 : 3

 即 eden + survivor 为堆总大小的四分之一

如果需要更细粒度调节，可以用NewSize and MaxNewSize 


Survivor Space Sizing

这个一般不会影响性能
-XX:SurvivorRatio=6 表示 eden : a survivor = 6 : 1

Survivor过小，超过的复制将直接到tenured 代，如果Survivor过大，则浪费空间
如果需要可用下面的参数查看对象生命周期分布
-XX:+PrintTenuringDistribution

Table 4-2 Default Parameter Values for Survivor Space Sizing
NewRatio 2
NewSize 1310M
MaxNewSize not limited
SurvivorRatio 8

* 保证tenured代能够足够持有所有的长生命周期的对象 和一些活动空间 20%左右
* 在上面的基础上：为young分配足够的空间；cpu增加时，增加young




5 Available Collectors

jvm有三种收集器：

* serial collector 单线程，适用于单处理器机器，或小于100MB的内存分配。可以通过-XX:+UseSerialGC 强制使用
* parallel collector  (also known as the throughput collector) 在minor时，并行处理。适用于多核大数据，可以通过-XX:+UseParallelGC 强制使用
  * Parallel compaction并行压缩，不使用它，major将使用单个线程，影响scalability。默认启动，可以通过-XX:-UseParallelOldGC关闭
* 响应时间很重要时，需要将大部分GC工作并行化，使pause尽量短，可以使用
  * CMS The Mostly Concurrent Collectors. Use the option -XX:+UseConcMarkSweepGC
  * G1 XX:+UseG1GC 





6 The Parallel Collector
默认收集器
这种收集器，吞吐量最大，一般垃圾收集线程为总线程的 5/8
可用参数 -XX:ParallelGCThreads=<N> 控制

* Maximum Garbage Collection Pause Time -XX:MaxGCPauseMillis=<N>
* Throughput -XX:GCTimeRatio=<N>  which sets the ratio of garbage collection time to application time to 1 / (1 + <N>)。 For example, -XX:GCTimeRatio=19 sets a goal of 1/20
* Footprint: Maximum heap footprint is specified using the option -Xmx<N>

gc会进行统计和调整代大小 来达到 前面的目标
调整大小是增量进行的，默认增加20% 收缩5%
对应参数 
-XX:YoungGenerationSizeIncrement=<Y> 
-XX:TenuredGenerationSizeIncrement=<T> 
-XX:AdaptiveSizeDecrementScaleFactor=<D>

使用-XX:+PrintFlagsFinal 可以查看最大堆大小

gc时间过长会抛出 OutOfMemoryError，这个需要关注





7 The Mostly Concurrent Collectors

* Concurrent Mark Sweep (CMS) Collector: This collector is for applications that prefer shorter garbage collection pauses and can afford to share processor resources with the garbage collection.
* Garbage-First Garbage Collector: This server-style collector is for multiprocessor machines with large memories. It meets garbage collection pause time goals with high probability while achieving high throughput.

一般并行收集使用 K/N of the available processors 来收集
 1<=K<=ceiling{N/4}

只有一两个处理器，也可以减小暂停，但是这个特性会被移除
https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/cms.html#CJAGIIEJ
However, there is a separate mode available for CMS (not G1) that can achieve low pauses on systems with only one or two processors; see Incremental Mode in Concurrent Mark Sweep (CMS) Collector for details. This feature is being deprecated in Java SE 8 and may be removed in a later major release.


Additional References

The Garbage-First Garbage Collector:
http://www.oracle.com/technetwork/java/javase/tech/g1-intro-jsp-135488.html

Garbage-First Garbage Collector Tuning:
http://www.oracle.com/technetwork/articles/java/g1gc-1984535.html




8 Concurrent Mark Sweep (CMS) Collector

-XX:+UseConcMarkSweepGC
在major的开始和中间阶段，有两次暂停，第二次时间长些
其余的tracing活对象，清除不可达对象 可以并行执行

Concurrent Mode Failure
一般，CMS减少暂停，但是当tenured generation满了却无法收集对象，或无法分配空间给新对象的时候，应用会暂停


Garbage Collection: Algorithms for Automated Dynamic Memory

Pauses
* initial mark pause 初始标记mark 暂停，标记Root可达的
* remark pause : The second pause comes at the end of the concurrent tracing phase and finds objects that were missed by the concurrent tracing due to updates by the application threads of references in an object after the CMS collector had finished tracing that object. 
重tracing被应用线程修改的对象

Concurrent Phases - concurrent tracing of the reachable object graph occurs between the initial mark pause and the remark pause
会消耗cpu资源


Starting a Concurrent Collection Cycle

启动并发收集需要在tenured代满之前启动，否则会出现concurrent mode failure
the start of a concurrent collection must be timed such that the collection can finish before the tenured generation becomes full; otherwise, the application would observe longer pauses due to concurrent mode failure. There are several ways to start a concurrent collection.

CMS是估计时间，或tenured代超过一个限额 -XX:CMSInitiatingOccupancyFraction=<N>，默认为92%


Incremental Mode （deprecated ）

* Stop all application threads, identify the set of objects reachable from roots, and then resume all application threads.
* Concurrently trace the reachable object graph, using one or more processors, while the application threads are executing.
* Concurrently retrace sections of the object graph that were modified since the tracing in the previous step, using one processor.
* Stop all application threads and retrace sections of the roots and object graph that may have been modified since they were last examined, and then resume all application threads.
* Concurrently sweep up the unreachable objects to the free lists used for allocation, using one processor.
* Concurrently resize the heap and prepare the support data structures for the next collection cycle, using one processor.


Command-Line Options
-XX:+UseConcMarkSweepGC
-XX:+CMSIncrementalMode
-XX:+CMSIncrementalPacing 自动调整
-XX:CMSIncrementalDutyCycle=<N> 默认10
-XX:CMSIncrementalDutyCycleMin=<N>
-XX:CMSIncrementalSafetyFactor=<N>
-XX:CMSIncrementalOffset=<N>
-XX:CMSExpAvgFactor=<N>

i-cms推荐设置

in java 8

-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+PrintGCDetails -XX:+PrintGCTimeStamps

in java5
-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode \
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps \
-XX:+CMSIncrementalPacing -XX:CMSIncrementalDutyCycleMin=0
-XX:CMSIncrementalDutyCycle=10

Table 8-2 Troubleshooting the i-cms Automatic Pacing Feature


Measurements

The initial mark pause is typically short relative to the minor collection pause time. The concurrent phases (concurrent mark, concurrent preclean and concurrent sweep) normally last significantly longer than a minor collection pause, as indicated by Example 8-1, "Output from the CMS Collector". Note, however, that the application is not paused during these concurrent phases. The remark pause is often comparable in length to a minor collection. The remark pause is affected by certain application characteristics (for example, a high rate of object modification can increase this pause) and the time since the last minor collection (for example, more objects in the young generation may increase this pause).

remaks时间会很长，如果应用线程修改了很多对象













参考链接 todo
https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/preface.html