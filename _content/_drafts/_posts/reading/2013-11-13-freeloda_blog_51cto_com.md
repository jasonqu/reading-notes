---
layout: post
title: Nginx 相关资料总结 on freeloda.blog.51cto.com
tagline: reading archive
category : reading
tags : [reading,web,linux,nginx,"web server",archive]
---

#### 介绍

http://freeloda.blog.51cto.com/ 是一个很不错的博客，专门学习了一下，总结在这里

###### Nginx系列 写得非常好

* [Web服务器之Nginx详解（理论部分）](http://freeloda.blog.51cto.com/2033581/1285332)
* [Web服务器之Nginx详解（操作部分）](http://freeloda.blog.51cto.com/2033581/1285722)
* [Nginx 反向代理、负载均衡、页面缓存、URL重写及读写分离详解](http://freeloda.blog.51cto.com/2033581/1288553)


* [http反向代理之haproxy详解](http://freeloda.blog.51cto.com/2033581/1294094)
* [Varnish 详解](http://freeloda.blog.51cto.com/2033581/1297414)
* Memcache相关 略

###### Tomcat 系列

* [Apache 整合 Tomcat](http://freeloda.blog.51cto.com/2033581/1189108)

* [Tomcat系列之Java技术详解](http://freeloda.blog.51cto.com/2033581/1298687)
* [Tomcat系列之服务器的安装与配置以及各组件详解](http://freeloda.blog.51cto.com/2033581/1299644)
* [Tomcat系列之服务器的基本配置及Nginx反向代理tomcat服务](http://freeloda.blog.51cto.com/2033581/1300915)
* [Tomcat系列之Apache使用mod_proxy和mod_jk反向代理Tomcat](http://freeloda.blog.51cto.com/2033581/1301382)
* [Tomcat系列之Apache负载均衡请求至Tomcat及DeltaManager的使用](http://freeloda.blog.51cto.com/2033581/1301888)

##### https

* [加密、解密及OpenSSL 详解](http://freeloda.blog.51cto.com/2033581/1216176)
* [SSH 协议与OpenSSH详解](http://freeloda.blog.51cto.com/2033581/1216374)
* [基于openssl的https服务配置](http://freeloda.blog.51cto.com/2033581/1218939)


##### web 架构

* [基于LAMP平台的网站架构](http://freeloda.blog.51cto.com/2033581/1131591)
  有一些不错的参考资料


##### linux hacks

* [使用TAR备份最近1天更改过的文件](http://freeloda.blog.51cto.com/2033581/1119756)

<pre>
1.使用find 命令找出etc目录下1天内修改过的文件,导入到file文件
find /etc -mtime -1 > file
cat file
2. 使用TAR备份最近1天更改过的文件
tar -cvf etc.tar -T file
</pre>

* [Linux下查看cpu具体信息](http://freeloda.blog.51cto.com/2033581/1189084)
* [Linux常用设备查看命令：系统、网络、进程、用户](http://freeloda.blog.51cto.com/2033581/1189098)
* [查看 nginx、apache、mysql、php 软件的编译参数](http://freeloda.blog.51cto.com/2033581/1189100)

* [Linux 性能监测工具](http://freeloda.blog.51cto.com/2033581/1189103)

  * uptime服务器负荷概况
  * dmesg 内核信息
  * top 显示处理器的活动状况
  * iostat CPU使用情况和磁盘使用情况
  * vmstat提供了processes, memory, paging, block I/O, traps和CPU的活动状况
  * sar用于收集、报告或者保存系统活动信息
  * free 显示所有空闲的和使用的内存数量
  * pmap可以报告某个或多个进程的内存使用情况
  * strace截取和记录系统进程调用，以及进程收到的信号
  * ulimit内置在bash shell中，用来提供对shell和进程可用资源的控制
  * mpstat用于报告多路CPU主机的每颗CPU活动情况，以及整个主机的CPU情况

* [Linux改变进程优先级的nice命令](http://freeloda.blog.51cto.com/2033581/1189104)

* [iptables 禁ping脚本](http://freeloda.blog.51cto.com/2033581/1189205)
* [iptables 做web防火墙脚本](http://freeloda.blog.51cto.com/2033581/1189201)
* [iptables 做vsftp防火墙脚本](http://freeloda.blog.51cto.com/2033581/1189198)
* [iptables 做mail防火墙脚本](http://freeloda.blog.51cto.com/2033581/1189194)
* [iptables 做nat路由器脚本](http://freeloda.blog.51cto.com/2033581/1189188)

* [防止SSH爆力破解脚本](http://freeloda.blog.51cto.com/2033581/1189211)


##### 运维案例

* [动态网站维护基本命令](http://freeloda.blog.51cto.com/2033581/1131591)
  介绍了一个apache+mysql服务的状态查看及监控、故障处理、系统容量扩展、数据备份/恢复/站点迁移的案例
* [Linux 中交换空间(swap)应该分多大才好](http://freeloda.blog.51cto.com/2033581/1199449)

##### 其他

* [Heartbeat+DRDB+NFS 构建高可用的文件系统](http://freeloda.blog.51cto.com/2033581/1189126)
* Linux高可用 均衡器 keepalived、LVS、Haproxy 监控 Nagios + Cacti 略
* MySQL 常用show命令、主从复制 etc
* 运维自动化相关 这里有一个概述 http://freeloda.blog.51cto.com/2033581/1306112
