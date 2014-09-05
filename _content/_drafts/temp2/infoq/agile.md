---
layout: post
category : infoq
---

http://www.infoq.com/cn/presentations/large-financial-enterprise-project-deployment-pipeline-evolution

我们希望Test load balancer满足
自动分发测试
对测试透明
容易理解，简单实现
通用性
对CI透明
在本地／CI使用相同的机制

最后保留了前4个目标


大集成测试
• 不稳定 （平均四天变“绿”一次）
• 运行时间长，不能提供快速反馈
• 关注度下降


Conway’s Law [p://en.wikipedia.org/wiki/Conway%27s_Law

Infrastructure
as
Code
• 让机器的配置和状态版本化、可追踪
• 明确环境的不同差异
• 快速搭建新的环境
• chocolatey.org | skylight

愿景
• 部署流水线向前延伸到开发人员，使开发
人员可以使用开发云，快速创建与生产环
境相似的开发环境，进行更快速的本地构
建；
• 部署流水线向后延伸到生产环境，降低商
业想法从产生到上线的时间。

四年多的时间，部署流水线也像代码、架构
一样，不断地演化，以适应新的环境和要求；
部署流水线不再是独立的实践，而是成为一个
平台，为其他实践提供良好的基础。


云计算与持续集成——七牛的实践
http://www.infoq.com/cn/presentations/cloud-computing-and-continuous-integration
感受 对缺陷要零容忍


http://www.infoq.com/cn/articles/db-versioning-scripts
我们的项目采用了敏捷方式，意味着应用程序是渐进与迭代式进行开发的，数据库也成为这套软件开发流程中的一部分。首先要做的是定义“完成标准”（Definition of Done – DoD），这对每个高效团队都是非常重要的。用户故事（User Story）级别的完成标准应该包含一个“可发布”的条件，这表示我们只需考虑用户故事的完成，而它随后可以通过脚本自动发布。当然完成标准中还有其它很多条件（编写数据库升级脚本也是其中之一），不过这个主题完全可以自成一篇文章了。

DevOps之7大习惯
http://www.infoq.com/cn/news/2013/09/7-devops-habits
弗雷斯特研究公司（Forrester Research）分析师格伦·奥唐纳（Glenn O'Donnell）和库尔特·比特纳（Kurt Bittner）发表了一份报告，该报告不仅介绍了开发人员与运维人员在隔离状态下工作时是如何看待彼此的，还给出了双方协作的七大习惯。

促使双方互相交谈
对每件事都采用由外而内的方法去处理
使构建、测试及发布过程自动化，以便减少其中包含的人为错误
使开发和生产环境简化并标准化
向从开发到运维的全过程逐渐灌输系统工程文化
实现反馈和前馈[1]回路
把开发人员放在一线支持的岗位上


http://www.infoq.com/cn/news/2013/10/cynefin-framework-playing-lego
通过玩Lego理解Cynefin框架


http://www.infoq.com/cn/articles/knowledge-design-thinking
在设计思维过程中捕获知识并使决策保持清晰性


http://www.infoq.com/cn/news/2013/10/decision-taking-Agile
用于敏捷的决策技术

在博客文章停止拖延，做出决策中，Vin D'Amico针对决策制定分享了一些想法。他解释了决策为何重要：
如果你的团队真的想要变得敏捷，那么必须快速决策，并且果断地使用最佳的有效信息。而决策总是要能够根据境地变迁进行重新调整。

若干不同方法，可以用在这样的群体决策中：
共识驱动（要求减轻反对意见）
独立投票（多数原则）
计分投票（参与者可以多次投票）

Stop Dawdling and Make a Decision
http://brainslink.com/2013/07/stop-dawdling-and-make-a-decision/

Individual Decision-Maker
Placing decisions in the hands of a single individual requires strong leadership. The decision-maker must have the respect and trust of those impacted by her decisions. This approach may spread authority over several individuals or concentrate it in one person. For example,

Product Owner makes software feature/function decisions
Scrum Master makes process decisions
Tech Lead makes technical decisions
Organizational manager makes all decisions (not recommended)
Group Decision-Making
There are many ways for groups to arrive at decisions. The best approach often depends on the company culture and the personalities within the group.

Consensus-driven (requires that objections be mitigated)
Individual voting (majority rules)
Range voting (people may cast multiple votes)
Committee Decision-Making
Pushing problems and issues to a committee for evaluation and direction is a common practice. It’s also inefficient. Not recommended.

Just Decide
Often the decision-making process isn’t clearly defined. It may even vary with the type of problem being solved or issue being managed. The worst situation is one in which nobody can make a decision and items just languish — lost in a kind of decision limbo.


通过由瀑布到敏捷的转换来减少浪费
http://www.infoq.com/cn/news/2013/10/waste-waterfall-agile

组织为什么要转向敏捷？一个原因是它可以使组织处理变化的能力更强。项目进行过程中，用户需求会经常变化，这就需要开发团队能够适应产品需求。敏捷帮助团队交付满足用户需要的产品；这些产品不包含不需要（而且没有用）的特性。精益软件开发使用术语“浪费”：一切不增加用户价值的特性都视为浪费。由瀑布到敏捷软件开发的转换是如何帮助组织减少浪费的呢？

但是对我而言，真正起决定作用的——使我发生了由对敏捷的热衷到对瀑布的绝望这一转变——是浪费。浪费资源，浪费开发时间，浪费精力。




