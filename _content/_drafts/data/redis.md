http://try.redis.io/

Numerical values in hash fields are handled exactly the same as in simple strings and there are operations to increment this value in an atomic way.


    HSET user:1000 visits 10
    HINCRBY user:1000 visits 1 => 11
    HINCRBY user:1000 visits 10 => 21
    HDEL user:1000 visits
    HINCRBY user:1000 visits 1 => 1












[10 个 Redis 建议/技巧](http://blog.jobbole.com/88170/)
1、停止使用 KEYS * 尝试一下scan
2、找出拖慢 Redis 的罪魁祸首
INFO commandstats
3、 将 Redis-Benchmark 结果作为参考，而不要一概而论
4、Hashes 是你的最佳选择
5、设置 key 值的存活时间
无论什么时候，只要有可能就利用key超时的优势。一个很好的例子就是储存一些诸如临时认证key之类的东西。当你去查找一个授权key时——以OAUTH为例——通常会得到一个超时时间。这样在设置key的时候，设成同样的超时时间，Redis就会自动为你清除！而不再需要使用KEYS *来遍历所有的key了，怎么样很方便吧？
6、 选择合适的回收策略

既然谈到了清除key这个话题，那我们就来聊聊回收策略。当 Redis 的实例空间被填满了之后，将会尝试回收一部分key。根据你的使用方式，我强烈建议使用 volatile-lru 策略——前提是你对key已经设置了超时。但如果你运行的是一些类似于 cache 的东西，并且没有对 key 设置超时机制，可以考虑使用 allkeys-lru 回收机制。我的建议是先在这里查看一下可行的方案。
http://redis.io/topics/lru-cache#eviction-policies
7、如果你的数据很重要，请使用 Try/Except

如果必须确保关键性的数据可以被放入到 Redis 的实例中，我强烈建议将其放入 try/except 块中。几乎所有的Redis客户端采用的都是“发送即忘”策略，因此经常需要考虑一个 key 是否真正被放到 Redis 数据库中了。至于将 try/expect 放到 Redis 命令中的复杂性并不是本文要讲的，你只需要知道这样做可以确保重要的数据放到该放的地方就可以了。
8、不要耗尽一个实例
http://redis.io/topics/cluster-spec
http://redis.io/topics/cluster-tutorial
http://redis.io/topics/partitioning
9、内核越多越好吗？！

当然是错的。Redis 是一个单线程进程，即使启用了持久化最多也只会消耗两个内核。
10、高可用

到目前为止 Redis Sentinel 已经经过了很全面的测试，很多用户已经将其应用到了生产环境中（包括 ObjectRocket ）

[Redis 的 5 个常见使用场景](http://blog.jobbole.com/88383/)

1、会话缓存（Session Cache）
2、全页缓存（FPC）
3、队列
4、排行榜/计数器
我们要从排序集合中获取到排名最靠前的10个用户–我们称之为“user_scores”，我们只需要像下面一样执行即可：
ZRANGE user_scores 0 10
如果你想返回用户及用户的分数，你需要这样执行：
ZRANGE user_scores 0 10 WITHSCORES
5、发布/订阅

Redis 使用模式之一：计数器
http://xiewenwei.github.io/blog/2014/07/06/redis-use-pattern-1-counter/

使用 Hash 数据类型维护大量计数器

有时候需要维护大量计数器，比如每一个论坛主题的查看数，比如每一个用户访问页面次数，因为论坛主题和用户基数可能很大，直接基于论坛主题或用户 ID 生成计数器的话，占用 Redis 资源还是相当可观的，这时可以用 Hash 数据类型压缩所需资源。

比如，对应论坛主题查看计数，可以由模式

  key: topic:<topic_id>:views
  value: view count (integer)

转换为模式：

  key: topic:views
  value: hash
    hash key: <topic_id>
    hash value: view count (integer)

使用 Redis 进行唯一计数的 3 种方法
http://xiewenwei.github.io/blog/2014/12/28/cardinality-counting-using-redis/
1.基于 set
2.基于 bit
3.基于 HyperLogLog


http://redisbook.readthedocs.org/en/latest/datatype/hash.html


Redis 常见使用模式分析
http://www.slideshare.net/vincent253/redis-37221509

redis小书
https://github.com/karlseguin/the-little-redis-book
有中文翻译链接
https://github.com/geminiyellow/the-little-redis-book/blob/master/zh-cn/redis.md

一亿两千八百万用户，在笔记本上测试，50ms 内做出了回答，而且只占用了16MB的内存。
http://blog.getspool.com/2011/11/29/fast-easy-realtime-metrics-using-redis-bitmaps/

Redis 设计与实现
https://github.com/huangz1990/redisbook


redis


Videos

Scaling Redis at Twitter
https://www.youtube.com/watch?v=rP9EKvWt0zo

Redis Use Patterns: An Introduction to the SQL Practitioner
https://www.youtube.com/watch?v=8Unaug_vmFI

Why and When You Should Use Redis
https://www.youtube.com/watch?v=CoQcNgfPYPc