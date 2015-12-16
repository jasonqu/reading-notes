---
layout: post
title: Nginx 相关资料学习
tagline: code better
tags : ["nginx","web server"]
---

### 为什么使用反向代理

"Nginx反向代理为什么可以提高网站性能？":http://www.zhihu.com/question/19761434
























查看服务状态：

<pre>
root@TGWEB:/usr/local/nginx> nmap -p 8000 localhost

Starting Nmap 4.00 ( http://www.insecure.org/nmap/ ) at 2013-11-12 14:15 CST
Interesting ports on localhost (127.0.0.1):
PORT     STATE SERVICE
8000/tcp open  http-alt

Nmap finished: 1 IP address (1 host up) scanned in 0.051 seconds
root@TGWEB:/usr/local/nginx> ps aux | grep nginx
root     29725  0.0  0.0   8500   532 ?        Ss   14:15   0:00 nginx: master process ./sbin/nginx
nobody   29726  0.0  0.0   8896  1248 ?        S    14:15   0:00 nginx: worker process

查看端口有没有监听
root@TGWEB:/usr/local/nginx> netstat -tulnp | grep :8000
tcp        0      0 0.0.0.0:8000            0.0.0.0:*               LISTEN      29725/nginx         
</pre>

停止就是用kill 发信号：
可以使用这个命令查看 信号量 man 7 signal

<pre>

# 从容关闭
kill -QUIT $(cat /var/run/nginx/nginx.pid)
# 快速停止
kill -TERM $(cat /var/run/nginx/nginx.pid)
# 强制结束
kill -9 nginx

# 平滑重启
kill -HUP $(cat /var/run/nginx/nginx.pid)
</pre>





4. 配置文件说明
查看cpu信息
cat /proc/cpuinfo
top 可以查看平均负载 是按照cpu核数查看的
修改配置文件 的工作进程数，一核分配一个进程即可
 worker_processes  1; 

这里是并发支持数，可以跟据实际情况填写
events {
    worker_connections  1024;
}

sendfile        on;
可以避免每次都从user层调取kernal层的信息，而把kernal的信息放在缓存中
keepalive_timeout  65;
表示超时设置
gzip
压缩配置

location下有一个
autoindex on
alias ...
deny allow ...
还可以指定一个虚拟的目录进行stub status 的状态展示


详细配置处理见这里
http://nginx.org/cn/docs/http/request_processing.html
Nginx如何处理一个请求
http://nginx.org/cn/docs/http/server_names.html
虚拟主机名

nslookup
查看域名

修改配置文件之后可以使用 nginx -t -c +配置文件名 来检查配置文件正确性


地址重写
可以通过rewrite进行
按资源重写
按User Agent 重写
防盗链
实现域名跳转


nginx有很多全局变量
http_x_forwarded_for 记录远程用户ip，而不是缓存或代理的ip

Nginx浏览器本地缓存设置 功能还是很强大的

设定限速 常用于流播放器




