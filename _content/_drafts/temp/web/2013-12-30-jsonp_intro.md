---
layout: post
title: jsonp 学习
tagline: code better
tags : ["jsonp","web"]
---

from : http://www.ibm.com/developerworks/cn/web/wa-aj-jsonp1

### 理解同源策略限制

同源策略阻止从一个域上加载的脚本获取或操作另一个域上的文档属性。也就是说，收到请求的 URL 的域必须与当前 Web 页面的域相同。

克服该限制的一个相对简单的方法是让 Web 页面向它源自的 Web 服务器请求数据，并且让 Web 服务器像代理一样将请求转发给真正的第三方服务器。尽管该技术获得了普遍使用，但它是不可伸缩的。另一种方式是使用框架要素在当前 Web 页面中创建新区域，并且使用 GET 请求获取任何第三方资源。不过，获取资源后，框架中的内容会受到同源策略的限制。

克服该限制更理想方法是在 Web 页面中插入动态脚本元素，该页面源指向其他域中的服务 URL 并且在自身脚本中获取数据。脚本加载时它开始执行。该方法是可行的，因为同源策略不阻止动态脚本插入，并且将脚本看作是从提供 Web 页面的域上加载的。但如果该脚本尝试从另一个域上加载文档，就不会成功。幸运的是，通过添加 JavaScript Object Notation (JSON) 可以改进该技术。

### JSON 和 JSONP

这是一个在web页面上展示数据的例子

<pre>
&lt;script type="text/javascript">
function showPrice(data) {
    alert("Symbol: " + data.symbol + ", Price: " + data.price);
}
&lt;/script>
&lt;script type="text/javascript">showPrice({symbol: 'IBM', price: 91.42});&lt;/script>	
</pre>

上面展示了如何将静态 JSON 数据作为参数调用 JavaScript 函数。不过，通过在函数调用中动态包装 JSON 数据可以用动态数据调用函数，这是一种动态 JavaScript 插入的技术。要查看其效果，将下面一行放入名为 ticker.js 的独立 JavaScript 文件中。

<pre>
showPrice({symbol: 'IBM', price: 91.42});
</pre>

现在改变 Web 页面中的脚本，使其和清单 3 一样。
清单 3. 动态 JavaScript 插入代码
<pre>
&lt;script type="text/javascript">
// This is our function to be called with JSON data
function showPrice(data) {
    alert("Symbol: " + data.symbol + ", Price: " + data.price);
}
var url = “ticker.js”; // URL of the external script
// this shows dynamic script insertion
var script = document.createElement('script');
script.setAttribute('src', url);

// load the script
document.getElementsByTagName('head')[0].appendChild(script); 
&lt;/script>
</pre>








