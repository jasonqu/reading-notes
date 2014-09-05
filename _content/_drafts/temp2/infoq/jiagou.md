http://www.infoq.com/cn/presentations/architecture-design-and-architects

架构是什么？
代码 = 算法 + 数据结构
软件 = 代码 + 架构
Reuse
Extensibility

系统 = 软件 + 资源
Multiplexing

大规模系统 = 系统 + 分布式架构
Elasticity
Manageability

云 = 大规模系统 + 人 + 数据
Evolution


架构的演进
过程                        人+数据
运营               数据中心
部署      单机资源
开发 代码

形态 软件 系统     服务     云

对架构师的要求
看到全局    抽象
降低复杂度  分解
把握过程    敏捷、迭代

存储
结构
 File
 Object
 Table
访问模式
 实时读写
 批量写、实时读
 流式读
 Scan / Range Query
数据特点
 Mutable or Not
 Size
 Data Layout
“实时性”
 Realtimeness
 Freshness
 Consistency

存储
矛盾
 延迟与吞吐
 随机与顺序
 规模与实时性
模型
 B+ tree （实时、随机）
 Log-based （批量、顺序）
化解矛盾
 弱化需求
 发掘局部性
 组合模型

服务架构
目标
 高吞吐
 极限压力下稳定输出
模型
 基本：Threadpool + Queue
 复杂：Event-driven
稳定性保证
 减小资源分配粒度，主动调度
 Flow Control
  负载反馈，Throttling
  延迟截断，分级队列

计算
数据密集型
 MapReduce
 Scan-Filter
计算密集型
 seti@home
通讯密集型（HPC）
 机器学习系统
 矩阵计算

架构师三板斧
看清需求
对不合理需求 say NO!
但给他end-to-end解决方案。
 Tradeoff
 无法满足所有需求
 无须同等对待所有需求
 发现根本需求
 分解、抽象、降维
 定义primitives和组合规则
 了解需求随时间的变化

选择方法
 测算 -> 模拟 -> 实现
 分解 vs 迭代
 设计模式
Back-of-Envelop Calculation
Monte-Carlo Simulation
Discrete Event Simulation
Emulation

把握节奏
 规划可达路径
 定期产出

架构师技能模型
内力 编程、重构
招式 Paper
经验 项目

What
How  Coding Hardworking
Why  Thinking Reading




http://www.infoq.com/cn/presentations/programmer-should-know-97-things
每个程序员都应该知道的97件事
讲的听不懂，直接看ppt

It is far better to have an underfeatured product that is rock solid, fast, and small than one that covers what an expert would consider the complete requirements.

Simplicity: The design is simple in implementation. The interface should be simple, but anything adequate will do.
Completeness: The design covers only necessary situations. Completeness can be sacrificed in favor of any other quality.
Correctness: The design is correct in all observable aspects.
Consistency: The design is consistent as far as it goes. Consistency is less of a problem because you always choose the smallest scope for the first implementation.

Implementation characteristics are foremost:
The implementation should be fast.
It should be small.
It should interoperate with the programs and tools that the expected users are already using.
It should be bug-free, and if that requires implementing fewer features, do it.
It should use parsimonious abstractions as long as they don’t get in the way.

OOP to me means only messaging, local retention and protection and hiding of state-process, and extreme late-binding of all things.
It can be done in Smalltalk and in LISP. There are possibly other systems in which this is possible, but I'm not aware of them.
Alan Kay

In a purist view of object-oriented methodology, dynamic dispatch is the only mechanism for taking advantage of attributes that have been forgotten by subsumption. This position is often taken on abstraction grounds: no knowledge should be obtainable about objects except by invoking their methods. In the purist approach, subsumption provides a simple and effective mechanism for hiding private attributes.


There have always been fairly severe size constraints on the Unix operating system and its software. Given the partially antagonistic desires for reasonable efficiency and expressive power, the size constraint has encouraged not only economy but a certain elegance of design. 
Dennis Ritchie and Ken Thompson "The UNIX Time-Sharing System", CACM

This is the Unix philosophy: Write programs that do one thing and do it well. Write programs to work together. Write programs to handle text streams, because that is a universal interface. 
Doug McIlroy

http://www.johndcook.com/blog/2010/06/30/where-the-unix-philosophy-breaks-down/
The hard part isn’t writing little programs that do one thing well. The hard part is combining little programs to solve bigger problems. In McIlroy’s summary, the hard part is his second sentence: Write programs to work together.

Software applications do things they’re not good at for the same reason companies do things they’re not good at: to avoid transaction costs.

John D Cook

Architecture is the decisions that you wish you could get right early in a project, but that you are not necessarily more likely to get them right than any other. 
Ralph Johnson

Properly gaining control of the design process tends to feel like one is losing control of the design process.



介绍Web基础架构设计原则的经典论文《架构风格与基于网络的软件架构设计》导读
http://www.infoq.com/cn/articles/doctor-fielding-article-review

http://www.infoq.com/cn/presentations/lefeng-electricity-supplier-sales-season
乐蜂网“电商促销季”
应用系统压力直翻十五倍，连续支撑两天大型电商产品促销，系统无宕机；超百万订单数，超两亿成交额…… 这是一次中小型互联网企业的案例分析。国内的二线中小型互联网企业数量众多，像淘宝这种企业毕竟是凤毛麟角。在资源紧张、能力有限的情况下，技术团队如何应对重大促销活动，如：如何确保系统可用性，如何为应用系统进行取舍、寻找平衡点，如何善待使用第三方资源，乃至于如何“借力”协调运营、市场、公关等内部资源做事，团队管理者怎样帮助员工提高心理素质等。 本次分享中，我会主要介绍我们遇到了哪些问题，以及相应的解决思路。涉及的内容包括： 项目启动需求分析 分析主要业务数据 剖析历史故障 自己吓自己——幻想故障 善待第三方资源 系统风险评估和预案汇总 快速评估影响范围 成立现场指挥部 活动结束，问题聚焦


http://www.infoq.com/cn/articles/challenges-and-optimization-of-cross-border-website
阿里巴巴网站架构师周涛明：跨境网站的优化与挑战
很多干货

