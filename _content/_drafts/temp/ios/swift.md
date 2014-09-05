http://edu.51cto.com/course/course_id-1387.html

http://www.cocoachina.com/special/swift/

http://www.cocoachina.com/newbie/basic/2014/0604/8675.html

值永远不会被隐式转换为其他类型。如果你需要把一个值转换成其他类型，请显式转换。

	let label = "The width is" 
	let width = 94 
	let widthLabel = label + String(width) 

有一种更简单的把值转换成字符串的方法：把值写到括号中，并且在括号之前写一个反斜杠。例如：

let apples = 3 
let oranges = 5 
let appleSummary = "I have \(apples) apples." 
let fruitSummary = "I have \(apples + oranges) pieces of fruit." 

在类型后面加一个问号来标记这个变量的值是可选的。

var optionalString: String? = "Hello" 
optionalString == nil 
  
var optionalName: String? = "John Appleseed" 
var greeting = "Hello!" 
if let name = optionalName { 
    greeting = "Hello, \(name)" 
} 

你可以使用for-in来遍历字典，需要两个变量来表示每个键值对。 

let interestingNumbers = [ 
    "Prime": [2, 3, 5, 7, 11, 13], 
    "Fibonacci": [1, 1, 2, 3, 5, 8], 
    "Square": [1, 4, 9, 16, 25], 
] 
var largest = 0 
for (kind, numbers) in interestingNumbers { 
    for number in numbers { 
        if number > largest { 
            largest = number 
        } 
    } 
} 
largest 

你可以在循环中使用..来表示范围，也可以使用传统的写法，两者是等价的：
var firstForLoop = 0 
for i in 0..3 { 
    firstForLoop += i 
} 
firstForLoop 
  
var secondForLoop = 0 
for var i = 0; i < 3; ++i { 
    secondForLoop += 1 
} 
secondForLoop 
  
使用..创建的范围不包含上界，如果想包含的话需要使用...。


... func class ...

使用func来声明一个函数，使用名字和参数来调用函数。使用->来指定函数返回值。 
func greet(name: String, day: String) -> String { 
    return "Hello \(name), today is \(day)." 
} 
greet("Bob", "Tuesday") 

函数实际上是一种特殊的闭包，你可以使用{}来创建一个匿名闭包。使用in来分割参数并返回类型。
numbers.map({ 
    (number: Int) -> Int in 
    let result = 3 * number 
    return result 
    }) 

处理变量的可选值时，你可以在操作（比如方法、属性和子脚本）之前加?。如果?之前的值是nil，?后面的东西都会被忽略，并且整个表达式返回nil。否则，?之后的东西都会被运行。在这两种情况下，整个表达式的值也是一个可选值。
let optionalSquare: Square? = Square(sideLength: 2.5, name: "optional square") 
let sideLength = optionalSquare?.sideLength 


... enum ...

... 接口和扩展 ...

http://www.cocoachina.com/newbie/basic/2014/0612/8776.html
元组
元组（tuples）把多个值组合成一个复合值。元组内的值可以使任意类型，并不要求是相同类型。

此外，你还可以通过下标来访问元组中的单个元素，下标从零开始：

println("The status code is \(http404Error.0)") 
// 输出 "The status code is 404" 
println("The status message is \(http404Error.1)") 
// 输出 "The status message is Not Found" 

你可以在定义元组的时候给单个元素命名：

let http200Status = (statusCode: 200, description: "OK") 

给元组中的元素命名后，你可以通过名字来获取这些元素的值：

println("The status code is \(http200Status.statusCode)") 
// 输出 "The status code is 200" 
println("The status message is \(http200Status.description)") 
// 输出 "The status message is OK" 

... 可选绑定 nil 隐式解析可选 TODO ...



http://www.cocoachina.com/newbie/basic/2014/0611/8767.html

浮点数求余计算
不同于C和Objective-C, Swift中是可以对浮点数进行求余的.
8 % 2.5 // 等于 0.5 

闭区间运算符 a...b 
半闭区间 a..b

http://www.cocoachina.com/newbie/basic/2014/0606/8704.html

unicode
let wiseWords = "\"Imagination is more important than knowledge\" - Einstein" 
// "Imagination is more important than knowledge" - Einstein 
let dollarSign = "\x24"        // $,  Unicode scalar U+0024 
let blackHeart = "\u2665"      // ♥,  Unicode scalar U+2665 
let sparklingHeart = "\U0001F496"  // ????, Unicode scalar U+1F496 


hasPrefix hasSuffix
uppercaseString lowercaseString 


UTF-8
您可以通过遍历字符串的 utf8 属性来访问它的 UTF-8 表示。其为 UTF8View 类型的属性，UTF8View 是无符号8位 (UInt8) 值的集合，每一个 UIn8 都是一个字符的 UTF-8 表示：
for codeUnit in dogString.utf8 { 
    print("\(codeUnit) ") 
} 
print("\n") 
// 68 111 103 33 240 159 144 182 
上面的例子中，前四个10进制codeUnit值 (68, 111, 103, 33) 代表了字符 D o g 和 ! ，他们的 UTF-8 表示与其ASCII 表示相同。后四个codeUnit值 (240, 159, 144, 182) 是DOG FACE字符的4位UTF-8表示。


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/04_Collection_Types.html

数组
for (index, value) in enumerate(shoppingList) {
    println("Item \(index + 1): \(value)")
}

字典
for (airportCode, airportName) in airports {
    println("\(airportCode): \(airportName)")
}

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/05_Control_Flow.html

Switch
区间匹配（Range Matching）
元组（Tuple）
值绑定（Value Bindings）
Where
贯穿（Fallthrough）

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/06_Functions.html

 严格上来说，虽然没有返回值被定义，sayGoodbye 函数依然返回了值。没有定义返回类型的函数会返回特殊的值，叫 Void。它其实是一个空的元组（tuple），没有任何元素，可以写成()。

函数参数名称（Function Parameter Names）

外部参数名写在局部参数名之前，用空格分隔。

func someFunction(externalParameterName localParameterName: Int) {
    // function body goes here, and can use localParameterName
    // to refer to the argument value for that parameter
}

简写外部参数名（Shorthand External Parameter Names）
如果你需要提供外部参数名，但是局部参数名已经定义好了，那么你不需要写两次参数名。相反，只写一次参数名，并用井号（#）作为前缀就可以了。这告诉 Swift 使用这个参数名作为局部和外部参数名。

默认参数值（Default Parameter Values）

常量参数和变量参数（Constant and Variable Parameters）
注意： 对变量参数所进行的修改在函数调用结束后便消失了，并且对于函数体外是不可见的。变量参数仅仅存在于函数调用的生命周期中。

输入输出参数（In-Out Parameters）

嵌套函数（Nested Functions）
这章中你所见到的所有函数都叫全局函数（global functions），它们定义在全局域中。你也可以把函数定义在别的函数体中，称作嵌套函数（nested functions）。

默认情况下，嵌套函数是对外界不可见的，但是可以被他们封闭函数（enclosing function）来调用。一个封闭函数也可以返回它的某一个嵌套函数，使得这个函数可以在其他域中被使用。


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/07_Closures.html

闭包是自包含的函数代码块，可以在代码中被传递和使用。 Swift 中的闭包与 C 和 Objective-C 中的代码块（blocks）以及其他一些编程语言中的 lambdas 函数比较相似。

闭包可以捕获和存储其所在上下文中任意常量和变量的引用。 这就是所谓的闭合并包裹着这些常量和变量，俗称闭包。Swift 会为您管理在捕获过程中涉及到的所有内存操作。

在函数 章节中介绍的全局和嵌套函数实际上也是特殊的闭包，闭包采取如下三种形式之一：
- 全局函数是一个有名字但不会捕获任何值的闭包
- 嵌套函数是一个有名字并可以捕获其封闭函数域内值的闭包
- 闭包表达式是一个利用轻量级语法所写的可以捕获其上下文中变量或常量值的匿名闭包

Swift 的闭包表达式拥有简洁的风格，并鼓励在常见场景中进行语法优化，主要优化如下：

- 利用上下文推断参数和返回值类型
- 隐式返回单表达式闭包，即单表达式闭包可以省略return关键字
- 参数名称缩写
- 尾随（Trailing）闭包语法


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/08_Enumerations.html

枚举
在 Swift 中，用来定义两种商品条码的枚举是这样子的：

enum Barcode {
  case UPCA(Int, Int, Int)
  case QRCode(String)
}

原始值（Raw Values）
enum ASCIIControlCharacter: Character {
    case Tab = "\t"
    case LineFeed = "\n"
    case CarriageReturn = "\r"
}

fromRaw toRaw

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/09_Classes_and_Structures.html

你也可以使用点语法为属性变量赋值：

someVideoMode.resolution.width = 12880

注意：
与 Objective-C 语言不同的是，Swift 允许直接设置结构体属性的子属性。上面的最后一个例子，就是直接设置了someVideoMode中resolution属性的width这个子属性，以上操作并不需要重新设置resolution属性。

结构体类型的成员逐一构造器(Memberwise Initializers for structure Types)

所有结构体都有一个自动生成的成员逐一构造器，用于初始化新结构体实例中成员的属性。新实例中各个属性的初始值可以通过属性的名称传递到成员逐一构造器之中：

let vga = resolution(width:640, heigth: 480)

结构体和枚举是值类型

等价于 （ === ） 判断引用相等


集合（Collection）类型的赋值和拷贝行为
Swift 中数组（Array）和字典（Dictionary）类型均以结构体的形式实现。然而当数组被赋予一个常量或变量，或被传递给一个函数或方法时，其拷贝行为与字典和其它结构体有些许不同。
重要！

确保数组的唯一性 unshare

判定两个数组是否共用相同元素 ===
if b === c 
if b[0...1] === b[0...1] 

强制复制数组

var names = ["Mohsen", "Hilary", "Justyn", "Amy", "Rich", "Graham", "Vic"]
var copiedNames = names.copy()

注意：
如果你仅需要确保你对数组的引用是唯一引用，请调用unshare方法，而不是copy方法。unshare方法仅会在确有必要时才会创建数组拷贝。copy方法会在任何时候都创建一个新的拷贝，即使引用已经是唯一引用。


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/10_Properties.html

常量和存储属性
@lazy

计算属性 ！

属性观察器
属性观察器监控和响应属性值的变化，每次属性被设置值的时候都会调用属性观察器，甚至新的值和现在的值相同的时候也不例外。

willset didset！
很重要

类型属性
实例的属性属于一个特定类型实例，每次类型实例化后都拥有自己的一套属性值，实例之间的属性相互独立。
也可以为类型本身定义属性，不管类型有多少个实例，这些属性都只有唯一一份。这种属性就是类型属性。

即java的static属性


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/11_Methods.html

方法的局部参数名称和外部参数名称(Local and External Parameter Names for Methods)

设置玩家等级示例！


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/12_Subscripts.html

下标脚本语法
subscript(index: Int) -> Int {
    get {
      // 返回与入参匹配的Int类型的值
    }

    set(newValue) {
      // 执行赋值操作
    }
}

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/13_Inheritance.html

继承 略

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/14_Initialization.html

构造参数
你可以在定义构造器时提供构造参数，为其提供定制化构造所需值的类型和名字。构造器参数的功能和语法跟函数和方法参数相同。

struct Celsius {
    var temperatureInCelsius: Double = 0.0
    init(fromFahrenheit fahrenheit: Double) {
        temperatureInCelsius = (fahrenheit - 32.0) / 1.8
    }
    init(fromKelvin kelvin: Double) {
        temperatureInCelsius = kelvin - 273.15
    }
}
let boilingPointOfWater = Celsius(fromFahrenheit: 212.0)
// boilingPointOfWater.temperatureInCelsius 是 100.0
let freezingPointOfWater = Celsius(fromKelvin: 273.15)
// freezingPointOfWater.temperatureInCelsius 是 0.0”

两段式构造过程
Swift 中类的构造过程包含两个阶段。第一个阶段，每个存储型属性通过引入它们的类的构造器来设置初始值。当每一个存储型属性值被确定后，第二阶段开始，它给每个类一次机会在新实例准备使用之前进一步定制它们的存储型属性。

通过闭包和函数来设置属性的默认值
略

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/15_Deinitialization.html

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/16_Automatic_Reference_Counting.html

解决实例之间的循环强引用
Swift 提供了两种办法用来解决你在使用类的属性时所遇到的循环强引用问题：弱引用（weak reference）和无主引用（unowned reference）。

对于生命周期中会变为nil的实例使用弱引用。相反的，对于初始化赋值后再也不会被赋值为nil的实例，使用无主引用。

弱引用
在实例的生命周期中，如果某些时候引用没有值，那么弱引用可以阻止循环强引用。如果引用总是有值，则可以使用无主引用，在无主引用中有描述。在上面Apartment的例子中，一个公寓的生命周期中，有时是没有“居民”的，因此适合使用弱引用来解决循环强引用。

无主引用
和弱引用类似，无主引用不会牢牢保持住引用的实例。和弱引用不同的是，无主引用是永远有值的。因此，无主引用总是被定义为非可选类型（non-optional type）。你可以在声明属性或者变量时，在前面加上关键字unowned表示这是一个无主引用。

无主引用以及隐式解析可选属性
上面弱引用和无主引用的例子涵盖了两种常用的需要打破循环强引用的场景。
然而，存在着第三种场景，在这种场景中，两个属性都必须有值，并且初始化完成后不能为nil。在这种场景中，需要一个类使用无主属性，而另外一个类使用隐式解析可选属性。


闭包引起的循环强引用
TODO！

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/17_Optional_Chaining.html
可选链（Optional Chaining）是一种可以请求和调用属性、方法及下标脚本的过程，它的可选性体现于请求或调用的目标当前可能为空（nil）。如果可选的目标有值，那么调用就会成功；相反，如果选择的目标为空（nil），则这种调用将返回空（nil）。多次请求或调用可以被链接在一起形成一个链，如果任何一个节点为空（nil）将导致整个链失效。
就像groovy中的?

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/18_Type_Casting.html

用类型检查操作符(is)来检查一个实例是否属于特定子类型。
可以尝试向下转到它的子类型，用类型转换操作符(as)

for item in library {
    if let movie = item as? Movie {
        println("Movie: '\(movie.name)', dir. \(movie.director)")
    } else if let song = item as? Song {
        println("Song: '\(song.name)', by \(song.artist)")
    }
}

Swift为不确定类型提供了两种特殊类型别名：

AnyObject可以代表任何class类型的实例。
Any可以表示任何类型，除了方法类型（function types）。

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/19_Nested_Types.html

枚举类型常被用于实现特定类或结构体的功能。也能够在有多种变量类型的环境中，方便地定义通用类或结构体来使用，为了实现这种功能，Swift允许你定义嵌套类型，可以在枚举类型、类和结构体中定义支持嵌套的类型。


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/20_Extensions.html

扩展就是向一个已有的类、结构体或枚举类型添加新功能（functionality）。这包括在没有权限获取原始源代码的情况下扩展类型的能力（即逆向建模）。扩展和 Objective-C 中的分类（categories）类似。

就像scala中的隐式转换，但是语法似乎更简洁

计算型属性（Computed Properties）
等等

http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/21_Protocols.html

协议(Protocol)用于定义完成某项任务或功能所必须的方法和属性，协议实际上并不提供这些功能或任务的具体实现(Implementation)--而只用来描述这些实现应该是什么样的。类，结构体，枚举通过提供协议所要求的方法，属性的具体实现来采用(adopt)协议。任意能够满足协议要求的类型被称为协议的遵循者。

委托(代理)模式
委托是一种设计模式，它允许类或结构体将一些需要它们负责的功能交由(委托)给其他的类型的实例。

委托模式的实现很简单: 定义协议来封装那些需要被委托的函数和方法， 使其遵循者拥有这些被委托的函数和方法。

委托模式可以用来响应特定的动作或接收外部数据源提供的数据，而无需要知道外部数据源的所属类型

下文是两个基于骰子游戏的协议:

protocol DiceGame {
    var dice: Dice { get }
    func play()
}

protocol DiceGameDelegate {
    func gameDidStart(game: DiceGame)
    func game(game: DiceGame, didStartNewTurnWithDiceRoll diceRoll:Int)
    func gameDidEnd(game: DiceGame)
}

通过扩展为已存在的类型遵循协议时，该类型的所有实例也会随之添加协议中的方法

通过扩展补充协议声明
当一个类型已经实现了协议中的所有要求，却没有声明时，可以通过扩展来补充协议声明:

struct Hamster {
    var name: String
    func asText() -> String {
        return "A hamster named \(name)"
    }
}
extension Hamster: TextRepresentable {}

协议合成
一个协议可由多个协议采用protocol<SomeProtocol， AnotherProtocol>这样的格式进行组合，称为协议合成(protocol composition)。

举个例子：

protocol Named {
    var name: String { get }
}
protocol Aged {
    var age: Int { get }
}
struct Person: Named, Aged {
    var name: String
    var age: Int
}
func wishHappyBirthday(celebrator: protocol<Named, Aged>) {
    println("Happy birthday \(celebrator.name) - you're \(celebrator.age)!")
}
let birthdayPerson = Person(name: "Malcolm", age: 21)
wishHappyBirthday(birthdayPerson)
// 输出 "Happy birthday Malcolm - you're 21!

@objc用来表示协议是可选的，也可以用来表示暴露给Objective-C的代码，此外，@objc型协议只对类有效，因此只能在类中检查协议的一致性。详情查看Using Siwft with Cocoa and Objectivei-c。

https://developer.apple.com/library/prerelease/ios/documentation/Swift/Conceptual/BuildingCocoaApps/index.html#//apple_ref/doc/uid/TP40014216

对可选协议的规定
可选协议含有可选成员，其遵循者可以选择是否实现这些成员。在协议中使用@optional关键字作为前缀来定义可选成员。


http://numbbbbb.gitbooks.io/-the-swift-programming-language-/chapter2/22_Generics.html

类型约束
关联类型(Associated Types) 类型别名

Where 语句
类型约束能够确保类型符合泛型函数或类的定义约束。

func allItemsMatch<
    C1: Container, C2: Container
    where C1.ItemType == C2.ItemType, C1.ItemType: Equatable>
    (someContainer: C1, anotherContainer: C2) -> Bool 























