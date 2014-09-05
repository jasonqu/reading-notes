http://www.ibm.com/developerworks/java/tutorials/j-nio/j-nio.html
http://www.ibm.com/developerworks/cn/education/java/j-nio/j-nio.html

2003 年 11 月 17 日 的文章，针对jdk1.4

### 输入/输出：概念性描述

#### I/O 简介

在 Java 编程中，直到最近一直使用 流 的方式完成 I/O。所有 I/O 都被视为单个的字节的移动，通过一个称为 Stream 的对象一次移动一个字节。流 I/O 用于与外部世界接触。它也在内部使用，用于将对象转换为字节，然后再转换回对象。

NIO 与原来的 I/O 有同样的作用和目的，但是它使用不同的方式? 块 I/O。正如您将在本教程中学到的，块 I/O 的效率可以比流 I/O 高许多。

#### 为什么要使用 NIO?

NIO 的创建目的是为了让 Java 程序员可以实现高速 I/O 而无需编写自定义的本机代码。NIO 将最耗时的 I/O 操作(即填充和提取缓冲区)转移回操作系统，因而可以极大地提高速度。

#### 流与块的比较

原来的 I/O 库(在 java.io.*中) 与 NIO 最重要的区别是数据打包和传输的方式。正如前面提到的，原来的 I/O 以流的方式处理数据，而 NIO 以块的方式处理数据。

面向流 的 I/O 系统一次一个字节地处理数据。一个输入流产生一个字节的数据，一个输出流消费一个字节的数据。为流式数据创建过滤器非常容易。链接几个过滤器，以便每个过滤器只负责单个复杂处理机制的一部分，这样也是相对简单的。不利的一面是，面向流的 I/O 通常相当慢。

一个 面向块 的 I/O 系统以块的形式处理数据。每一个操作都在一步中产生或者消费一个数据块。按块处理数据比按(流式的)字节处理数据要快得多。但是面向块的 I/O 缺少一些面向流的 I/O 所具有的优雅性和简单性。

### 通道和缓冲区

#### 概述

通道 和 缓冲区 是 NIO 中的核心对象，几乎在每一个 I/O 操作中都要使用它们。

通道是对原 I/O 包中的流的模拟。到任何目的地(或来自任何地方)的所有数据都必须通过一个 Channel 对象。一个 Buffer 实质上是一个容器对象。发送给一个通道的所有对象都必须首先放到缓冲区中；同样地，从通道中读取的任何数据都要读到缓冲区中。

####什么是通道？

Channel是一个对象，可以通过它读取和写入数据。拿 NIO 与原来的 I/O 做个比较，通道就像是流。

正如前面提到的，所有数据都通过 Buffer 对象来处理。您永远不会将字节直接写入通道中，相反，您是将数据写入包含一个或者多个字节的缓冲区。同样，您不会直接从通道中读取字节，而是将数据从通道读入缓冲区，再从缓冲区获取这个字节。

####通道类型

通道与流的不同之处在于通道是双向的。而流只是在一个方向上移动(一个流必须是 InputStream 或者 OutputStream 的子类)， 而 通道 可以用于读、写或者同时用于读写。

因为它们是双向的，所以通道可以比流更好地反映底层操作系统的真实情况。特别是在 UNIX 模型中，底层操作系统通道是双向的。

###从理论到实践：NIO 中的读和写
####概述
读和写是 I/O 的基本过程。从一个通道中读取很简单：只需创建一个缓冲区，然后让通道将数据读到这个缓冲区中。写入也相当简单：创建一个缓冲区，用数据填充它，然后让通道用这些数据来执行写入操作。

####从文件中读取
因此读取文件涉及三个步骤：(1) 从 FileInputStream 获取 Channel，(2) 创建 Buffer，(3) 将数据从 Channel 读到 Buffer 中。

第一步是获取通道。我们从 FileInputStream 获取通道：

	FileInputStream fin = new FileInputStream( "readandshow.txt" );
	FileChannel fc = fin.getChannel();

下一步是创建缓冲区：

	ByteBuffer buffer = ByteBuffer.allocate( 1024 );

最后，需要将数据从通道读到缓冲区中，如下所示：

	fc.read( buffer );

####写入文件
在 NIO 中写入文件类似于从文件中读取。首先从 FileOutputStream 获取一个通道：

	FileOutputStream fout = new FileOutputStream( "writesomebytes.txt" );
	FileChannel fc = fout.getChannel();

下一步是创建一个缓冲区并在其中放入一些数据 - 在这里，数据将从一个名为 message 的数组中取出，这个数组包含字符串 "Some bytes" 的 ASCII 字节(本教程后面将会解释 buffer.flip() 和 buffer.put() 调用)。

	ByteBuffer buffer = ByteBuffer.allocate( 1024 );
	
	for (int i=0; i<message.length; ++i) {
	     buffer.put( message[i] );
	}
	buffer.flip();

最后一步是写入缓冲区中：

	fc.write( buffer );

####读写结合

copyfile

    FileInputStream fin = new FileInputStream( infile );
    FileOutputStream fout = new FileOutputStream( outfile );

    FileChannel fcin = fin.getChannel();
    FileChannel fcout = fout.getChannel();

    ByteBuffer buffer = ByteBuffer.allocate( 1024 );

    while (true) {
      buffer.clear();
      int r = fcin.read( buffer );
      if (r==-1) {
        break;
      }
      buffer.flip();
      fcout.write( buffer );
    }

###缓冲区内部细节

####状态变量
可以用三个值指定缓冲区在任意时刻的状态：

- position
- limit
- capacity

这三个变量一起可以跟踪缓冲区的状态和它所包含的数据。

一个例子，略

####访问方法

ByteBuffer 类中有四个 get() 方法：

1. byte get();
1. ByteBuffer get( byte dst[] );
1. ByteBuffer get( byte dst[], int offset, int length );
1. byte get( int index );

我们认为前三个 get() 方法是相对的，而最后一个方法是绝对的。 相对 意味着 get() 操作服从 limit 和 position 值 ― 更明确地说，字节是从当前 position 读取的，而 position 在 get 之后会增加。另一方面，一个 绝对 方法会忽略 limit 和 position 值，也不会影响它们。事实上，它完全绕过了缓冲区的统计方法。

put()方法
ByteBuffer 类中有五个 put() 方法：

1. ByteBuffer put( byte b );
1. ByteBuffer put( byte src[] );
1. ByteBuffer put( byte src[], int offset, int length );
1. ByteBuffer put( ByteBuffer src );
1. ByteBuffer put( int index, byte b );

与 get() 方法一样，我们将把 put() 方法划分为 相对 或者 绝对 的。前四个方法是相对的，而第五个方法是绝对的。


####缓冲区的使用：一个内部循环
下面的内部循环概括了使用缓冲区将数据从输入通道拷贝到输出通道的过程。

	while (true) {
	     buffer.clear();
	     int r = fcin.read( buffer );
	
	     if (r==-1) {
	       break;
	     }
	
	     buffer.flip();
	     fcout.write( buffer );
	}

read() 和 write() 调用得到了极大的简化，因为许多工作细节都由缓冲区完成了。 clear() 和 flip() 方法用于让缓冲区在读和写之间切换。

###关于缓冲区的更多内容

本节将讨论使用缓冲区的一些更复杂的方面，比如缓冲区分配、包装和分片。我们还会讨论 NIO 带给 Java 平台的一些新功能。您将学到如何创建不同类型的缓冲区以达到不同的目的，如可保护数据不被修改的 只读 缓冲区，和直接映射到底层操作系统缓冲区的 直接 缓冲区。我们将在本节的最后介绍如何在 NIO 中创建内存映射文件。

####缓冲区分配和包装

要创建缓冲区，您必须 分配 它。我们使用静态方法 allocate() 来分配缓冲区：

	ByteBuffer buffer = ByteBuffer.allocate( 1024 );

还可以将一个现有的数组转换为缓冲区，如下所示：

	byte array[] = new byte[1024];
	ByteBuffer buffer = ByteBuffer.wrap( array );

####缓冲区分片
slice() 方法根据现有的缓冲区创建一种 子缓冲区 。也就是说，它创建一个新的缓冲区，新缓冲区与原来的缓冲区的一部分共享数据。

在某种意义上，子缓冲区就像原来的缓冲区中的一个 窗口 。
窗口的起始和结束位置通过设置 position 和 limit 值来指定，然后调用 Buffer 的 slice() 方法：

	buffer.position( 3 );
	buffer.limit( 7 );
	ByteBuffer slice = buffer.slice();

片段 和 缓冲区 共享同一个底层数据数组

    ByteBuffer buffer = ByteBuffer.allocate( 10 );

    for (int i=0; i<buffer.capacity(); ++i) {
      buffer.put( (byte)i );
    }

    buffer.position( 3 );
    buffer.limit( 7 );

    ByteBuffer slice = buffer.slice();

    for (int i=0; i<slice.capacity(); ++i) {
      byte b = slice.get( i );
      b *= 11;
      slice.put( i, b );
    }

    buffer.position( 0 );
    buffer.limit( buffer.capacity() );

    while (buffer.remaining()>0) {
      System.out.println( buffer.get() );
    }


####只读缓冲区
只读缓冲区非常简单 ― 您可以读取它们，但是不能向它们写入。可以通过调用缓冲区的 asReadOnlyBuffer() 方法，将任何常规缓冲区转换为只读缓冲区，这个方法返回一个与原缓冲区完全相同的缓冲区(并与其共享数据)，只不过它是只读的。

####直接和间接缓冲区
另一种有用的 ByteBuffer 是直接缓冲区。 直接缓冲区 是为加快 I/O 速度，而以一种特殊的方式分配其内存的缓冲区。

给定一个直接字节缓冲区，Java 虚拟机将尽最大努力直接对它执行本机 I/O 操作。也就是说，它会在每一次调用底层操作系统的本机 I/O 操作之前(或之后)，尝试避免将缓冲区的内容拷贝到一个中间缓冲区中(或者从一个中间缓冲区中拷贝数据)。

    FileInputStream fin = new FileInputStream( infile );
    FileOutputStream fout = new FileOutputStream( outfile );

    FileChannel fcin = fin.getChannel();
    FileChannel fcout = fout.getChannel();

    ByteBuffer buffer = ByteBuffer.allocateDirect( 1024 );

    while (true) {
      buffer.clear();
      int r = fcin.read( buffer );
      if (r==-1) {
        break;
      }
      buffer.flip();
      fcout.write( buffer );
    }

####内存映射文件 I/O
内存映射文件 I/O 是一种读和写文件数据的方法，它可以比常规的基于流或者基于通道的 I/O 快得多。

下面代码行将文件的前 1024 个字节映射到内存中：

	MappedByteBuffer mbb = fc.map( FileChannel.MapMode.READ_WRITE, 0, 1024 );

map() 方法返回一个 MappedByteBuffer，它是 ByteBuffer 的子类。因此，您可以像使用其他任何 ByteBuffer 一样使用新映射的缓冲区，操作系统会在需要时负责执行行映射。

###分散和聚集

####概述
分散/聚集 I/O 是使用多个而不是单个缓冲区来保存数据的读写方法。

一个分散的读取就像一个常规通道读取，只不过它是将数据读到一个缓冲区数组中而不是读到单个缓冲区中。同样地，一个聚集写入是向缓冲区数组而不是向单个缓冲区写入数据。

分散/聚集 I/O 对于将数据流划分为单独的部分很有用，这有助于实现复杂的数据格式。

通道可以有选择地实现两个新的接口： ScatteringByteChannel 和 GatheringByteChannel

####分散/聚集 I/O

一个 ScatteringByteChannel 是一个具有两个附加读方法的通道：

	long read( ByteBuffer[] dsts );
	long read( ByteBuffer[] dsts, int offset, int length );

这些 long read() 方法很像标准的 read 方法，只不过它们不是取单个缓冲区而是取一个缓冲区数组。
在 分散读取 中，通道依次填充每个缓冲区。填满一个缓冲区后，它就开始填充下一个。在某种意义上，缓冲区数组就像一个大缓冲区。

####聚集写入

聚集写入 类似于分散读取，只不过是用来写入。它也有接受缓冲区数组的方法：

	long write( ByteBuffer[] srcs );
	long write( ByteBuffer[] srcs, int offset, int length );

聚集写对于把一组单独的缓冲区中组成单个数据流很有用。为了与上面的消息例子保持一致，您可以使用聚集写入来自动将网络消息的各个部分组装为单个数据流，以便跨越网络传输消息。

####分散/聚集的应用

分散/聚集 I/O 对于将数据划分为几个部分很有用。例如，您可能在编写一个使用消息对象的网络应用程序，每一个消息被划分为固定长度的头部和固定长度的正文。您可以创建一个刚好可以容纳头部的缓冲区和另一个刚好可以容难正文的缓冲区。当您将它们放入一个数组中并使用分散读取来向它们读入消息时，头部和正文将
整齐地划分到这两个缓冲区中。

我们从缓冲区所得到的方便性对于缓冲区数组同样有效。因为每一个缓冲区都跟踪自己还可以接受多少数据，所以分散读取会自动找到有空间接受数据的第一个缓冲区。在这个缓冲区填满后，它就会移动到下一个缓冲区。

	public class UseScatterGather
	{
	  static private final int firstHeaderLength = 2;
	  static private final int secondHeaderLength = 4;
	  static private final int bodyLength = 6;
	
	  static public void main( String args[] ) throws Exception {
	    if (args.length!=1) {
	      System.err.println( "Usage: java UseScatterGather port" );
	      System.exit( 1 );
	    }
	
	    int port = Integer.parseInt( args[0] );
	
	    ServerSocketChannel ssc = ServerSocketChannel.open();
	    InetSocketAddress address = new InetSocketAddress( port );
	    ssc.socket().bind( address );
	
	    int messageLength =
	      firstHeaderLength + secondHeaderLength + bodyLength;
	
	    ByteBuffer buffers[] = new ByteBuffer[3];
	    buffers[0] = ByteBuffer.allocate( firstHeaderLength );
	    buffers[1] = ByteBuffer.allocate( secondHeaderLength );
	    buffers[2] = ByteBuffer.allocate( bodyLength );
	
	    SocketChannel sc = ssc.accept();
	
	    while (true) {
	
	      // Scatter-read into buffers
	      int bytesRead = 0;
	      while (bytesRead < messageLength) {
	        long r = sc.read( buffers );
	        bytesRead += r;
	
	        System.out.println( "r "+r );
	        for (int i=0; i<buffers.length; ++i) {
	          ByteBuffer bb = buffers[i];
	          System.out.println( "b "+i+" "+bb.position()+" "+bb.limit() );
	        }
	      }
	
	      // Process message here
	
	      // Flip buffers
	      for (int i=0; i<buffers.length; ++i) {
	        ByteBuffer bb = buffers[i];
	        bb.flip();
	      }
	
	      // Scatter-write back out
	      long bytesWritten = 0;
	      while (bytesWritten<messageLength) {
	        long r = sc.write( buffers );
	        bytesWritten += r;
	      }
	
	      // Clear buffers
	      for (int i=0; i<buffers.length; ++i) {
	        ByteBuffer bb = buffers[i];
	        bb.clear();
	      }
	
	      System.out.println( bytesRead+" "+bytesWritten+" "+messageLength );
	    }
	  }
	}

###文件锁定

####概述

文件锁定初看起来可能让人迷惑。它 似乎 指的是防止程序或者用户访问特定文件。事实上，文件锁就像常规的 Java 对象锁 ― 它们是 劝告式的（advisory） 锁。它们不阻止任何形式的数据访问，相反，它们通过锁的共享和获取赖允许系统的不同部分相互协调。

#### 文件锁定和可移植性

文件锁定可能是一个复杂的操作，特别是考虑到不同的操作系统是以不同的方式实现锁这一事实。下面的指导原则将帮助您尽可能保持代码的可移植性：

- 只使用排它锁。
- 将所有的锁视为劝告式的（advisory）。

### 连网和异步 I/O

####异步 I/O
异步 I/O 是一种 没有阻塞地 读写数据的方法。通常，在代码进行 read() 调用时，代码会阻塞直至有可供读取的数据。同样， write() 调用将会阻塞直至数据能够写入。

另一方面，异步 I/O 调用不会阻塞。相反，您将注册对特定 I/O 事件的兴趣 ― 可读的数据的到达、新的套接字连接，等等，而在发生这样的事件时，系统将会告诉您。

异步 I/O 的一个优势在于，它允许您同时根据大量的输入和输出执行 I/O。同步程序常常要求助于轮询，或者创建许许多多的线程以处理大量的连接。使用异步 I/O，您可以监听任何数量的通道上的事件，不用轮询，也不用额外的线程。

我们将通过研究一个名为 MultiPortEcho.java 的例子程序来查看异步 I/O 的实际应用。这个程序就像传统的 echo server，它接受网络连接并向它们回响它们可能发送的数据。不过它有一个附加的特性，就是它能同时监听多个端口，并处理来自所有这些端口的连接。并且它只在单个线程中完成所有这些工作。


	public class MultiPortEcho
	{
	  private int ports[];
	  private ByteBuffer echoBuffer = ByteBuffer.allocate( 1024 );
	
	  public MultiPortEcho( int ports[] ) throws IOException {
	    this.ports = ports;
	
	    go();
	  }
	
	  private void go() throws IOException {
	    // Create a new selector
	    Selector selector = Selector.open();
	
	    // Open a listener on each port, and register each one
	    // with the selector
	    for (int i=0; i<ports.length; ++i) {
	      ServerSocketChannel ssc = ServerSocketChannel.open();
	      ssc.configureBlocking( false );
	      ServerSocket ss = ssc.socket();
	      InetSocketAddress address = new InetSocketAddress( ports[i] );
	      ss.bind( address );
	
	      SelectionKey key = ssc.register( selector, SelectionKey.OP_ACCEPT );
	
	      System.out.println( "Going to listen on "+ports[i] );
	    }
	
	    while (true) {
	      int num = selector.select();
	
	      Set selectedKeys = selector.selectedKeys();
	      Iterator it = selectedKeys.iterator();
	
	      while (it.hasNext()) {
	        SelectionKey key = (SelectionKey)it.next();
	
	        if ((key.readyOps() & SelectionKey.OP_ACCEPT)
	          == SelectionKey.OP_ACCEPT) {
	          // Accept the new connection
	          ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
	          SocketChannel sc = ssc.accept();
	          sc.configureBlocking( false );
	
	          // Add the new connection to the selector
	          SelectionKey newKey = sc.register( selector, SelectionKey.OP_READ );
	          it.remove();
	
	          System.out.println( "Got connection from "+sc );
	        } else if ((key.readyOps() & SelectionKey.OP_READ)
	          == SelectionKey.OP_READ) {
	          // Read the data
	          SocketChannel sc = (SocketChannel)key.channel();
	
	          // Echo data
	          int bytesEchoed = 0;
	          while (true) {
	            echoBuffer.clear();
	
	            int r = sc.read( echoBuffer );
	
	            if (r<=0) {
	              break;
	            }
	
	            echoBuffer.flip();
	
	            sc.write( echoBuffer );
	            bytesEchoed += r;
	          }
	
	          System.out.println( "Echoed "+bytesEchoed+" from "+sc );
	
	          it.remove();
	        }
	
	      }
	
	//System.out.println( "going to clear" );
	//      selectedKeys.clear();
	//System.out.println( "cleared" );
	    }
	  }
	}

####Selectors

异步 I/O 中的核心对象名为 Selector。Selector 就是您注册对各种 I/O 事件的兴趣的地方，而且当那些事件发生时，就是这个对象告诉您所发生的事件。

所以，我们需要做的第一件事就是创建一个 Selector：

	Selector selector = Selector.open();

然后，我们将对不同的通道对象调用 register() 方法，以便注册我们对这些对象中发生的 I/O 事件的兴趣。register() 的第一个参数总是这个 Selector。

为了接收连接，我们需要一个 ServerSocketChannel。事实上，我们要监听的每一个端口都需要有一个 ServerSocketChannel 。

####选择键

下一步是将新打开的 ServerSocketChannels 注册到 Selector上。为此我们使用 ServerSocketChannel.register() 方法，如下所示：

	SelectionKey key = ssc.register( selector, SelectionKey.OP_ACCEPT );

register() 的第一个参数总是这个 Selector。第二个参数是 OP_ACCEPT，这里它指定我们想要监听 accept 事件，也就是在新的连接建立时所发生的事件。这是适用于 ServerSocketChannel 的唯一事件类型。

请注意对 register() 的调用的返回值。 SelectionKey 代表这个通道在此 Selector 上的这个注册。当某个 Selector 通知您某个传入事件时，它是通过提供对应于该事件的 SelectionKey 来进行的。SelectionKey 还可以用于取消通道的注册。

####内部循环

现在已经注册了我们对一些 I/O 事件的兴趣，下面将进入主循环。使用 Selectors 的几乎每个程序都像下面这样使用内部循环：

	int num = selector.select();
	
	Set selectedKeys = selector.selectedKeys();
	Iterator it = selectedKeys.iterator();
	
	while (it.hasNext()) {
	     SelectionKey key = (SelectionKey)it.next();
	     // ... deal with I/O event ...
	}

首先，我们调用 Selector 的 select() 方法。这个方法会阻塞，直到至少有一个已注册的事件发生。当一个或者更多的事件发生时， select() 方法将返回所发生的事件的数量。

接下来，我们调用 Selector 的 selectedKeys() 方法，它返回发生了事件的 SelectionKey 对象的一个 集合 。

我们通过迭代 SelectionKeys 并依次处理每个 SelectionKey 来处理事件。对于每一个 SelectionKey，您必须确定发生的是什么 I/O 事件，以及这个事件影响哪些 I/O 对象。

####监听新连接
程序执行到这里，我们仅注册了 ServerSocketChannel，并且仅注册它们“接收”事件。为确认这一点，我们对 SelectionKey 调用 readyOps() 方法，并检查发生了什么类型的事件：

	if ((key.readyOps() & SelectionKey.OP_ACCEPT)
	     == SelectionKey.OP_ACCEPT) {
	
	     // Accept the new connection
	     // ...
	}

可以肯定地说， readOps() 方法告诉我们该事件是新的连接。

####接受新的连接
因为我们知道这个服务器套接字上有一个传入连接在等待，所以可以安全地接受它；也就是说，不用担心 accept() 操作会阻塞：

	ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
	SocketChannel sc = ssc.accept();

下一步是将新连接的 SocketChannel 配置为非阻塞的。而且由于接受这个连接的目的是为了读取来自套接字的数据，所以我们还必须将 SocketChannel 注册到 Selector上，如下所示：

	sc.configureBlocking( false );
	SelectionKey newKey = sc.register( selector, SelectionKey.OP_READ );

注意我们使用 register() 的 OP_READ 参数，将 SocketChannel 注册用于 读取 而不是 接受 新连接。

####删除处理过的 SelectionKey

在处理 SelectionKey 之后，我们几乎可以返回主循环了。但是我们必须首先将处理过的 SelectionKey 从选定的键集合中删除。如果我们没有删除处理过的键，那么它仍然会在主集合中以一个激活的键出现，这会导致我们尝试再次处理它。我们调用迭代器的 remove() 方法来删除处理过的 SelectionKey：

	it.remove();

现在我们可以返回主循环并接受从一个套接字中传入的数据(或者一个传入的 I/O 事件)了。

####传入的 I/O
当来自一个套接字的数据到达时，它会触发一个 I/O 事件。这会导致在主循环中调用 Selector.select()，并返回一个或者多个 I/O 事件。这一次， SelectionKey 将被标记为 OP_READ 事件，如下所示：

	} else if ((key.readyOps() & SelectionKey.OP_READ)
	     == SelectionKey.OP_READ) {
	     // Read the data
	     SocketChannel sc = (SocketChannel)key.channel();
	     // ...
	}

与以前一样，我们取得发生 I/O 事件的通道并处理它。


####回到主循环
每次返回主循环，我们都要调用 select 的 Selector()方法，并取得一组 SelectionKey。每个键代表一个 I/O 事件。我们处理事件，从选定的键集中删除 SelectionKey，然后返回主循环的顶部。

这个程序有点过于简单，因为它的目的只是展示异步 I/O 所涉及的技术。在现实的应用程序中，您需要通过将通道从 Selector 中删除来处理关闭的通道。而且您可能要使用多个线程。这个程序可以仅使用一个线程，因为它只是一个演示，但是在现实场景中，创建一个线程池来负责 I/O 事件处理中的耗时部分会更有意义。

###字符集 略



深入分析 Java I/O 的工作机制
http://www.ibm.com/developerworks/cn/java/j-lo-javaio/

许 令波, Java 工程师, 淘宝网
2011 年 10 月 31 日


还有一点需要说明，通过 Channel 获取的 I/O 数据首先要经过操作系统的 Socket 缓冲区再将数据复制到 Buffer 中，这个的操作系统缓冲区就是底层的 TCP 协议关联的 RecvQ 或者 SendQ 队列，从操作系统缓冲区到用户缓冲区复制数据比较耗性能，Buffer 提供了另外一种直接操作操作系统缓冲区的的方式即 ByteBuffer.allocateDirector(size)，这个方法返回的 byteBuffer 就是与底层存储空间关联的缓冲区，它的操作方式与 linux2.4 内核的 sendfile 操作方式类似。

###I/O 调优 总结

####磁盘 I/O 

我们的应用程序通常都需要访问磁盘读取数据，而磁盘 I/O 通常都很耗时，我们要判断 I/O 是否是一个瓶颈，我们有一些参数指标可以参考：

如我们可以压力测试应用程序看系统的 I/O wait 指标是否正常，例如测试机器有 4 个 CPU，那么理想的 I/O wait 参数不应该超过 25%，如果超过 25% 的话，I/O 很可能成为应用程序的性能瓶颈。Linux 操作系统下可以通过 iostat 命令查看。

提升磁盘 I/O 性能通常的方法有：

1. 增加缓存，减少磁盘访问次数
1. 优化磁盘的管理系统，设计最优的磁盘访问策略，以及磁盘的寻址策略，这里是在底层操作系统层面考虑的。
1. 设计合理的磁盘存储数据块，以及访问这些数据块的策略，这里是在应用层面考虑的。如我们可以给存放的数据设计索引，通过寻址索引来加快和减少磁盘的访问，还有可以采用异步和非阻塞的方式加快磁盘的访问效率。
1. 应用合理的 RAID 策略提升磁盘 IO

####网络 I/O 优化

网络 I/O 优化通常有一些基本处理原则：

1. 一个是减少网络交互的次数：要减少网络交互的次数通常我们在需要网络交互的两端会设置缓存，比如 Oracle 的 JDBC 驱动程序，就提供了对查询的 SQL 结果的缓存，在客户端和数据库端都有，可以有效的减少对数据库的访问。关于 Oracle JDBC 的内存管理可以参考《 Oracle JDBC 内存管理》。除了设置缓存还有一个办法是，合并访问请求：如在查询数据库时，我们要查 10 个 id，我可以每次查一个 id，也可以一次查 10 个 id。再比如在访问一个页面时通过会有多个 js 或 css 的文件，我们可以将多个 js 文件合并在一个 HTTP 链接中，每个文件用逗号隔开，然后发送到后端 Web 服务器根据这个 URL 链接，再拆分出各个文件，然后打包再一并发回给前端浏览器。这些都是常用的减少网络 I/O 的办法。
1. 减少网络传输数据量的大小：减少网络数据量的办法通常是将数据压缩后再传输，如 HTTP 请求中，通常 Web 服务器将请求的 Web 页面 gzip 压缩后在传输给浏览器。还有就是通过设计简单的协议，尽量通过读取协议头来获取有用的价值信息。比如在代理程序设计时，有 4 层代理和 7 层代理都是来尽量避免要读取整个通信数据来取得需要的信息。
1. 尽量减少编码：通常在网络 I/O 中数据传输都是以字节形式的，也就是通常要序列化。但是我们发送要传输的数据都是字符形式的，从字符到字节必须编码。但是这个编码过程是比较耗时的，所以在要经过网络 I/O 传输时，尽量直接以字节形式发送。也就是尽量提前将字符转化为字节，或者减少字符到字节的转化过程。
1. 根据应用场景设计合适的交互方式：所谓的交互场景主要包括同步与异步阻塞与非阻塞方式，下面将详细介绍。

####同步与异步
所谓同步就是一个任务的完成需要依赖另外一个任务时，只有等待被依赖的任务完成后，依赖的任务才能算完成，这是一种可靠的任务序列。要么成功都成功，失败都失败，两个任务的状态可以保持一致。而异步是不需要等待被依赖的任务完成，只是通知被依赖的任务要完成什么工作，依赖的任务也立即执行，只要自己完成了整个任务就算完成了。至于被依赖的任务最终是否真正完成，依赖它的任务无法确定，所以它是不可靠的任务序列。我们可以用打电话和发短信来很好的比喻同步与异步操作。
在设计到 IO 处理时通常都会遇到一个是同步还是异步的处理方式的选择问题。因为同步与异步的 I/O 处理方式对调用者的影响很大，在数据库产品中都会遇到这个问题。因为 I/O 操作通常是一个非常耗时的操作，在一个任务序列中 I/O 通常都是性能瓶颈。但是同步与异步的处理方式对程序的可靠性影响非常大，同步能够保证程序的可靠性，而异步可以提升程序的性能，必须在可靠性和性能之间做个平衡，没有完美的解决办法。
####阻塞与非阻塞
阻塞与非阻塞主要是从 CPU 的消耗上来说的，阻塞就是 CPU 停下来等待一个慢的操作完成 CPU 才接着完成其它的事。非阻塞就是在这个慢的操作在执行时 CPU 去干其它别的事，等这个慢的操作完成时，CPU 再接着完成后续的操作。虽然表面上看非阻塞的方式可以明显的提高 CPU 的利用率，但是也带了另外一种后果就是系统的线程切换增加。增加的 CPU 使用时间能不能补偿系统的切换成本需要好好评估。

####两种的方式的组合
组合的方式可以由四种，分别是：同步阻塞、同步非阻塞、异步阻塞、异步非阻塞，这四种方式都对 I/O 性能有影响。下面给出分析，并有一些常用的设计用例参考。

<table border="1">
<tr>
<td>组合方式</td>
<td>性能分析</td>
</tr>
<tr>
<td>同步阻塞</td>
<td>最常用的一种用法，使用也是最简单的，但是 I/O 性能一般很差，CPU 大部分在空闲状态。</td>
</tr>
<tr>
<td>同步非阻塞</td>
<td>提升 I/O 性能的常用手段，就是将 I/O 的阻塞改成非阻塞方式，尤其在网络 I/O 是长连接，同时传输数据也不是很多的情况下，提升性能非常有效。
这种方式通常能提升 I/O 性能，但是会增加 CPU 消耗，要考虑增加的 I/O 性能能不能补偿 CPU 的消耗，也就是系统的瓶颈是在 I/O 还是在 CPU 上。</td>
</tr>
<tr>
<td>异步阻塞</td>
<td>这种方式在分布式数据库中经常用到，例如在网一个分布式数据库中写一条记录，通常会有一份是同步阻塞的记录，而还有两至三份是备份记录会写到其它机器上，这些备份记录通常都是采用异步阻塞的方式写 I/O。
异步阻塞对网络 I/O 能够提升效率，尤其像上面这种同时写多份相同数据的情况。</td>
</tr>
<tr>
<td>异步非阻塞</td>
<td>这种组合方式用起来比较复杂，只有在一些非常复杂的分布式情况下使用，像集群之间的消息同步机制一般用这种 I/O 组合方式。如 Cassandra 的 Gossip 通信机制就是采用异步非阻塞的方式。
它适合同时要传多份相同的数据到集群中不同的机器，同时数据的传输量虽然不大，但是却非常频繁。这种网络 I/O 用这个方式性能能达到最高。</td>
</tr>
</table>

####Cassandra 例子 略

###参考链接

《深入分析Java中文编码问题》
http://www.ibm.com/developerworks/cn/java/j-lo-chinesecoding/

《Oracle JDBC内存管理》 链接失效

《Jetty 的工作原理和与 Tomcat 的比较》：
http://www.ibm.com/developerworks/cn/java/j-lo-jetty/

Servlet 工作原理解析
http://www.ibm.com/developerworks/cn/java/j-lo-servlet/






http://ifeve.com/overview/

Java NIO系列教程  作者：Jakob Jenkov

翻译时间 2013 年 6 月 7 日


####Selector
Selector允许单线程处理多个 Channel。如果你的应用打开了多个连接（通道），但每个连接的流量都很低，使用Selector就会很方便。例如，在一个聊天服务器中。

这是在一个单线程中使用一个Selector处理3个Channel的图示：

![](http://ifeve.com/wp-content/uploads/2013/06/overview-selectors.png)

要使用Selector，得向Selector注册Channel，然后调用它的select()方法。这个方法会一直阻塞到某个注册的通道有事件就绪。

一旦这个方法返回，线程就可以处理这些事件，事件的例子有如新连接进来，数据接收等。


Java NIO的通道类似流，但又有些不同：

- 既可以从通道中读取数据，又可以写数据到通道。但流的读写通常是单向的。
- 通道可以异步地读写。
- 通道中的数据总是要先读到一个Buffer，或者总是要从一个Buffer中写入。

####Buffer

- clear()方法会清空整个缓冲区
- compact()方法只会清除已经读过的数据
- 向Buffer中写数据 int bytesRead = inChannel.read(buf); //read into buffer.
- 从Buffer中读数据 int bytesWritten = inChannel.write(buf); //read from buffer into channel.
- flip()方法 将Buffer从写模式切换到读模式
- Buffer.rewind()将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变
- 通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。

####Scatter/Gather

Scattering Reads在移动下一个buffer前，必须填满当前的buffer，这也意味着它不适用于动态消息(译者注：消息大小不固定)。换句话说，如果存在消息头和消息体，消息头必须完成填充（例如 128byte），Scattering Reads才能正常工作。

buffers数组是write()方法的入参，write()方法会按照buffer在数组中的顺序，将数据写入到channel，注意只有position和limit之间的数据才会被写入。因此，如果一个buffer的容量为128byte，但是仅仅包含58byte的数据，那么这58byte的数据将被写入到channel中。因此与Scattering Reads相反，Gathering Writes能较好的处理动态消息。

####通道之间的数据传输

在Java NIO中，如果两个通道中有一个是FileChannel，那你可以直接将数据从一个channel传输到另外一个channel。 transferFrom() transferTo()

http://ifeve.com/selectors/

###Selector
####为什么使用Selector?

仅用单个线程来处理多个Channels的好处是，只需要更少的线程来处理通道。事实上，可以只用一个线程处理所有的通道。对于操作系统来说，线程之间上下文切换的开销很大，而且每个线程都要占用系统的一些资源（如内存）。因此，使用的线程越少越好。

与Selector一起使用时，Channel必须处于非阻塞模式下。这意味着不能将FileChannel与Selector一起使用，因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。

Selector可以监听四种不同类型的事件： Connect Accept Read Write

这四种事件用SelectionKey的四个常量来表示：

- SelectionKey.OP_CONNECT
- SelectionKey.OP_ACCEPT
- SelectionKey.OP_READ
- SelectionKey.OP_WRITE

如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来，如下：

	int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;

###SelectionKey

在上一小节中，当向Selector注册Channel时，register()方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性：

- interest集合
- ready集合
- Channel
- Selector
- 附加的对象（可选）

####interest集合

就像向Selector注册通道一节中所描述的，interest集合是你所选择的感兴趣的事件集合。可以通过SelectionKey读写interest集合，像这样：

	int interestSet = selectionKey.interestOps();
	
	boolean isInterestedInAccept  = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
	boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;
	boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
	boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;

可以看到，用“位与”操作interest 集合和给定的SelectionKey常量，可以确定某个确定的事件是否在interest 集合中。

####ready集合

ready 集合是通道已经准备就绪的操作的集合。在一次选择(Selection)之后，你会首先访问这个ready set。Selection将在下一小节进行解释。可以这样访问ready集合：

	int readySet = selectionKey.readyOps();

可以用像检测interest集合那样的方法，来检测channel中什么事件或操作已经就绪。但是，也可以使用以下四个方法，它们都会返回一个布尔类型：

	selectionKey.isAcceptable();
	selectionKey.isConnectable();
	selectionKey.isReadable();
	selectionKey.isWritable();

####Channel + Selector

从SelectionKey访问Channel和Selector很简单。如下：

	Channel  channel  = selectionKey.channel();
	Selector selector = selectionKey.selector();

####附加的对象

可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。例如，可以附加 与通道一起使用的Buffer，或是包含聚集数据的某个对象。使用方法如下：

	selectionKey.attach(theObject);
	Object attachedObj = selectionKey.attachment();

还可以在用register()方法向Selector注册Channel的时候附加对象。如：

	SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);

####通过Selector选择通道

一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道

- int select()
- int select(long timeout)
- int selectNow()

select()方法返回的int值表示有多少通道已经就绪。亦即，自上次调用select()方法后有多少通道变成就绪状态。如果调用select()方法，因为有一个通道变成就绪状态，返回了1，若再次调用select()方法，如果另一个通道就绪了，它会再次返回1。如果对第一个就绪的channel没有做任何操作，现在就有两个就绪的通道，但在每次select()方法调用之间，只有一个通道就绪了。

####selectedKeys()

一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用selector的selectedKeys()方法，访问“已选择键集（selected key set）”中的就绪通道。如下所示：

	Set selectedKeys = selector.selectedKeys();
	Iterator keyIterator = selectedKeys.iterator();
	while(keyIterator.hasNext()) {
	    SelectionKey key = keyIterator.next();
	    if(key.isAcceptable()) {
	        // a connection was accepted by a ServerSocketChannel.
	    } else if (key.isConnectable()) {
	        // a connection was established with a remote server.
	    } else if (key.isReadable()) {
	        // a channel is ready for reading
	    } else if (key.isWritable()) {
	        // a channel is ready for writing
	    }
	    keyIterator.remove();
	}

这个循环遍历已选择键集中的每个键，并检测各个键所对应的通道的就绪事件。

注意每次迭代末尾的keyIterator.remove()调用。Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。下次该通道变成就绪时，Selector会再次将其放入已选择键集中。

SelectionKey.channel()方法返回的通道需要转型成你要处理的类型，如ServerSocketChannel或SocketChannel等。

####wakeUp()

某个线程调用select()方法后阻塞了，即使没有通道已经就绪，也有办法让其从select()方法返回。只要让其它线程在第一个线程调用select()方法的那个对象上调用Selector.wakeup()方法即可。阻塞在select()方法上的线程会立马返回。

如果有其它线程调用了wakeup()方法，但当前没有线程阻塞在select()方法上，下个调用select()方法的线程会立即“醒来（wake up）”。

####close()

用完Selector后调用其close()方法会关闭该Selector，且使注册到该Selector上的所有SelectionKey实例无效。通道本身并不会关闭。

###FileChannel

####position方法

有时可能需要在FileChannel的某个特定位置进行数据的读/写操作。可以通过调用position()方法获取FileChannel的当前位置。

size,truncate,force

出于性能方面的考虑，操作系统会将数据缓存在内存中，所以无法保证写入到FileChannel里的数据一定会即时写到磁盘上。要保证这一点，需要调用force()方法。

###SocketChannel

SocketChannel是一个连接到TCP网络套接字的通道。可以通过以下2种方式创建SocketChannel：

1. 打开一个SocketChannel并连接到互联网上的某台服务器。
1. 一个新连接到达ServerSocketChannel时，会创建一个SocketChannel。

####打开 SocketChannel

下面是SocketChannel的打开方式：

	SocketChannel socketChannel = SocketChannel.open();
	socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));

####写入 SocketChannel

写数据到SocketChannel用的是SocketChannel.write()方法，该方法以一个Buffer作为参数。示例如下：

	String newData = "New String to write to file..." + System.currentTimeMillis();
	
	ByteBuffer buf = ByteBuffer.allocate(48);
	buf.clear();
	buf.put(newData.getBytes());
	
	buf.flip();
	
	while(buf.hasRemaining()) {
	    channel.write(buf);
	}

非阻塞模式下，write()方法在尚未写出任何内容时可能就返回了。所以需要在循环中调用write()。

注意SocketChannel.write()方法的调用是在一个while循环中的。Write()方法无法保证能写多少字节到SocketChannel。所以，我们重复调用write()直到Buffer没有要写的字节为止。

####connect()

如果SocketChannel在非阻塞模式下，此时调用connect()，该方法可能在连接建立之前就返回了。为了确定连接是否建立，可以调用finishConnect()的方法。像这样：

	socketChannel.configureBlocking(false);
	socketChannel.connect(new InetSocketAddress("http://jenkov.com", 80));
	
	while(! socketChannel.finishConnect() ){
	    //wait, or do something else...
	}

###ServerSocketChannel

通过 ServerSocketChannel.accept() 方法监听新进来的连接。当 accept()方法返回的时候,它返回一个包含新进来的连接的 SocketChannel。因此, accept()方法会一直阻塞到有新连接到达。

	while(true){
	    SocketChannel socketChannel =
	            serverSocketChannel.accept();
	
	    //do something with socketChannel...
	}

在非阻塞模式下，accept() 方法会立刻返回，如果还没有新进来的连接,返回的将是null。 因此，需要检查返回的SocketChannel是否是null.如：

	ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
	
	serverSocketChannel.socket().bind(new InetSocketAddress(9999));
	serverSocketChannel.configureBlocking(false);
	
	while(true){
	    SocketChannel socketChannel =
	            serverSocketChannel.accept();
	
	    if(socketChannel != null){
	        //do something with socketChannel...
	    }
	}

###DatagramChannel 略

###Pipe

Java NIO 管道是2个线程之间的单向数据连接。Pipe有一个source通道和一个sink通道。数据会被写到sink通道，从source通道读取。

这里是Pipe原理的图示：

![](http://ifeve.com/wp-content/uploads/2013/06/pipe.bmp)















