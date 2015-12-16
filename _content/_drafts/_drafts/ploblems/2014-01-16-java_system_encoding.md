---
layout: post
title: system encoding in java
tagline: code better
tags : ["system encoding","java","archive"]
---

#### 问题描述

在日志文件中打印的中文变成了一堆问号。

#### 问题原因

关于这个问题的原理性的原因在这里有详细的介绍：许令波《深入分析 Java 中的中文编码问题》
http://www.ibm.com/developerworks/cn/java/j-lo-chinesecoding/

从中可以看出是因为日志文件用了类似ISO-8859-1这样的编码导致的问题。

既然我没有在程序中设定文件字符集，java就一定用了某一个默认的字符集，那么问题就是怎样找到系统的默认字符集，修改之；或者是怎样使用指定的字符集。

#### 解决方法

这个解决办法网上有很多：
http://stackoverflow.com/questions/1749064/how-to-find-default-charset-encoding-in-java
http://javarevisited.blogspot.com/2012/01/get-set-default-character-encoding.html

总结一下，获取系统文件默认字符集的方法：
<pre>
    System.getProperty("file.encoding")
</pre>

修改之的方式中侵入性最小的方式是启动时加一个参数 <code>-Dfile.encoding="UTF-8"</code>，例如：

<pre>
java -Dfile.encoding="UTF-8"  HelloWorld
</pre>
