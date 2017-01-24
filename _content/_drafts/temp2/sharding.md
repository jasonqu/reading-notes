# sharding

https://en.wikipedia.org/wiki/Shard_%28database_architecture%29

Horizontal partitioning is a database design principle whereby rows of a database table are held separately, rather than being split into columns (which is what normalization and vertical partitioning do, to differing extents). Each partition forms part of a shard, which may in turn be located on a separate database server or physical location.

Disadvantages include :
* A heavier reliance on the interconnect between servers
* Increased latency when querying, especially where more than one shard must be searched.
* Data or indexes are often only sharded one way, so that some searches are optimal, and others are slow or impossible.
* Issues of consistency and durability due to the more complex failure modes of a set of servers, which often result in systems making no guarantees about cross-shard consistency or durability.

here is a desire to support sharding automatically, both in terms of adding code support for it, and for identifying candidates to be sharded separately. Consistent hashing is one form of automatic sharding to spread large loads across multiple smaller services and servers.


https://en.wikipedia.org/wiki/Partition_(database)
Range List Hash Composite 
Horizontal partitioning (also see shard)
Vertical partitioning involves creating tables with fewer columns and using additional tables to store the remaining columns.

其实垂直切分很常见，平时的schema设计就是垂直切分

[数据库Sharding的基本思想和切分策略](http://blog.csdn.net/bluishglc/article/details/6161475)

[数据库分库分表(sharding)系列(二) 全局主键生成策略](http://blog.csdn.net/bluishglc/article/details/7710738)

http://code.flickr.com/blog/2010/02/08/ticket-servers-distributed-unique-primary-keys-on-the-cheap/

与一般Sequence表方案类似，但解决了性能瓶颈和单点问题，是一种可靠高效的全局主键生成方案
两台数据库ID生成服务器，那么结果就是奇数的ID都将从第一台服务器上生成，偶数的ID都从第二台服务器上生成
使用auto increment的初始值和步长来确定

[又拍网架构中的分库设计](http://www.infoq.com/cn/articles/yupoo-partition-database)

[Sharding & IDs at Instagram](http://instagram-engineering.tumblr.com/post/10853187575/sharding-ids-at-instagram)

Each of our IDs consists of:

* 41 bits for time in milliseconds (gives us 41 years of IDs with a custom epoch)
* 13 bits that represent the logical shard ID
* 10 bits that represent an auto-incrementing sequence, modulus 1024. This means we can generate 1024 IDs, per shard, per millisecond


[DATABASE SHARDING](http://www.agildata.com/database-sharding/)

[What is sharding and why is it important?](http://stackoverflow.com/questions/992988/what-is-sharding-and-why-is-it-important)

一致性哈希(consistent hashing)

[每天进步一点点——五分钟理解一致性哈希算法(consistent hashing)](http://blog.csdn.net/cywosp/article/details/23397179)

[一致性 hash 算法（ consistent hashing ）](http://blog.csdn.net/sparkliang/article/details/5279393)
内容和上文差不多，有些参考链接






