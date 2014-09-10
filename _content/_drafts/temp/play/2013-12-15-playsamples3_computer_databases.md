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

###### list

定义好了数据库操作，我们就可以根据routes设计controller了，这对不同的请求返回不同的数据：

列表很简单，首页不过是没有限制参数的列表页：

<pre class="prettyprint"><code>
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Computer.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  val Home = Redirect(routes.Application.list(0, 2, ""))
  def index = Action { Home }
</code></pre>

列表页展示的效果是这样的：

![列表页](/img/playsamples/computer-database-index.png "列表页")

<pre class="prettyprint"><code>
@(currentPage: Page[(Computer, Option[Company])], currentOrderBy: Int, currentFilter: String)(implicit flash: play.api.mvc.Flash)
</code></pre>

列表页接受的参数是：
* currentPage: 分页的数据
* currentOrderBy: 当前排序序号
* currentFilter: 当前过滤词
* flash: flash 参数，展示成功或失败信息

之所以对flash用implicit，是因为调用方可以选择不传这个参数进来，那样将会使用原来方法作用域中定义的一个implicit修饰的类型相同的对象，例如list。当然也可以选择传入这个参数，例如更新或删除完成后就可以加上这个参数【参考http://www.scala-lang.org/old/node/114】：

<pre class="prettyprint"><code>
  def delete(id: Long) = Action {
    Computer.delete(id)
    Home.flashing("success" -> "Computer has been deleted")
  }
</code></pre>

在实际的展示html代码中：

首先是一个影藏的提示框，在有flash操作提示的时候才显示：

<pre class="prettyprint"><code>
    @flash.get("success").map { message =>
        <div class="alert-message warning">
            <strong>Done!</strong> @message
        </div>
    }
</code></pre>

然后是搜索框，这里有指向两个操作的反向routing的链接：

<pre class="prettyprint"><code>
    <div id="actions">
        @helper.form(action=routes.Application.list()) {
            <input type="search" id="searchbox" name="f" value="@currentFilter" placeholder="Filter by computer name...">
            <input type="submit" id="searchsubmit" value="Filter by name" class="btn primary">
        }
        <a class="btn success" id="add" href="@routes.Application.create()">Add a new computer</a>
    </div>
</code></pre>

下面就到数据表了

首先检查有没有数据：

<pre class="prettyprint"><code>
    @Option(currentPage.items).filterNot(_.isEmpty).map { computers =>
</code></pre>

页面的表头需要提供排序等功能，是通过在表头上添加链接实现的：

<pre class="prettyprint"><code>
            <thead>
                <tr>
                    @header(2, "Computer name")
                    @header(3, "Introduced")
                    @header(4, "Discontinued")
                    @header(5, "Company")
                </tr>
            </thead>

扇面的代码调用了这样的方法：

@link(newPage: Int, newOrderBy: Option[Int] = None) = @{
    routes.Application.list(newPage, newOrderBy.map { orderBy =>
        if(orderBy == scala.math.abs(currentOrderBy)) -currentOrderBy else orderBy
    }.getOrElse(currentOrderBy), currentFilter)
    
}

@**********************************
* Helper generating table headers *
***********************************@
@header(orderBy: Int, title: String) = {
    <th class="col@orderBy header @if(scala.math.abs(currentOrderBy) == orderBy) @{if(currentOrderBy < 0) "headerSortDown" else "headerSortUp"}">
        <a href="@link(0, Some(orderBy))">@title</a>
    </th>
}
</code></pre>

然后就是表体填充数据了，这里就不再赘述了

最后是增加分页标签，代码内容比较长，但还是很好理解的：

<pre class="prettyprint"><code>
        <div id="pagination" class="pagination">
            <ul>
                @currentPage.prev.map { page =>
                    <li class="prev">
                        <a href="@link(page)">&larr; Previous</a>
                    </li> 
                }.getOrElse {
                    <li class="prev disabled">
                        <a>&larr; Previous</a>
                    </li>
                }
                <li class="current">
                    <a>Displaying @(currentPage.offset + 1) to @(currentPage.offset + computers.size) of @currentPage.total</a>
                </li>
                @currentPage.next.map { page =>
                    <li class="next">
                        <a href="@link(page)">Next &rarr;</a>
                    </li> 
                }.getOrElse {
                    <li class="next disabled">
                        <a>Next &rarr;</a>
                    </li>
                }
            </ul>
        </div>
</code></pre>



###### CRUD

对Computer实体的操作首先肯定是涉及到一个表单的定义：

<pre class="prettyprint"><code>
  val computerForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
      "introduced" -> optional(date("yyyy-MM-dd")),
      "discontinued" -> optional(date("yyyy-MM-dd")),
      "company" -> optional(longNumber)
    )(Computer.apply)(Computer.unapply)
  )
</code></pre>

然后controller的代码相对是比较直观的：

<pre class="prettyprint"><code>
  def edit(id: Long) = Action {
    Computer.findById(id).map { computer =>
      Ok(html.editForm(id, computerForm.fill(computer), Company.options))
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors, Company.options)),
      computer => {
        Computer.update(id, computer)
        Home.flashing("success" -> "Computer %s has been updated".format(computer.name))
      }
    )
  }

  def create = Action {
    Ok(html.createForm(computerForm, Company.options))
  }
  
  def save = Action { implicit request =>
    computerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors, Company.options)),
      computer => {
        Computer.insert(computer)
        Home.flashing("success" -> "Computer %s has been created".format(computer.name))
      }
    )
  }
  
  def delete(id: Long) = Action {
    Computer.delete(id)
    Home.flashing("success" -> "Computer has been deleted")
  }
</code></pre>

在表单页的入口，注意到create用的是html.createForm，edit用的是html.editForm

这两个页面的区别是：

* 首先参数上：createForm是 computerForm和company列表，而editForm增加了Computer的id
* 然后就是保存的目标action不一样：createForm是save，而editForm是update
* 最后就是editForm比createForm增加了一个删除按钮

具体可以参考代码，这里不再赘述了

最后需要提到的是这里用到了一个helper方法 twitterBootstrapInput.scala，用来快速构件表单元素：

<pre class="prettyprint"><code>
@(elements: helper.FieldElements)

@**************************************************
* Generate input according twitter bootsrap rules *
**************************************************@
<div class="clearfix @if(elements.hasErrors) {error}">
    <label for="@elements.id">@elements.label</label>
    <div class="input">
        @elements.input
        <span class="help-inline">@elements.infos.mkString(", ")</span> 
    </div>
</div>

使用的例子:

            @inputText(computerForm("discontinued"), '_label -> "Discontinued date")
            
            @select(
                computerForm("company"), 
                companies, 
                '_label -> "Company", '_default -> "-- Choose a company --",
                '_showConstraints -> false
            )


</code></pre>










