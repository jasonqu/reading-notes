https://github.com/davidflanagan/javascript6_examples

## 2

## 3 数据类型和值

https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/A_re-introduction_to_JavaScript
先从任何编程语言都不可缺少的组成部分——“类型”开始。JavaScript 程序可以修改值（value），这些值都有各自的类型。JavaScript 中的类型包括：

* Number（数字）
* String（字符串）
* Boolean（布尔）
* Symbol（符号）
* Object（对象）
  * Function（函数）
  * Array（数组）
  * Date（日期）
  * RegExp（正则表达式）/^[1-9][1-9]*/
  * Error（语法或运行时错误）EvalError[eval() don't use it]、RangeError[数字超过合法范围，如数组的length设置为负数]、ReferenceError【读取一个不存在的变量】、SyntaxError[语法错误，eval、Function、RegExp都会抛出]、TypeError[值类型与要求不符：访问值为null、undefined的属性时、对非构造函数使用new时]、URIError[de/encodeURI de/encodeURIComponent 时有不合法的16进制转义序列/Unicode替代对时报错]
* null（空）单值
* undefined（未定义）未声明、未赋值、对象属性不存在

值类型：Number、String、Boolean、Null、Undefined
复合类型：Object

Number：js只有double类型的数字
String：
Boolean：任何对象都可以转为bool：var x_as_boolean = !!x;

Object:json {}
Array: []

3.12自动转换

包装类型，just evil 3.13 3.14

new Boolean(false)
Boolean {[[PrimitiveValue]]: false}
if(new Boolean(false)) console.log("xxx")
VM619:2 xxx
undefined
new Boolean(false) == true
false
new Boolean(false) == false
true
new Boolean(false) === false
false
if(new Boolean(false)) console.log("xxx") else console.log("good")
VM699:2 Uncaught SyntaxError: Unexpected token elseInjectedScript._evaluateOn @ VM108:883InjectedScript._evaluateAndWrap @ VM108:816InjectedScript.evaluate @ VM108:682
if(new Boolean(false)) console.log("xxx") ; else console.log("good");

传值 与 传引用
对象是传引用，其它都是传值，对字符串一般也认为是对象，传不可变的引用


## 4

js中的变量是非类型的untype，即声明的变量可以被赋为任何类型的值

可以重复声明，没有var则声明为global变量【don‘t do it】

4.3 变量作用域

1. 可以重复声明变量

var scope =


4.3.1没有块级作用域

var scope = "global"; // Declare a global variable
function checkscope() {
var scope = "local"; // Declare a local variable with the same name
return scope; // Return the local value, not the global one
}
checkscope() // => "local"
Although you can get away with not using the var statement when you write code in
the global scope, you must always use var to declare local variables. Consider what
happens if you don’t:
scope = "global"; // Declare a global variable, even without var.
function checkscope2() {
scope = "local"; // Oops! We just changed the global variable.
myscope = "local"; // This implicitly declares a new global variable.
return [scope, myscope]; // Return two values.
}
checkscope2() // => ["local", "local"]: has side effects!
scope // => "local": global variable has changed.
myscope // => "local": global namespace cluttered up.
Function definitions can be nested. Each function has its own local scope, so it is possible
to have several nested layers of local scope. For example:
var scope = "global scope"; // A global variable
function checkscope() {
var scope = "local scope"; // A local variable
function nested() {
var scope = "nested scope"; // A nested scope of local variables
return scope; // Return the value in scope here
}
return nested();
}
checkscope() // => "nested scope"


























to be eval
http://jonham.cn/test/PasswordInput/
http://www.gfzj.us/page2/
https://blog.cnbluebox.com/
