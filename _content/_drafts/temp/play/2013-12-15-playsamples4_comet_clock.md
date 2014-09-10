---
layout: post
title: play2 samples 2 - Forms
tagline: code better
category : play framework
tags : ["play2","web framework","scala"]
---

### 介绍

对play提供的scala samples进行学习的一个记录。在看这些例子之前要先过一遍play的文档作为知识基础。文章中的例子来自 play-2.2.3。

### comet-clock

comet 是[基于 HTTP 长连接的“服务器推”技术](http://www.ibm.com/developerworks/cn/web/wa-lo-comet/)，在websocket出现之前使用比较多。使用时候有一个比较明显的缺点就是页面下载框会不停的显示。

#### 功能介绍

一个时钟：

![时钟](/img/playsamples/comet-clock.png "时钟")

##### routes

只有两个，一个是index的页面显示，另外一个是永不停止的数据源clock。

<pre>
# Home page
GET     /                           controllers.Application.index
# The clock Comet stream
GET     /clock                      controllers.Application.liveClock
</pre>










