eloquent

ch4

http://eloquentjavascript.net/04_data.html#h_+q4Ck0/tFV

相关度
Correlation is a measure of dependence between variables (“variables” in the statistical sense, not the JavaScript sense). It is usually expressed as a coefficient that ranges from -1 to 1. Zero correlation means the variables are not related, whereas a correlation of one indicates that the two are perfectly related—if you know one, you also know the other. Negative one also means that the variables are perfectly related but that they are opposites—when one is true, the other is false.

For binary (Boolean) variables, the phi coefficient (ϕ) provides a good measure of correlation and is relatively easy to compute. To compute ϕ, we need a table n that contains the number of times the various combinations of the two variables were observed. For example, we could take the event of eating pizza and put that in a table like this:

Eating pizza versus turning into a squirrel
ϕ can be computed using the following formula, where n refers to the table:

ϕ =	
n11n00 - n10n01
√ n1•n0•n•1n•0
The notation n01 indicates the number of measurements where the first variable (squirrelness) is false (0) and the second variable (pizza) is true (1). In this example, n01 is 9.

```
function phi(table) {
  return (table[3] * table[0] - table[2] * table[1]) /
    Math.sqrt((table[2] + table[3]) *
              (table[0] + table[1]) *
              (table[1] + table[3]) *
              (table[0] + table[2]));
}

console.log(phi([76, 9, 4, 1]));


function hasEvent(event, entry) {
  return entry.events.indexOf(event) != -1;
}

function tableFor(event, journal) {
  var table = [0, 0, 0, 0];
  for (var i = 0; i < journal.length; i++) {
    var entry = journal[i], index = 0;
    if (hasEvent(event, entry)) index += 1;
    if (entry.squirrel) index += 2;
    table[index] += 1;
  }
  return table;
}

console.log(tableFor("pizza", JOURNAL));
```

shuju
http://eloquentjavascript.net/code/jacques_journal.js


slice and concat together

```
function remove(array, index) {
  return array.slice(0, index)
    .concat(array.slice(index + 1));
}
console.log(remove(["a", "b", "c", "d", "e"], 2));
// → ["a", "b", "d", "e"]
```

random int

```
console.log(Math.floor(Math.random() * 10));
```






The global object

The global scope, the space in which global variables live, can also be approached as an object in JavaScript. Each global variable is present as a property of this object. In browsers, the global scope object is stored in the window variable.

var myVar = 10;
console.log("myVar" in window);
// → true
console.log(window.myVar);
// → 10


http://eloquentjavascript.net/04_data.html#h_nSTX34CM1M
List

var list = {
  value: 1,
  rest: {
    value: 2,
    rest: {
      value: 3,
      rest: null
    }
  }
};


deep equal


http://eloquentjavascript.net/05_higher_order.html

There are two ways of constructing a software design: One way is to make it so simple that there are obviously no deficiencies, and the other way is to make it so complicated that there are no obvious deficiencies.

C.A.R. Hoare, 1980 ACM Turing Award Lecture


### Abstracting array traversal

```
function forEach(array, action) {
  for (var i = 0; i < array.length; i++)
    action(array[i]);
}

forEach(["Wampeter", "Foma", "Granfalloon"], console.log);
// → Wampeter
// → Foma
// → Granfalloon
```

Higher-order functions

Functions that operate on other functions, either by taking them as arguments or by returning them, are called higher-order functions. 


you can have functions that create new functions.

```
function greaterThan(n) {
  return function(m) { return m > n; };
}
var greaterThan10 = greaterThan(10);
console.log(greaterThan10(11));
// → true
```

And you can have functions that change other functions.

```
function noisy(f) {
  return function(arg) {
    console.log("calling with", arg);
    var val = f(arg);
    console.log("called with", arg, "- got", val);
    return val;
  };
}
noisy(Boolean)(0);
// → calling with 0
// → called with 0 - got false
```

You can even write functions that provide new types of control flow.

```
function unless(test, then) {
  if (!test) then();
}
function repeat(times, body) {
  for (var i = 0; i < times; i++) body(i);
}

repeat(3, function(n) {
  unless(n % 2, function() {
    console.log(n, "is even");
  });
});
// → 0 is even
// → 2 is even
```

The lexical scoping rules that we discussed in Chapter 3 work to our advantage when using functions in this way. In the previous example, the n variable is a parameter to the outer function. Because the inner function lives inside the environment of the outer one, it can use n. The bodies of such inner functions can access the variables around them. They can play a role similar to the {} blocks used in regular loops and conditional statements. An important difference is that variables declared inside inner functions do not end up in the environment of the outer function. And that is usually a good thing.




#### Passing along arguments

上面的noisy只能传入一个参数，此时可以使用apply解决

```
function transparentWrapping(f) {
  return function() {
    return f.apply(null, arguments);
  };
}
```


http://eloquentjavascript.net/05_higher_order.html#h_1BJbwiI0gI
Filtering an array
Transforming with map
Composability

```
function average(array) {
  function plus(a, b) { return a + b; }
  return array.reduce(plus) / array.length;
}
function age(p) { return p.died - p.born; }
function male(p) { return p.sex == "m"; }
function female(p) { return p.sex == "f"; }

console.log(average(ancestry.filter(male).map(age)));
// → 61.67
console.log(average(ancestry.filter(female).map(age)));
// → 54.56
```


In the happy land of elegant code and pretty rainbows, there lives a spoil-sport monster called inefficiency.


http://eloquentjavascript.net/05_higher_order.html#h_fwBD5oTMLl
利用 binding 进行柯里化

http://eloquentjavascript.net/05_higher_order.html#h_TcUD2vzyMe
练习，尤其是
http://eloquentjavascript.net/05_higher_order.html#h_FkNn96IrQe
实现一个groupby


## The Secret Life of Objects

The problem with object-oriented languages is they’ve got all this implicit environment that they carry around with them. You wanted a banana but what you got was a gorilla holding the banana and the entire jungle.

Joe Armstrong, interviewed in Coders at Work


speak.apply(fatRabbit, ["Burp!"]);
// → The fat rabbit says 'Burp!'
speak.call({type: "old"}, "Oh my.");


You can use Object.create to create an object with a specific prototype.

Map

```
for (var name in map) {
  if (map.hasOwnProperty(name)) {
    // ... this is an own property
  }
}
```

or 

```
var map = Object.create(null);
map["pizza"] = 0.069;
console.log("toString" in map);
// → false
console.log("pizza" in map);
// → true
```

Much better! We no longer need the hasOwnProperty kludge because all the properties the object has are its own properties. Now we can safely use for/in loops, no matter what people have been doing to Object.prototype.


Project: Electronic Life
http://eloquentjavascript.net/07_elife.html


Debugging is twice as hard as writing the code in the first place. Therefore, if you write the code as cleverly as possible, you are, by definition, not smart enough to debug it.

Brian Kernighan and P.J. Plauger, The Elements of Programming Style

In short, putting a "use strict" at the top of your program rarely hurts and might help you spot a problem.



### Regular Expressions

Some people, when confronted with a problem, think ‘I know, I’ll use regular expressions.’ Now they have two problems.

Jamie Zawinski

```
console.log(/abc/.test("abcde"));
console.log(/[0-9]/.test("in 1992"));
```


\d	Any digit character
\w	An alphanumeric character (“word character”)
\s	Any whitespace character (space, tab, newline, and similar)
\D	A character that is not a digit
\W	A nonalphanumeric character
\S	A nonwhitespace character
.	Any character except for newline

To invert a set of characters—that is, to express that you want to match any character except the ones in the set—you can write a caret (^) character after the opening bracket.

```
var notBinary = /[^01]/;
console.log(notBinary.test("1100100010100110"));
// → false
console.log(notBinary.test("1100100010200110"));
// → true
```

+
*
?

大括号

To indicate that a pattern should occur a precise number of times, use curly braces. Putting {4} after an element, for example, requires it to occur exactly four times. It is also possible to specify a range this way: {2,4} means the element must occur at least twice and at most four times.

You can also specify open-ended ranges when using curly braces by omitting the number on either side of the comma. So {,5} means zero to five times, and {5,} means five or more times.

```
var dateTime = /\d\d-\d\d-\d\d\d\d \d\d:\d\d/;
console.log(dateTime.test("30-01-2003 15:20"));
// → true
console.log(dateTime.test("30-jan-2003 15:20"));
// → false

var dateTime = /\d{1,2}-\d{1,2}-\d{4} \d{1,2}:\d{2}/;
console.log(dateTime.test("30-1-2003 8:45"));
// → true
```

```
var cartoonCrying = /boo+(hoo+)+/i;
console.log(cartoonCrying.test("Boohoooohoohooo"));
// → true
```

The i at the end of the expression in the previous example makes this regular expression case insensitive, allowing it to match the uppercase B in the input string, even though the pattern is itself all lowercase.


#### Matches and groups

The test method is the absolute simplest way to match a regular expression. It tells you only whether it matched and nothing else. Regular expressions also have an exec (execute) method that will return null if no match was found and return an object with information about the match otherwise.

```
var match = /\d+/.exec("one two 100");
console.log(match);
// → ["100"]
console.log(match.index);
// → 8
```

String values have a match method that behaves similarly.

```
console.log("one two 100".match(/\d+/));
// → ["100"]
```

When the regular expression contains subexpressions grouped with parentheses, the text that matched those groups will also show up in the array. The whole match is always the first element. The next element is the part matched by the first group (the one whose opening parenthesis comes first in the expression), then the second group, and so on.

```
var quotedText = /'([^']*)'/;
console.log(quotedText.exec("she said 'hello'"));
// → ["'hello'", "hello"]
```

When a group does not end up being matched at all (for example, when followed by a question mark), its position in the output array will hold undefined. Similarly, when a group is matched multiple times, only the last match ends up in the array.

```
console.log(/bad(ly)?/.exec("bad"));
// → ["bad", undefined]
console.log(/(\d)+/.exec("123"));
// → ["123", "3"]
```

http://eloquentjavascript.net/09_regexp.html#h_AzxCBCKdvY
#### The mechanics of matching

regex graph

#### Backtracking

#### The replace method

```
console.log("Borobudur".replace(/[ou]/, "a"));
// → Barobudur
console.log("Borobudur".replace(/[ou]/g, "a"));
// → Barabadar
```

When a g option (for global) is added to the regular expression, all matches in the string will be replaced, not just the first.

or by providing a different method, replaceAll.

The real power of using regular expressions with replace comes from the fact that we can refer back to matched groups in the replacement string. 

```
console.log(
  "Hopper, Grace\nMcCarthy, John\nRitchie, Dennis"
    .replace(/([\w ]+), ([\w ]+)/g, "$2 $1"));
// → Grace Hopper
//   John McCarthy
//   Dennis Ritchie
```

It is also possible to pass a function, rather than a string, as the second argument to replace. For each replacement, the function will be called with the matched groups (as well as the whole match) as arguments, and its return value will be inserted into the new string.

```
var s = "the cia and fbi";
console.log(s.replace(/\b(fbi|cia)\b/g, function(str) {
  return str.toUpperCase();
}));
// → the CIA and FBI
```

```
var stock = "1 lemon, 2 cabbages, and 101 eggs";
function minusOne(match, amount, unit) {
  amount = Number(amount) - 1;
  if (amount == 1) // only one left, remove the 's'
    unit = unit.slice(0, unit.length - 1);
  else if (amount == 0)
    amount = "no";
  return amount + " " + unit;
}
console.log(stock.replace(/(\d+) (\w+)/g, minusOne));
// → no lemon, 1 cabbage, and 100 eggs
```


#### Greed

```
function stripComments(code) {
  return code.replace(/\/\/.*|\/\*[^]*\*\//g, "");
}
console.log(stripComments("1 + /* 2 */3"));
// → 1 + 3
console.log(stripComments("x = 10;// ten!"));
// → x = 10;
console.log(stripComments("1 /* a */+/* b */ 1"));
// → 1  1
```

Because of this behavior, we say the repetition operators (+, *, ?, and {}) are greedy, meaning they match as much as they can and backtrack from there. If you put a question mark after them (+?, *?, ??, {}?), they become nongreedy and start by matching as little as possible, matching more only when the remaining pattern does not fit the smaller match.

A lot of bugs in regular expression programs can be traced to unintentionally using a greedy operator where a nongreedy one would work better. When using a repetition operator, consider the nongreedy variant first.

#### Dynamically creating RegExp objects
http://eloquentjavascript.net/09_regexp.html#h_Rhu25fogrG

To work around this, we can add backslashes before any character that we don’t trust. Adding backslashes before alphabetic characters is a bad idea because things like \b and \n have a special meaning. But escaping everything that’s not alphanumeric or whitespace is safe.

```
var name = "dea+hl[]rd";
var text = "This dea+hl[]rd guy is super annoying.";
var escaped = name.replace(/[^\w\s]/g, "\\$&");
var regexp = new RegExp("\\b(" + escaped + ")\\b", "gi");
console.log(text.replace(regexp, "_$1_"));
// → This _dea+hl[]rd_ guy is super annoying.
```

#### The search method

```
console.log("  word".search(/\S/));
// → 2
console.log("    ".search(/\S/));
// → -1
```

#### The lastIndex property

When using a global regular expression value for multiple exec calls, these automatic updates to the lastIndex property can cause problems. Your regular expression might be accidentally starting at an index that was left over from a previous call.

```
var digit = /\d/g;
console.log(digit.exec("here it is: 1"));
// → ["1"]
console.log(digit.exec("and now: 1"));
// → null
```

Another interesting effect of the global option is that it changes the way the match method on strings works. When called with a global expression, instead of returning an array similar to that returned by exec, match will find all matches of the pattern in the string and return an array containing the matched strings.

```
console.log("Banana".match(/an/g));
// → ["an", "an"]
```

```
var input = "A string with 3 numbers in it... 42 and 88.";
var number = /\b(\d+)\b/g;
var match;
while (match = number.exec(input))
  console.log("Found", match[1], "at", match.index);
// → Found 3 at 14
//   Found 42 at 33
//   Found 88 at 40
```



/abc/	A sequence of characters
/[abc]/	Any character from a set of characters
/[^abc]/	Any character not in a set of characters
/[0-9]/	Any character in a range of characters
/x+/	One or more occurrences of the pattern x
/x+?/	One or more occurrences, nongreedy
/x*/	Zero or more occurrences
/x?/	Zero or one occurrence
/x{2,4}/	Between two and four occurrences
/(abc)/	A group
/a|b|c/	Any one of several patterns
/\d/	Any digit character
/\w/	An alphanumeric character (“word character”)
/\s/	Any whitespace character
/./	Any character except newlines
/\b/	A word boundary
/^/	Start of input
/$/	End of input


exercises
http://eloquentjavascript.net/09_regexp.html#h_TcUD2vzyMe





## Chapter 10 Modules

Modules divide programs into clusters of code that, by some criterion, belong together. This chapter explores some of the benefits that such division provides and shows techniques for building modules in JavaScript.

There are a number of reasons why authors divide their books into chapters and sections. These divisions make it easier for a reader to see how the book is built up and to find specific parts that they are interested in. They also help the author by providing a clear focus for every section.


### Namespacing

Most modern programming languages have a scope level between global (everyone can see it) and local (only this function can see it). JavaScript does not. Thus, by default, everything that needs to be visible outside of the scope of a top-level function is visible everywhere.

reuse
Decoupling

### Using functions as namespaces

Functions are the only things in JavaScript that create a new scope. So if we want our modules to have their own scope, we will have to base them on functions.

```
(function() {
  function square(x) { return x * x; }
  var hundred = 100;

  console.log(square(hundred));
})();
// → 10000
```

Why did we wrap the namespace function in a pair of parentheses? This has to do with a quirk in JavaScript’s syntax. If an expression starts with the keyword function, it is a function expression. However, if a statement starts with function, it is a function declaration, which requires a name and, not being an expression, cannot be called by writing parentheses after it. You can think of the extra wrapping parentheses as a trick to force the function to be interpreted as an expression.

### Objects as interfaces

```
(function(exports) {
  var names = ["Sunday", "Monday", "Tuesday", "Wednesday",
               "Thursday", "Friday", "Saturday"];

  exports.name = function(number) {
    return names[number];
  };
  exports.number = function(name) {
    return names.indexOf(name);
  };
})(this.weekDay = {});

console.log(weekDay.name(weekDay.number("Saturday")));
// → Saturday
```

### Detaching from the global scope

The previous pattern is commonly used by JavaScript modules intended for the browser. The module will claim a single global variable and wrap its code in a function in order to have its own private namespace. But this pattern still causes problems if multiple modules happen to claim the same name or if you want to load two versions of a module alongside each other.

With a little plumbing, we can create a system that allows one module to directly ask for the interface object of another module, without going through the global scope. Our goal is a require function that, when given a module name, will load that module’s file (from disk or the Web, depending on the platform we are running on) and return the appropriate interface value.

This approach solves the problems mentioned previously and has the added benefit of making your program’s dependencies explicit, making it harder to accidentally make use of some module without stating that you need it.

For require we need two things. First, we want a function readFile, which returns the content of a given file as a string. (A single such function is not present in standard JavaScript, but different JavaScript environments, such as the browser and Node.js, provide their own ways of accessing files. For now, let’s just pretend we have this function.) Second, we need to be able to actually execute this string as JavaScript code.

### Evaluating data as code

bad

```
function evalAndReturnX(code) {
  eval(code);
  return x;
}

console.log(evalAndReturnX("var x = 2"));
// → 2
```

USE FUNCTION 
```
var plusOne = new Function("n", "return n + 1;");
console.log(plusOne(4));
// → 5
```

### Require

The following is a minimal implementation of require:

```
function require(name) {
  var code = new Function("exports", readFile(name));
  var exports = {};
  code(exports);
  return exports;
}

console.log(require("weekDay").name(1));
// → Monday
``` 

module code

```
var names = ["Sunday", "Monday", "Tuesday", "Wednesday",
             "Thursday", "Friday", "Saturday"];

exports.name = function(number) {
  return names[number];
};
exports.number = function(name) {
  return names.indexOf(name);
};
```

using code

```
var weekDay = require("weekDay");
var today = require("today");

console.log(weekDay.name(today.dayNumber()));
```

The simplistic implementation of require given previously has several problems. For one, it will load and run a module every time it is required, so if several modules have the same dependency or a require call is put inside a function that will be called multiple times, time and energy will be wasted.

This can be solved by storing the modules that have already been loaded in an object and simply returning the existing value when one is loaded multiple times.

The second problem is that it is not possible for a module to directly export a value other than the exports object, such as a function. For example, a module might want to export only the constructor of the object type it defines. Right now, it cannot do that because require always uses the exports object it creates as the exported value.

The traditional solution for this is to provide modules with another variable, module, which is an object that has a property exports. This property initially points at the empty object created by require but can be overwritten with another value in order to export something else.

```
function require(name) {
  if (name in require.cache)
    return require.cache[name];

  var code = new Function("exports, module", readFile(name));
  var exports = {}, module = {exports: exports};
  code(exports, module);

  require.cache[name] = module.exports;
  return module.exports;
}
require.cache = Object.create(null);
```

We now have a module system that uses a single global variable (require) to allow modules to find and use each other without going through the global scope.

This style of module system is called CommonJS modules, after the pseudo-standard that first specified it. It is built into the Node.js system. Real implementations do a lot more than the example I showed. Most importantly, they have a much more intelligent way of going from a module name to an actual piece of code, allowing both pathnames relative to the current file and module names that point directly to locally installed modules.


### Slow-loading modules

Though it is possible to use the CommonJS module style when writing JavaScript for the browser, it is somewhat involved. The reason for this is that reading a file (module) from the Web is a lot slower than reading it from the hard disk.

One way to work around this problem is to run a program like Browserify on your code before you serve it on a web page. This will look for calls to require, resolve all dependencies, and gather the needed code into a single big file. The website itself can simply load this file to get all the modules it needs.
http://browserify.org/


Another solution is to wrap the code that makes up your module in a function so that the module loader can first load its dependencies in the background and then call the function, initializing the module, when the dependencies have been loaded. That is what the Asynchronous Module Definition (AMD) module system does.

Our trivial program with dependencies would look like this in AMD:

```
define(["weekDay", "today"], function(weekDay, today) {
  console.log(weekDay.name(today.dayNumber()));
});
```

The define function is central to this approach. It takes first an array of module names and then a function that takes one argument for each dependency. It will load the dependencies (if they haven’t already been loaded) in the background, allowing the page to continue working while the files are being fetched. Once all dependencies are loaded, define will call the function it was given, with the interfaces of those dependencies as arguments.

The modules that are loaded this way must themselves contain a call to define. The value used as their interface is whatever was returned by the function passed to define. Here is the weekDay module again:

```
define([], function() {
  var names = ["Sunday", "Monday", "Tuesday", "Wednesday",
               "Thursday", "Friday", "Saturday"];
  return {
    name: function(number) { return names[number]; },
    number: function(name) { return names.indexOf(name); }
  };
});
```

To be able to show a minimal implementation of define, we will pretend we have a backgroundReadFile function that takes a filename and a function and calls the function with the content of the file as soon as it has finished loading it. (Chapter 17 will explain how to write that function.)

The getModule function, when given a name, will return such an object and ensure that the module is scheduled to be loaded. It uses a cache object to avoid loading the same module twice.

```
var defineCache = Object.create(null);
var currentMod = null;

function getModule(name) {
  if (name in defineCache)
    return defineCache[name];

  var module = {exports: null,
                loaded: false,
                onLoad: []};
  defineCache[name] = module;
  backgroundReadFile(name, function(code) {
    currentMod = module;
    new Function("", code)();
  });
  return module;
}
```

We assume the loaded file also contains a (single) call to define. The currentMod variable is used to tell this call about the module object that is currently being loaded so that it can update this object when it finishes loading. We will come back to this mechanism in a moment.

The define function itself uses getModule to fetch or create the module objects for the current module’s dependencies. Its task is to schedule the moduleFunction (the function that contains the module’s actual code) to be run whenever those dependencies are loaded. For this purpose, it defines a function whenDepsLoaded that is added to the onLoad array of all dependencies that are not yet loaded. This function immediately returns if there are still unloaded dependencies, so it will do actual work only once, when the last dependency has finished loading. It is also called immediately, from define itself, in case there are no dependencies that need to be loaded.

```
function define(depNames, moduleFunction) {
  var myMod = currentMod;
  var deps = depNames.map(getModule);

  deps.forEach(function(mod) {
    if (!mod.loaded)
      mod.onLoad.push(whenDepsLoaded);
  });

  function whenDepsLoaded() {
    if (!deps.every(function(m) { return m.loaded; }))
      return;

    var args = deps.map(function(m) { return m.exports; });
    var exports = moduleFunction.apply(null, args);
    if (myMod) {
      myMod.exports = exports;
      myMod.loaded = true;
      myMod.onLoad.forEach(function(f) { f(); });
    }
  }
  whenDepsLoaded();
}
```

When all dependencies are available, whenDepsLoaded calls the function that holds the module, giving it the dependencies’ interfaces as arguments.

The first thing define does is store the value that currentMod had when it was called in a variable myMod. Remember that getModule, just before evaluating the code for a module, stored the corresponding module object in currentMod. This allows whenDepsLoaded to store the return value of the module function in that module’s exports property, set the module’s loaded property to true, and call all the functions that are waiting for the module to load.

This code is a lot harder to follow than the require function. Its execution does not follow a simple, predictable path. Instead, multiple operations are set up to happen at some unspecified time in the future, which obscures the way the code executes.

A real AMD implementation is, again, quite a lot more clever about resolving module names to actual URLs and generally more robust than the one shown previously. The RequireJS (requirejs.org) project provides a popular implementation of this style of module loader.

requirejs.org


### Interface design
http://eloquentjavascript.net/10_modules.html#h_4266RdeHYR

Never assume that a painful interface is “just the way it is”. Fix it, or wrap it in a new interface that works better for you.

Predictability
Composability
Layered interfaces












































http://eloquentjavascript.net/14_event.html
drawing point
http://eloquentjavascript.net/14_event.html#p_A7YDC3hfu1

drag bar
http://eloquentjavascript.net/14_event.html#p_Z1h4BQYT0/

stopPropagation
preventDefault

mouse over mouse out的对象不一致
http://eloquentjavascript.net/14_event.html#p_H543iFvHsm

scroll
http://eloquentjavascript.net/14_event.html#h_xGSp7W5DAZ

focus blur

load event
http://eloquentjavascript.net/14_event.html#h_NmV8RP8lpt

Elements such as images and script tags that load an external file also have a "load" event that indicates the files they reference were loaded. Like the focus-related events, loading events do not propagate.

When a page is closed or navigated away from (for example by following a link), a "beforeunload" event fires. The main use of this event is to prevent the user from accidentally losing work by closing a document. Preventing the page from unloading is not, as you might expect, done with the preventDefault method. Instead, it is done by returning a string from the handler. The string will be used in a dialog that asks the user if they want to stay on the page or leave it. This mechanism ensures that a user is able to leave the page, even if it is running a malicious script that would prefer to keep them there forever in order to force them to look at dodgy weight loss ads.


Script execution timeline
http://eloquentjavascript.net/14_event.html#h_cj44kRfk/h


The fact that JavaScript programs do only one thing at a time makes our lives easier. For cases where you really do want to do some time-consuming thing in the background without freezing the page, browsers provide something called web workers. A worker is an isolated JavaScript environment that runs alongside the main program for a document and can communicate with it only by sending and receiving messages.


Debouncing
http://eloquentjavascript.net/14_event.html#h_AOVmaqj10I
通过延时 减少事件数量

Mouse trail
http://eloquentjavascript.net/14_event.html#h_NOgRH0Y9st

tab
http://eloquentjavascript.net/14_event.html#h_Kk1WKx2anJ


game
http://eloquentjavascript.net/15_game.html
http://www.lessmilk.com/games/10/

canvas
略





http
```
console.log(encodeURIComponent("Hello & goodbye"));
// → Hello%20%26%20goodbye
console.log(decodeURIComponent("Hello%20%26%20goodbye"));
// → Hello & goodbye
```

```
var req = new XMLHttpRequest();
req.open("GET", "example/fruit.json", false);
req.send(null);
console.log(JSON.parse(req.responseText));
// → {banana: "yellow", lemon: "yellow", cherry: "red"}
```

HTTP sandboxing

```
Access-Control-Allow-Origin: *
```

why jsonp 不需要？没有安全问题？

#### Abstracting requests

In Chapter 10, in our implementation of the AMD module system, we used a hypothetical function called backgroundReadFile. It took a filename and a function and called that function with the contents of the file when it had finished fetching it. Here’s a simple implementation of that function:

```
function backgroundReadFile(url, callback) {
  var req = new XMLHttpRequest();
  req.open("GET", url, true);
  req.addEventListener("load", function() {
    if (req.status < 400)
      callback(req.responseText);
  });
  req.send(null);
}
```

This simple abstraction makes it easier to use XMLHttpRequest for simple GET requests. If you are writing a program that has to make HTTP requests, it is a good idea to use a helper function so that you don’t end up repeating the ugly XMLHttpRequest pattern all through your code.


Promises
http://eloquentjavascript.net/17_http.html#h_sdRy5CTAP/
lue


#### Security and HTTPS

The secure HTTP protocol, whose URLs start with https://, wraps HTTP traffic in a way that makes it harder to read and tamper with. First, the client verifies that the server is who it claims to be by requiring that server to prove that it has a cryptographic certificate issued by a certificate authority that the browser recognizes. Next, all data going over the connection is encrypted in a way that should prevent eavesdropping and tampering.

Thus, when it works right, HTTPS prevents both the someone impersonating the website you were trying to talk to and the someone snooping on your communication. It is not perfect, and there have been various incidents where HTTPS failed because of forged or stolen certificates and broken software. Still, plain HTTP is trivial to mess with, whereas breaking HTTPS requires the kind of effort that only states or sophisticated criminal organizations can hope to make.



## Form

focus

```
  document.querySelector("input").focus();
  console.log(document.activeElement.tagName);

<input type="text" autofocus>

<input type="text" tabindex=1> <a href=".">(help)</a>
<button onclick="console.log('ok')" tabindex=2>OK</button>
```

```
<form action="example/submit.html">
  Value: <input type="text" name="value">
  <button type="submit">Save</button>
</form>
<script>
  var form = document.querySelector("form");
  form.addEventListener("submit", function(event) {
    console.log("Saving value", form.elements.value.value);
    event.preventDefault();
  });
</script>
```

http://eloquentjavascript.net/18_forms.html#h_tK84z183/8
File fields

File fields were originally designed as a way to upload files from the browser’s machine through a form. In modern browsers, they also provide a way to read such files from JavaScript programs. The field acts as a manner of gatekeeper. The script cannot simply start reading private files from the user’s computer, but if the user selects a file in such a field, the browser interprets that action to mean that the script may read the file.

```
<input type="file">
<script>
  var input = document.querySelector("input");
  input.addEventListener("change", function() {
    if (input.files.length > 0) {
      var file = input.files[0];
      console.log("You chose", file.name);
      if (file.type)
        console.log("It has type", file.type);
    }
  });
</script>
```

The files property of a file field element is an array-like object (again, not a real array) containing the files chosen in the field. It is initially empty. The reason there isn’t simply a file property is that file fields also support a multiple attribute, which makes it possible to select multiple files at the same time.

Objects in the files property have properties such as name (the filename), size (the file’s size in bytes), and type (the media type of the file, such as text/plain or image/jpeg).

What it does not have is a property that contains the content of the file. Getting at that is a little more involved. Since reading a file from disk can take time, the interface will have to be asynchronous to avoid freezing the document. You can think of the FileReader constructor as being similar to XMLHttpRequest but for files.

```
<input type="file" multiple>
<script>
  var input = document.querySelector("input");
  input.addEventListener("change", function() {
    Array.prototype.forEach.call(input.files, function(file) {
      var reader = new FileReader();
      reader.addEventListener("load", function() {
        console.log("File", file.name, "starts with",
                    reader.result.slice(0, 20));
      });
      reader.readAsText(file);
    });
  });
</script>
```

Reading a file is done by creating a FileReader object, registering a "load" event handler for it, and calling its readAsText method, giving it the file we want to read. Once loading finishes, the reader’s result property contains the file’s content.

FileReaders also fire an "error" event when reading the file fails for any reason. The error object itself will end up in the reader’s error property. If you don’t want to remember the details of yet another inconsistent asynchronous interface, you could wrap it in a Promise (see Chapter 17) like this:

```
function readFile(file) {
  return new Promise(function(succeed, fail) {
    var reader = new FileReader();
    reader.addEventListener("load", function() {
      succeed(reader.result);
    });
    reader.addEventListener("error", function() {
      fail(reader.error);
    });
    reader.readAsText(file);
  });
}
```

It is possible to read only part of a file by calling slice on it and passing the result (a so-called blob object) to the file reader.

simple note with local storage
http://eloquentjavascript.net/18_forms.html#p_YJCl32s1ep


A JavaScript workbench
http://eloquentjavascript.net/18_forms.html#h_wTXvIH5Wds

```
  document.querySelector("#button").addEventListener("click", function() {
    var code = document.querySelector("#code").value;
    var outputNode = document.querySelector("#output");
    try {
      var result = new Function(code)();
      outputNode.innerText = String(result);
    } catch (e) {
      outputNode.innerText = "Error: " + e;
    }
  });
```

Autocompletion

Conway’s Game of Life






