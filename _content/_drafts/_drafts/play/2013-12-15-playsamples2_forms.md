---
layout: post
title: play2 samples 2 - Forms
tagline: code better
category : play framework
tags : ["play2","web framework","scala"]
---

### 介绍

对play提供的scala samples进行学习的一个记录。在看这些例子之前要先过一遍play的文档作为知识基础。文章中的例子来自 play-2.2.3。

### Forms

这个示例介绍了怎样编写一个带有验证的表单，并且提供了动态处理重复值的功能。

#### 功能介绍

首页是两个示例的导航

![首页](/img/playsamples/forms-index.png "首页")

“Sign up”一个注册表单页面的例子：

![注册页](/img/playsamples/forms-signup.png "注册页")

“Contacts”是一个联系人管理页的例子，出了表单的演示之外，还增加了动态添加联系人的功能：

![联系人管理页](/img/playsamples/forms-contacts.png "联系人管理页")

##### routes

主要划分为三个模块：首页、注册演示(signup)和联系人管理(contacts)。其中signup和contacts都由get来获取空白表单，post来提交表单，两个例子还都增加了fakedata的get请求来获取填写示例。

注意三个模块使用了不同的controller [Application、SignUp、Contacts]来处理其对应的请求，体现了模块划分的方法。

<pre>
# Home page
GET     /                           controllers.Application.index

# Signup
GET     /signup                     controllers.SignUp.form
GET     /users/fakeuser             controllers.SignUp.editForm
POST    /signup                     controllers.SignUp.submit

# Contact
GET     /contacts                   controllers.Contacts.form
GET     /contacts/fakecontact       controllers.Contacts.editForm
POST    /contacts                   controllers.Contacts.submit
</pre>

###### base view

main.scala.html 接受两个参数 @(title: String)(content: Html)，分别是标题和页面的内容

页面中的链接都采用reverse-route 的方式给出

##### index

是两个示例的简单介绍，不再赘述

##### signup

signup的目标是通过表单提交的数据，组成这样的model类：

<pre class="prettyprint"><code>
case class User(
  username: String, 
  password: String,
  email: String,
  profile: UserProfile
)

case class UserProfile(
  country: String,
  address: Option[String],
  age: Option[Int]
)
</code></pre>

play的表单都是从controller中定义的，注册表单的也是这样：

<pre class="prettyprint"><code>
  val signupForm: Form[User] = Form(
    // 字段mapping
    mapping(
      "username" -> text(minLength = 4),
      "email" -> email,
      // 密码，有更高级的校验
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text
      ).verifying(
        "Passwords don't match", passwords => passwords._1 == passwords._2
      ),
      "profile" -> mapping(
        "country" -> nonEmptyText,
        "address" -> optional(text),
        "age" -> optional(number(min = 18, max = 100))
      )
      // 这里使用了apply/unapply，因为这些就是models.UserProfile的组成
      (UserProfile.apply)(UserProfile.unapply),
      
      "accept" -> checked("You must accept the conditions")
    )
    // 这里的映射和User的签名不同，所以需要定义自己的绑定方法
    {
      // Binding: 忽略第二个password和accept，构建一个User
      (username, email, passwords, profile, _) => User(username, passwords._1, email, profile) 
    } 
    {
      // Unbinding: 利用User数据填充表单
      user => Some(user.username, user.email, (user.password, ""), user.profile, false)
    }.verifying(
      // 可以添加额外的校验，如可以进行数据库校验是否重名等
      "This username is not available",
      user => !Seq("admin", "guest").contains(user.username)
    )
  )
</code></pre>

可以看到，表单有较为复杂的校验，下面看看如何将这个User的mapping传给view进行展示。

view的参数就是这个Form - <code>@(signupForm: Form[User])</code>

挑出一个field username看一下是怎样处理的：

<pre class="prettyprint"><code>
@import helper._
@import helper.twitterBootstrap._

@inputText(
    signupForm("username"), 
    '_label -> "Username", 
    '_help -> "Please choose a valid username.",
    '_error -> signupForm.globalError
)
</code></pre>

这里使用`helper.twitterBootstrap`中的方法构建表单字段，用`username`这个键获取了表单中的数据，提供了输入提示 `_help`，并且使用了`signupForm.globalError`来展示error信息。

初始时展示出来的字段是这样的：

![username](/img/playsamples/forms-input-username.png "username normal")

<pre class="prettyprint"><code>
<div class="clearfix  " id="username_field">
    <label for="username">Username</label>
    <div class="input">
        
    <input type="text" id="username" name="username" value="" >

        <span class="help-inline"></span>
        <span class="help-block">Please choose a valid username.</span> 
    </div>
</div>
</code></pre>

如果我们什么都没有输入，返回的表单中就会加上错误提示：

![username empty](/img/playsamples/forms-input-username-empty.png "username empty")

<pre class="prettyprint"><code>
<div class="clearfix  error" id="username_field">
    <label for="username">Username</label>
    <div class="input">
        
    <input type="text" id="username" name="username" value="" >

        <span class="help-inline">Minimum length is 4</span>
        <span class="help-block">Please choose a valid username.</span> 
    </div>
</div>
</code></pre>

如果出发了重名校验，会有这样的错误提示：

![username repeat](/img/playsamples/forms-input-username-exist.png "username repeat")

<pre class="prettyprint"><code>
<div class="clearfix  error" id="username_field">
    <label for="username">Username</label>
    <div class="input">
        
    <input type="text" id="username" name="username" value="admin" >

        <span class="help-inline">This username is not available</span>
        <span class="help-block">Please choose a valid username.</span> 
    </div>
</div>
</code></pre>

那每个表单项是怎样找到对应的error信息的呢？TODO

这个表单的指向是 `@helper.form(action = routes.SignUp.submit)`，即反向routing，指向的是`POST /signup`:

<pre class="prettyprint"><code>
<form action="/signup" method="POST" >
</code></pre>

最后的表单展示view，就是将Some(user.username, user.email, (user.password, ""), user.profile, false)这个form展示出来：

提交后就是指向一个summary页面

<pre class="prettyprint"><code>
  def submit = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      errors => BadRequest(html.signup.form(errors)),
      user => Ok(html.signup.summary(user))
    )
  }
</code></pre>

示例还展示了将已有数据填写到表单的做法是，这里不再赘述了：

<pre class="prettyprint"><code>
Ok(html.signup.form(signupForm.fill(existingUser)))
</code></pre>


##### contact

contact的目标页也是通过表单提交的数据组成model类，与之前的不同在于，这个model类中有一个列表式的对象：

<pre class="prettyprint"><code>
case class Contact(
  firstname: String,
  lastname: String,
  company: Option[String],
  informations: Seq[ContactInformation]
)

case class ContactInformation(
  label: String,
  email: Option[String],
  phones: List[String]
)
</code></pre>

对应的表单是这样的，注意其中的informations是一个Seq：

<pre class="prettyprint"><code>
  val contactForm: Form[Contact] = Form(
    mapping(
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "company" -> optional(text),
      "informations" -> seq(
        mapping(
          "label" -> nonEmptyText,
          "email" -> optional(email),
          "phones" -> list(
            text verifying pattern("""[0-9.+]+""".r, error="A valid phone number is required")
          ) 
        )(ContactInformation.apply)(ContactInformation.unapply)
      )
    )(Contact.apply)(Contact.unapply)
  )
</code></pre>

这里代码和前面的类似，就不再赘述了。关键是其中有一些动态添加的代码是怎么实现的呢？

![动态添加代码](/img/playsamples/forms-input-contact-dyn.png "动态添加代码")

其实是通过js代码实现的：

<pre class="prettyprint"><code>
    <script type="text/javascript" charset="utf-8">
        
        $('.removeProfile').live('click', function(e) {
            $(this).parents('.profile').remove()
            renumber()
        })
        
        $('.removePhone').live('click', function(e) {
            var phones = $(this).parents('.phones')
            $(this).parents('.phone').remove()
            renumber(phones)
        })
        
        $('.addPhone').live('click', function(e) {
            var phones = $(this).parents('.phones')
            var template = $('.phone_template', phones)
            template.before('<div class="clearfix phone">' + template.html() + '</div>')
            renumber(phones)
        })
        
        $('.addProfile').live('click', function(e) {
            var template = $('.profile_template')
            template.before('<div class="twipsies well profile">' + template.html() + '</div>')
            renumber()
        })
        
        // -- renumber fields
        
        // Rename fields to have a coherent payload like:
        //
        // informations[0].label
        // informations[0].email
        // informations[0].phones[0]
        // informations[0].phones[1]
        // ...
        //
        // This is probably not the easiest way to do it. A jQuery plugin would help.
        
        var renumber = function(phones) {
            $('.profile').each(function(i) {
                $('input', this).each(function() {
                    $(this).attr('name', $(this).attr('name').replace(/informations\[.+?\]/g, 'informations[' + i + ']'))
                })
                $('.phone input', this).each(function(i) {
                    $(this).attr('name', $(this).attr('name').replace(/phones\[.+\]/g, 'phones[' + i + ']'))
                })
            })
        }
        
    </script>
</code></pre>

这里不再赘述了。







