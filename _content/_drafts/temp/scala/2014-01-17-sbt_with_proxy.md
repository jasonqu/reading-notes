---
layout: post
title: sbt
tagline: code better
tags : ["sbt","scala"]
---

网上的都是说怎样自建一个Repository
Set up your SBT for personal proxy use
http://engineering.clever-cloud.com/sysadmin/2013/11/30/set-up-sbt-for-proxy-use.html

http://www.scala-sbt.org/0.13.0/docs/Detailed-Topics/Setup-Notes.html
http://www.scala-sbt.org/release/docs/Detailed-Topics/Proxy-Repositories.html

我的问题比较简单，就是穿过防火墙。还好有fiddler，所以只要修改一下sbt脚本就行了：
<pre>
"%_JAVACMD%" %SBT_OPTS% -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8888 -cp "%SBT_HOME%sbt-launch.jar" xsbt.boot.Boot %*
</pre>

来源：
http://stackoverflow.com/questions/13803459/how-to-use-sbt-from-behind-a-proxy

同理在运行play的时候如果需要增加代理，只需要同样修改framework下的build.bat即可。



sbt local ivy home
http://stackoverflow.com/questions/3142856/configure-sbts-ivy-cache-directory-per-user-or-system-wide
修改sbt启动脚本即可
 -Dsbt.ivy.home=F:/Software/OpenSource/jar/ 


add proxy
http://www.scala-sbt.org/0.13/docs/Proxy-Repositories.html


