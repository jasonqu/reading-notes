---
layout: post
title: html escape in java
tagline: code better
tags : ["html escape","web","archive"]
---

#### 问题描述

将"&", "\"", "<", ">"转为"&amp;", "&quot;", "&lt;", "&gt;"，目的是显示html代码或者防止攻击。

#### 解决方法

http://stackoverflow.com/questions/4874626/java-escape-html

如果使用commons lang：

<pre>
org.apache.commons.lang.StringEscapeUtils escapeHtml()
或
StringUtils.replaceEach(str, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"})
</pre>

如果是 Android, use TextUtils.htmlEncode(String) instead.

如果使用Guava

<pre>
HtmlEscapers.htmlEscaper().escape(income);
</pre>


in Scala, just use method xml.Utility.escape

核心处理逻辑：

<pre>
      text.charAt(pos) match {
        case '<' => s.append("&lt;")
        case '>' => s.append("&gt;")
        case '&' => s.append("&amp;")
        case '"' => s.append("&quot;")
        case '\n' => s.append('\n')
        case '\r' => s.append('\r')
        case '\t' => s.append('\t')
        case c => if (c >= ' ') s.append(c)
      }
</pre>






