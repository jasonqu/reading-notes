---
layout: post
title: play samples 1 - Hello world
tagline: code better
category : play framework
tags : ["play","web framework","scala"]
---

### 介绍

对play提供的scala samples进行学习的一个记录。在看这些例子之前要先过一遍play的文档作为知识基础。文章中的例子来自 play-2.2.3。

### Hello world

一个基础简单实例，展示了怎样编写controller 和 action，routes、template以及form。

#### 功能介绍

首页是一个表单

![表单提交页](/img/playsamples/helloworld-index.jpg "表单提交页")

填写参数提交之后将会转到一个展示页面：

![结果展示页](/img/playsamples/helloworld-hello.jpg "结果展示页")

##### routes

两个url ：index 和 hello

<pre class="prettyprint">
GET     /                           controllers.Application.index
GET     /hello                      controllers.Application.sayHello
GET     /assets/*file               controllers.Assets.at(path="/public", file)
</pre>

##### base view

main.scala.html 接受两个参数 @(title: String)(content: Html)，分别是标题和页面的内容

##### index

首先在Application.scala中定义了一个表单，并对表单参数name、repeat进行了限制，而color则是可选的；然后在index这个action中将这个表单传给了index的template：

<pre class="prettyprint">
  val helloForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "repeat" -> number(min = 1, max = 100),
      "color" -> optional(text)
    )
  )

  def index = Action {
    Ok(html.index(helloForm))
  }
</pre>

index.scala.html 接受一个表单为参数：<code>@(helloForm: Form[(String,Int,Option[String])])</code>

使用了main template，title为固定值"The 'helloworld' application"，构造form的时候使用了helper方法【需要引用 @import helper._】

<pre class="prettyprint">
    &lt;h1>Configure your 'Hello world':&lt;/h1>

    @form(action = routes.Application.sayHello, args = 'id -> "helloform") {
        @inputText(
            field = helloForm("name"),
            args = '_label -> "What's your name?", 'placeholder -> "World"
        )
        @inputText(
            field = helloForm("repeat"),
            args = '_label -> "How many times?", 'size -> 3, 'placeholder -> 10
        )
        @select(
            field = helloForm("color"), 
            options = options(
                "" -> "Default",
                "red" -> "Red",
                "green" -> "Green",
                "blue" -> "Blue"
            ),
            args = '_label -> "Choose a color"
        )

        &lt;p class="buttons">
            &lt;input type="submit" id="submit">
        &lt;p>

    }
</pre>

以name为例，生成的结果是这样的：

<pre class="prettyprint">
	@inputText(
	    field = helloForm("name"),
	    args = '_label -> "What's your name?", 'placeholder -> "World"
	)

	-->

	&lt;dl class=" " id="name_field">
	    &lt;dt>&lt;label for="name">What's your name?&lt;/label>&lt;/dt>
	    &lt;dd>
	    	&lt;input type="text" id="name" name="name" value="" placeholder="World">
		&lt;/dd>
	    &lt;dd class="info">Required&lt;/dd>
	&lt;/dl>
</pre>

这里使用的是scala的默认form构造helper，当然play中可以很方便的定义自己的helper，可以参考这里 : http://www.playframework.com/documentation/2.2.x/ScalaCustomFieldConstructors

最后点击submit 的时候使用了form的参数 action = routes.Application.sayHello, args = 'id -> "helloform"

##### hello

之前介绍了form的校验，注意这里的表单参数检验是服务器端进行的。

<pre class="prettyprint">
  def sayHello = Action { implicit request =>
    helloForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index(formWithErrors)),
      {case (name, repeat, color) => Ok(html.hello(name, repeat.toInt, color))}
    )
  }
</pre>

可以看到，如果参数留空并点击提交的话，会发起一个get请求 <code>http://localhost:9000/hello?name=&amp;repeat=&amp;color=</code>，sayHello处理出错，进入formWithErrors分支，会将包含错误提示的表单内容再交给index展现，此时相应的error信息就会加上，像这样： <code>&lt;dd class="error">This field is required&lt;/dd></code>

如果表单处理成功，则使用hello展现表单的匹配结果——一个三元组 tuple

hello.html 的参数就是一个三元组，没有太多可说的，值得注意的是：
* color使用的是scala代码生成的： <code>style="color: @color.getOrElse("inherited")"</code>
* 有一个指向主页的链接使用了reverse-route "@routes.Application.index" 即 "/"" <code>&lt;a href="@routes.Application.index">Back to the form&lt;/a></code>

