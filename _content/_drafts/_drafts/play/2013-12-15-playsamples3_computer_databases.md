---
layout: post
title: play2 samples 2 - Forms
tagline: code better
category : play framework
tags : ["play2","web framework","scala"]
---

### 介绍

对play提供的scala samples进行学习的一个记录。在看这些例子之前要先过一遍play的文档作为知识基础。文章中的例子来自 play-2.2.3。

### computer-database

一个CRUD的数据库项目，这个示例介绍了怎样通过Anorm连接数据库，编写带分页的CRUD表单等功能。

工程需要添加数据库依赖`libraryDependencies ++= Seq(jdbc, anorm)`

#### 功能介绍

一个列表页，能够分页展示数据库的数据：

![列表页](/img/playsamples/computer-database-index.png "列表页")

一个创建或更新Computer实体的表单页：

![表单页](/img/playsamples/computer-database-detail.png "表单页")

##### routes

主要划分为两个模块：列表页、实体的CRUD。使用了相同的controller Application。

<pre>
# 都是列表页，前者展示所有，后者增加了分页、过滤词等参数
GET     /                           controllers.Application.index
GET     /computers                  controllers.Application.list(p:Int ?= 0, s:Int ?= 2, f ?= "")


# computer实体的增删改查
GET     /computers/new              controllers.Application.create
POST    /computers                  controllers.Application.save

GET     /computers/:id              controllers.Application.edit(id:Long)
POST    /computers/:id              controllers.Application.update(id:Long)
POST    /computers/:id/delete       controllers.Application.delete(id:Long)
</pre>

###### base view

main.scala.html 接受一个参数 @(content: Html) —— 页面的内容

##### model

由于需要使用数据库，所以使用了Anorm来处理数据库的操作。application.conf有这样的配置

<pre class="prettyprint"><code>
db.default.driver=org.h2.Driver
db.default.url="jdbc:h2:mem:play"
</code></pre>

当然生产环境可以有更为复杂的设置。下面一步是数据库的操作脚本，数据库脚本在`conf\evolutions`目录下存放：

<pre class="prettyprint"><code>
create table company (
  id                        bigint not null,
  name                      varchar(255) not null,
  constraint pk_company primary key (id))
;

create table computer (
  id                        bigint not null,
  name                      varchar(255) not null,
  introduced                timestamp,
  discontinued              timestamp,
  company_id                bigint,
  constraint pk_computer primary key (id))
;
</code></pre>

对应的scala类是这样：

<pre class="prettyprint"><code>
case class Company(id: Pk[Long] = NotAssigned, name: String)
case class Computer(id: Pk[Long] = NotAssigned, name: String, introduced: Option[Date], discontinued: Option[Date], companyId: Option[Long])
</code></pre>

play中，所有的模型处理都是在model中完成的，所以数据库操作写在了他们对应的伴生对象中：


<pre class="prettyprint"><code>
case class Company(id: Pk[Long] = NotAssigned, name: String)
case class Computer(id: Pk[Long] = NotAssigned, name: String, introduced: Option[Date], discontinued: Option[Date], companyId: Option[Long])
</code></pre>

使用Anorm定义映射(OR-Mapping)：

<pre class="prettyprint"><code>
  val simple = {
    get[Pk[Long]]("computer.id") ~
    get[String]("computer.name") ~
    get[Option[Date]]("computer.introduced") ~
    get[Option[Date]]("computer.discontinued") ~
    get[Option[Long]]("computer.company_id") map {
      case id~name~introduced~discontinued~companyId => Computer(id, name, introduced, discontinued, companyId)
    }
  }
  
  val withCompany = Computer.simple ~ (Company.simple ?) map {
    case computer~company => (computer,company)
  }
</code></pre>

前者是简单映射，后者是包含了附加对象的映射：一个computer会有一个或0个公司与之对应

查询：先看简单的单个查询：

<pre class="prettyprint"><code>
  def findById(id: Long): Option[Computer] = {
    DB.withConnection { implicit connection =>
      SQL("select * from computer where id = {id}").on('id -> id).as(Computer.simple.singleOpt)
    }
  }
</code></pre>

再看一个复杂的分页查询，使用了一个分页类Page：

<pre class="prettyprint"><code>
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Computer, Option[Company])] = {
    
    val offest = pageSize * page
    
    DB.withConnection { implicit connection =>
      
      val computers = SQL(
        """
          select * from computer 
          left join company on computer.company_id = company.id
          where computer.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize, 
        'offset -> offest,
        'filter -> filter,
        'orderBy -> orderBy
      ).as(Computer.withCompany *)

      val totalRows = SQL(
        """
          select count(*) from computer 
          left join company on computer.company_id = company.id
          where computer.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(computers, page, offest, totalRows)
      
    }
    
  }
</code></pre>

插入更新删除等就不在赘述了。

##### controller

定义好了数据库操作，我们就可以根据routes设计controller了，这对不同的请求返回不同的数据：



















