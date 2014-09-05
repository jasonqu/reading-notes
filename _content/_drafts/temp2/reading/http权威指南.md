http权威指南
这里最权威
http://www.w3.org/Protocols/rfc2616/rfc2616.html

P17 netcat 可以方便的操纵udp tcp流量

31 参数
http://www.joes-hardware.com/hammers;sale=false/index.html;graphics=true
query
http://www.joes-hardware.com/inventory-check.cgi?item=12731

70 head
http://en.wikipedia.org/wiki/List_of_HTTP_header_fields

85 http性能
http://blog.csdn.net/csh624366188/article/details/8597056
http://blog.csdn.net/jkay_wong/article/details/8580727
http://www.cnblogs.com/artwl/p/3340576.html
http://blog.csdn.net/wangchun8926/article/details/8495243  目录

125 io
128 虚拟主机
一台服务器，虚拟托管了两个docroot，访问时通过host首部，或ip区分
<VirtualHost www.joes-hardware.com>
	ServerName www.joes-hardware.com
	DocumentRoot /docs/joe
	TransferLog /logs/joe.access_log
	ErrorLog /logs/joe.error_log
</VirtualHost>
<VirtualHost www.marys-antiques.com>
	ServerName www.marys-antiques.com
	DocumentRoot /docs/mary
	TransferLog /logs/mary.access_log
	ErrorLog /logs/mary.error_log
</VirtualHost>

138 代理
为什么？
	儿童过滤
	文档访问控制
	防火墙
	web缓存
	反向代理：假扮服务器，收发真实请求，并与其他服务器通信，按需定位；通常将它和内容路由结合，创建按需复制内容的分布式网络
	内容路由器
	转码器
	匿名者 删除ip from referer cookie uri sessionid ua cookie

部署
	出口代理
	访问代理
	反向代理
	网络交换代理

代理可以有多层，用来处理内容分发、负载均衡、地理位置路由、协议路由、基于订购的路由

客户端：手工 或者 pac文件配置

P204 缓存与广告 两难 日志迁移 高效的日志分发策略；命中计数

P217 隧道 web tunnel



P307 随机数 13.1.3

P324 https


P366 多部分表格提交
填写http form时提交的数据结构，如：
<FORM action="http://server.com/cgi/handle"
	enctype="multipart/form-data"
	method="post">
<P>
What is your name? <INPUT type="text" name="submit-name"><BR>
What files are you sending? <INPUT type="file" name="files"><BR>
<INPUT type="submit" value="Send"> <INPUT type="reset">
</FORM>

如果用户填写了姓名Sally，并选择了文本文件easy.txt，则点击提交时Agent代理可能会发回下面的数据：
Content-Type: multipart/form-data; boundary=AaB03x
--AaB03x
Content-Disposition: form-data; name="submit-name"
Sally
--AaB03x
Content-Disposition: form-data; name="files"; filename="essayfile.txt"
Content-Type: text/plain
...contents of essayfile.txt...
--AaB03x--

如果还选择了image.gif 则Agent代理可能会这样构造
Content-Type: multipart/form-data; boundary=AaB03x
--AaB03x
Content-Disposition: form-data; name="submit-name"
Sally
--AaB03x
Content-Disposition: form-data; name="files"
Content-Type: multipart/mixed; boundary=BbC04y
--BbC04y
Content-Disposition: file; filename="essayfile.txt"
Content-Type: text/plain
...contents of essayfile.txt...
--BbC04y
Content-Disposition: file; filename="imagefile.gif"
Content-Type: image/gif
Content-Transfer-Encoding: binary
...contents of imagefile.gif...
--BbC04y--
--AaB03x--

相应也可以是多部分的
HTTP/1.0 206 Partial content
Server: Microsoft-IIS/5.0
Date: Sun, 10 Dec 2000 19:11:20 GMT
Content-Location: http://www.joes-hardware.com/gettysburg.txt
Content-Type: multipart/x-byteranges; boundary=--[abcdefghijklmnopqrstuvwxyz]--
Last-Modified: Sat, 09 Dec 2000 00:38:47 GMT
--[abcdefghijklmnopqrstuvwxyz]--
Content-Type: text/plain
Content-Range: bytes 0-174/1441
Fourscore and seven years ago our fathers brough forth on this continent
a new nation, conceived in liberty and dedicated to the proposition that
all men are created equal.
--[abcdefghijklmnopqrstuvwxyz]--
Content-Type: text/plain
Content-Range: bytes 552-761/1441
But in a larger sense, we can not dedicate, we can not consecrate,
we can not hallow this ground. The brave men, living and dead who
struggled here have consecrated it far above our poor power to add
or detract.
--[abcdefghijklmnopqrstuvwxyz]--
Content-Type: text/plain
Content-Range: bytes 1344-1441/1441
and that government of the people,


P373 分块编码 chunked encoding
见chunked encoding.jpg

P380 范围请求 即请求相应实体的一部分，在头部加 Range
GET /bigfile.html HTTP/1.1
Host: www.joes-hardware.com
Range: bytes=4000-
User-Agent: Mozilla/4.61 [en] (WinNT; I)
...

见 range request.jpg
常见于P2P程序中

还有差异编码 P382












