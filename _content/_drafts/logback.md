# slf4j与logback：珠联璧合的日志框架

### Why Logback

Spring框架的logging一节开头是这样写的：

> Unfortunately, the runtime discovery algorithm in commons-logging, while convenient for the end-user, is problematic. If we could turn back the clock and start Spring now as a new project it would use a different logging dependency. The first choice would probably be the Simple Logging Facade for Java ( SLF4J), which is also used by a lot of other tools that people use with Spring inside their applications.

具体的为什么使用slf4j的讨论可以参考这个链接——[What is the issue with the runtime discovery algorithm of Apache Commons Logging](http://stackoverflow.com/questions/3222895/what-is-the-issue-with-the-runtime-discovery-algorithm-of-apache-commons-logging)

我们可以这样看待：slf4j用来取代commons-logging，logback用来取代log4j。使用slf4j的理由已经很清楚了，为什么使用logback呢？

1. logback比log4j要快大约10倍，而且消耗更少的内存。
2. logback-classic模块直接实现了SLF4J的接口，所以我们迁移到logback几乎是零开销的。
3. logback不仅支持xml格式的配置文件，还支持groovy格式的配置文件。相比之下，Groovy风格的配置文件更加直观，简洁。
4. logback-classic能够检测到配置文件的更新，并且自动重新加载配置文件。
5. logback能够优雅的从I/O异常中恢复，从而我们不用重新启动应用程序来恢复logger。
6. logback能够根据配置文件中设置的上限值，自动删除旧的日志文件。
7. logback能够自动压缩日志文件。
8. logback能够在配置文件中加入条件判断（if-then-else)。可以避免不同的开发环境（dev、test、uat...）的配置文件的重复。
9. logback带来更多的filter。
10. logback的stack trace中会包含详细的包信息。
11. logback-access和Jetty、Tomcat集成提供了功能强大的HTTP-access日志。

所以作为开发者，不要再使用commons-logging + log4j的组合了，大胆地拥抱slf4j + logback吧！

理由还不够充分？没关系，后面会介绍其一个小技巧——[延迟字符串生成](#lazyeval)和一个其独门绝技——[MDC与多日志输出](#mdc)，应该更能说服你了。

参考资料

* http://logback.qos.ch/reasonsToSwitch.html
* http://www.cnblogs.com/penghongwei/p/3417179.html

### 安装

for gradle


    compile 'ch.qos.logback:logback-classic:1.1.2'
    compile 'org.slf4j:slf4j-api:1.7.7'


### 配置示例

配置文件：需要在类路径下提供配置文件，具体的查找顺序是这样的：

1. 尝试查找logback.groovy
1. 当没有找到时，尝试查找logback-test.xml
1. 当没有找到时，尝试查找logback.xml
1. 当没有找到时，默认配置（打印到控制台）

logback.xml配置文件示例：

	<configuration scan="true" scanPeriod="60 seconds" debug="false">
	    <!-- 用来输出 logback 调试信息的配置 -->
	    <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />
	
	    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
	        <!-- 日志输出路径，注意需要提前建好文件夹 -->
	        <file>logs/access.log</file>
	        
	        <!-- 按照时间格式滚动的策略，如果gz、zip结尾会自动压缩 -->
	        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
	            <fileNamePattern>logs/access-%d{yyyy-MM-dd_HH-mm}.log.gz</fileNamePattern>
	        </rollingPolicy>
	        
	        <!-- 只保存最近2个备份文件 -->
	        <maxHistory>2</maxHistory>
	        
	        <!-- 日志输出格式 -->
	        <encoder>
	            <pattern>%-4relative %d{yyyy-MM-dd HH-mm-ss} [%thread] %-5level %logger{35} - %msg%n</pattern>
	        </encoder>
	    </appender>
	
		<!-- 按名日志输出文件 -->
		<appender name="FILE2" class="ch.qos.logback.core.rolling.RollingFileAppender">
	        <file>logs/mylog.log</file>
	        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
	            <fileNamePattern>logs/mylog-%d{yyyy-MM-dd_HH-mm}.log.gz</fileNamePattern>
	        </rollingPolicy>
	        <encoder>
	            <pattern>%-4relative %d{yyyy-MM-dd HH-mm-ss} [%thread] %-5level %logger{35} - %msg%n</pattern>
	        </encoder>
	    </appender>
	
	    <!-- 标准输出 -->
	    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
	        <encoder>
	            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
	        </encoder>
	    </appender>
	
	    <!-- 自定义名的logger -->
		<logger name="mylog" level="INFO">
			<appender-ref ref="FILE2" />
		</logger>
	
	    <!-- 根下带有两个appender -->
	    <root level="DEBUG">
	        <appender-ref ref="STDOUT" />
	        <appender-ref ref="FILE" />
	    </root>
	
	</configuration>


<a name="lazyeval"></a>对应的Logger API可以这样使用，注意字符串不需要在参数接口处拼接，因此避免了类似``if(logger.isDebugEnabled()) {logger.debug(...)}``这样的if判断：

> HelloWorld.java


	package chapters.introduction;
	
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	
	/**
	 * Logger的接口示例：
	 * void debug(java.lang.String s, java.lang.Object o);
	   void debug(java.lang.String s, java.lang.Object o, java.lang.Object o1);
	   void debug(java.lang.String s, java.lang.Object... objects);
	 */
	public class HelloWorld {
	    private static Logger logger = LoggerFactory.getLogger(HelloWorld.class);
	    public static void main(String[] args) {
	        logger.debug("a very expensive operation : {}", new BigObject());
	        logger.info("test {}", 1);
	        logger.info("test {}, {}", 1, 2);
	        logger.info("test {}, {}, {}", 1, 2, "x");
	        logger.info("test {}, {}, {}", 1);
	        /**
	         * 一个输出示例
	353  2014-12-18 17-19-01 [main] ERROR chapters.introduction.BigObject - a very big object
	2364 2014-12-18 17-19-03 [main] ERROR chapters.introduction.BigObject - finish processing
	337  2014-12-18 17-19-01 [main] DEBUG chapters.introduction.HelloWorld - a very expensive operation : BigObject [x=0]
	2364 2014-12-18 17-19-03 [main] INFO  chapters.introduction.HelloWorld - test 1
	2364 2014-12-18 17-19-03 [main] INFO  chapters.introduction.HelloWorld - test 1, 2
	2364 2014-12-18 17-19-03 [main] INFO  chapters.introduction.HelloWorld - test 1, 2, x
	2364 2014-12-18 17-19-03 [main] INFO  chapters.introduction.HelloWorld - test 1, {}, {}
	         */
	    }
	}


> BigObject.java

	package chapters.introduction;
	
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	
	public class BigObject {
	    private static Logger logger = LoggerFactory.getLogger("mylog");
	    public int x = 0;
	
	    @Override
	    public String toString() {
	        logger.error("a very big object");
	        try {
	            Thread.sleep(500);
	        } catch (InterruptedException e) {
	        }
	        return "BigObject [x=" + x + "]";
	    }
	}


### <a name="mdc"></a>MDC与多日志输出

MDC是`Mapped Diagnostic Context`的简称，用来输出不同的上下文。例如一个多线程程序：

> HelloWorld.java

	package chapters.introduction;
	
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	
	import java.util.Random;
	import java.util.concurrent.ExecutorService;
	import java.util.concurrent.Executors;
	
	public class HelloWorld {
	    private static Logger logger = LoggerFactory.getLogger(HelloWorld.class);
	
	    public static void main(String[] args) {
	        logger.info("application begin");
	
	        ExecutorService executorService = Executors.newFixedThreadPool(6);
	        for (int i = 0; i < 10; i++) {
	            executorService.execute(new UserAction("user" + i));
	        }
	        executorService.shutdown();
	        logger.error("application end");
	    }
	}
	
	class UserAction extends Thread {
	    private static Logger logger = LoggerFactory.getLogger(UserAction.class);
	    int loops = new Random().nextInt(5);
	    String name;
	
	    public UserAction(String name) {
	        this.name = name;
	        logger.info("user {} has {} loops", name, loops);
	    }
	
	    public void run() {
	        for (int i = 0; i < loops; i++) {
	            logger.info("user {} log in", name);
	            try {
	                Thread.sleep(new Random().nextInt(1000));
	            } catch (InterruptedException e) {
	            }
	            logger.info("user {} log out", name);
	        }
	    }
	}

由于使用多线程，所以其日志输出是混乱的：


	17:51:44.447 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log in
	17:51:44.446 [main] INFO  chapters.introduction.UserAction - user user7 has 1 loops
	17:51:44.449 [main] INFO  chapters.introduction.UserAction - user user8 has 2 loops
	17:51:44.449 [pool-1-thread-2] INFO  chapters.introduction.UserAction - user user7 log in
	17:51:44.450 [main] INFO  chapters.introduction.UserAction - user user9 has 1 loops
	17:51:44.451 [main] ERROR chapters.introduction.HelloWorld - application end
	17:51:44.673 [pool-1-thread-5] INFO  chapters.introduction.UserAction - user user4 log out
	17:51:44.673 [pool-1-thread-5] INFO  chapters.introduction.UserAction - user user4 log in
	17:51:44.864 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log out
	17:51:44.864 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log in
	17:51:44.968 [pool-1-thread-3] INFO  chapters.introduction.UserAction - user user2 log out
	17:51:44.968 [pool-1-thread-3] INFO  chapters.introduction.UserAction - user user8 log in
	17:51:45.138 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log out
	17:51:45.138 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log in
	17:51:45.157 [pool-1-thread-2] INFO  chapters.introduction.UserAction - user user7 log out
	17:51:45.158 [pool-1-thread-2] INFO  chapters.introduction.UserAction - user user9 log in
	17:51:45.174 [pool-1-thread-3] INFO  chapters.introduction.UserAction - user user8 log out
	17:51:45.175 [pool-1-thread-3] INFO  chapters.introduction.UserAction - user user8 log in
	17:51:45.221 [pool-1-thread-5] INFO  chapters.introduction.UserAction - user user4 log out
	17:51:45.279 [pool-1-thread-4] INFO  chapters.introduction.UserAction - user user3 log out
	17:51:45.308 [pool-1-thread-3] INFO  chapters.introduction.UserAction - user user8 log out
	17:51:45.389 [pool-1-thread-6] INFO  chapters.introduction.UserAction - user user5 log out
	17:51:45.389 [pool-1-thread-6] INFO  chapters.introduction.UserAction - user user5 log in
	17:51:45.636 [pool-1-thread-6] INFO  chapters.introduction.UserAction - user user5 log out
	17:51:45.636 [pool-1-thread-6] INFO  chapters.introduction.UserAction - user user5 log in
	17:51:45.959 [pool-1-thread-2] INFO  chapters.introduction.UserAction - user user9 log out
	17:51:46.032 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log out
	17:51:46.032 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log in
	17:51:46.206 [pool-1-thread-6] INFO  chapters.introduction.UserAction - user user5 log out
	17:51:46.862 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user6 log out

这给调试差日志造成了很大困难。

我们可以这样改写配置文件，使用MDC加`SiftingAppender`解决这个问题：

	
	<configuration>
	    <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />
	
	    <appender name="SIFT" class="ch.qos.logback.classic.sift.SiftingAppender">
	        <discriminator>
	            <key>userid</key>
	            <defaultValue>none</defaultValue>
	        </discriminator>
	        <sift>
	            <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
	                <file>logs/access-${userid}.log</file>
	                <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
	                    <fileNamePattern>E:/Project/workspace/branch/logback-test/logs/access-${userid}-%d{yyyy-MM-dd_HH-mm}.log</fileNamePattern>
	                </rollingPolicy>
	                <encoder>
	                    <pattern>%-4relative %d{yyyy-MM-dd_HH-mm} [%thread] %-5level %logger{35} - %msg%n</pattern>
	                </encoder>
	            </appender>
	        </sift>
	    </appender>
	
	    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
	        <!-- encoder 默认配置为PatternLayoutEncoder -->
	        <encoder>
	            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
	        </encoder>
	    </appender>
	
	    <root level="DEBUG">
	        <appender-ref ref="STDOUT" />
	        <appender-ref ref="SIFT" />
	    </root>
	
	</configuration>


修改UserAction的日志输出，增加MDC的调用：


	package chapters.introduction;
	
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.slf4j.MDC;
	
	import java.util.Random;
	import java.util.concurrent.ExecutorService;
	import java.util.concurrent.Executors;
	
	public class HelloWorld {
	    private static Logger logger = LoggerFactory.getLogger(HelloWorld.class);
	
	    public static void main(String[] args) {
	        MDC.put("userid", "main");
	        logger.info("application begin");
	
	        ExecutorService executorService = Executors.newFixedThreadPool(6);
	        for (int i = 0; i < 10; i++) {
	            executorService.execute(new UserAction("user" + i));
	        }
	        executorService.shutdown();
	        logger.error("application end");
	    }
	}
	
	class UserAction extends Thread {
	    private static Logger logger = LoggerFactory.getLogger(UserAction.class);
	    int loops = new Random().nextInt(5);
	    String name;
	
	    public UserAction(String name) {
	        // MDC.put("userid", name); // do not put it here
	        this.name = name;
	    }
	
	    public void run() {
	        MDC.put("userid", name);
	        logger.info("user {} has {} loops", name, loops);
	        for (int i = 0; i < loops; i++) {
	            logger.info("user {} log in", name);
	            try {
	                Thread.sleep(new Random().nextInt(1000));
	            } catch (InterruptedException e) {
	            }
	            logger.info("user {} log out", name);
	        }
	        MDC.remove("userid");
	    }
	}


则输出变成了按userid划分的多个文件：


	access-main-2014-12-18_17-57.log
	access-main.log
	access-none-2014-12-18_17-51.log
	access-none-2014-12-18_17-52.log
	access-none-2014-12-18_17-57.log
	access-none.log
	access-user0-2014-12-18_17-57.log
	access-user0.log
	access-user1-2014-12-18_17-57.log
	access-user1.log
	access-user2-2014-12-18_17-57.log
	access-user2.log
	access-user3-2014-12-18_17-57.log
	access-user3.log
	access-user4-2014-12-18_17-57.log
	access-user4.log
	access-user5-2014-12-18_17-57.log
	access-user5.log
	access-user6-2014-12-18_17-57.log
	access-user6.log
	access-user7-2014-12-18_17-57.log
	access-user7.log
	access-user8-2014-12-18_17-57.log
	access-user8.log
	access-user9-2014-12-18_17-57.log
	access-user9.log


其中对某一个UserAction来说日志是这样的：


	381  2014-12-18_18-00 [main] INFO  chapters.introduction.UserAction - user user0 has 3 loops
	396  2014-12-18_18-00 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user0 log in
	1218 2014-12-18_18-00 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user0 log out
	1218 2014-12-18_18-00 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user0 log in
	2194 2014-12-18_18-00 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user0 log out
	2194 2014-12-18_18-00 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user0 log in
	2495 2014-12-18_18-00 [pool-1-thread-1] INFO  chapters.introduction.UserAction - user user0 log out

对app来说日志是这样的：

	336  2014-12-18_18-06 [main] INFO  chapters.introduction.HelloWorld - application begin
	383  2014-12-18_18-06 [main] ERROR chapters.introduction.HelloWorld - application end

可以看到日志按规则分开了。这个功能一般可以用在多线程程序中，可以清楚地看到每个线程的执行内容。

不过更强的功能意味着更大的复杂性，注意put和remove必须放在同一个线程里，否则肯定会有奇怪的现象发生。

参考资料：

* http://java.dzone.com/articles/siftingappender-logging
* http://logback.qos.ch/manual/mdc.html
* http://logback.qos.ch/manual/loggingSeparation.html

### 继续深入

* http://logback.qos.ch/documentation.html
* logback 架构与流程，与性能优势介绍 http://logback.qos.ch/manual/architecture.html
* http://aub.iteye.com/blog/1101222
* 手册中文翻译 http://dl2.iteye.com/upload/attachment/0086/3438/e0ea2ac8-98bb-31f2-b4c6-9a43bf8baa75.pdf