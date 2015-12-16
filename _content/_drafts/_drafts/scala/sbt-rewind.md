sbt 0.13.9 getstarted

已装 0.13.7 只修改

hello/project/build.properties

sbt.version=0.13.9

下载不下来jar，可能原因，需要proxy 参见 sbt\_with\_proxy.md

project can contain .scala files, which are combined with .sbt files to form the complete build definition.
project目录可以包含scala文件，project目录下的sbt文件与build.sbt含义不同 http://www.scala-sbt.org/0.13/tutorial/Organizing-Build.html

sbt批量模式
$ sbt clean compile "testOnly TestA TestB"

Continuous build and test  http://www.scala-sbt.org/0.13/docs/Triggered-Execution.html
> ~ compile


常用命令
clean	Deletes all generated files (in the target directory).
compile	Compiles the main sources (in src/main/scala and src/main/java directories).
test	Compiles and runs all tests.
console	Starts the Scala interpreter with a classpath including the compiled sources and all dependencies. To return to sbt, type :quit, Ctrl+D (Unix), or Ctrl+Z (Windows).
run <argument>*	Runs the main class for the project in the same virtual machine as sbt.
package	Creates a jar file containing the files in src/main/resources and the classes compiled from src/main/scala and src/main/java.
help <command>	Displays detailed help for the specified command. If no command is provided, displays brief descriptions of all commands.
reload	Reloads the build definition (build.sbt, project/*.scala, project/*.sbt files). Needed if you change the build definition.

http://www.scala-sbt.org/0.13/docs/Command-Line-Reference.html


build文件三种模式
Multi-project .sbt build definition
Bare .sbt build definition
.scala build definition

重点关注第一种

Each project is associated with an immutable map (set of key-value pairs) describing the project.
一个工程有一堆kv，构建定义定义的是他的转换
Instead, the build definition creates a huge list of objects with type Setting[T] where T is the type of the value in the map. A Setting describes a transformation to the map, such as adding a new key-value pair or appending to an existing value.

To create the map, sbt first sorts the list of settings so that all changes to the same key are made together, and values that depend on other keys are processed after the keys they depend on. Then sbt walks over the sorted list of Settings and applies each one to the map in turn.
sbt首先对setting排序，所以所有对某个key的修改就放在一起，然后对key的依赖进行计算

Summary: A build definition defines Projects with a list of Setting[T], where a Setting[T] is a transformation affecting sbt’s map of key-value pairs and T is the type of each value.

eg

```
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "2.11.4"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "hello"
  )
```

There are three flavors of key:

* SettingKey[T]: a key for a value computed once (the value is computed when loading the project, and kept around).
* TaskKey[T]: a key for a value, called a task, that has to be recomputed each time, potentially with side effects.
* InputKey[T]: a key for a task that has command line arguments as input. Check out Input Tasks for more details.
http://www.scala-sbt.org/0.13/docs/Input-Tasks.html

内建key
http://www.scala-sbt.org/0.13/sxr/sbt/Keys.scala.html

Custom Keys 

Typically, lazy vals are used instead of vals to avoid initialization order problems.
```
lazy val hello = taskKey[Unit]("An example task")

lazy val root = (project in file(".")).
  settings(
    hello := { println("Hello!") }
  )
```

> From a type-system perspective, the Setting created from a task key is slightly different from the one created from a setting key. taskKey := 42 results in a Setting[Task[T]] while settingKey := 42 results in a Setting[T]. For most purposes this makes no difference; the task key still creates a value of type T when the task executes.

 a setting can’t depend on a task,

```
<task name> // 直接执行，不展示结果
show <task name> // 执行并展示结果
inspect <keyname> // key信息
```


默认import

```
import sbt._
import Process._
import Keys._
```

###Scopes

 there is a single value for a given scoped key.

Scope axes 

* Projects
* Configurations Compile 、Test、Runtime
* Tasks

##### Scoping by task axis 

Settings can affect how a task works. For example, the packageSrc task is affected by the packageOptions setting.

To support this, a task key (such as packageSrc) can be a scope for another key (such as packageOptions).

The various tasks that build a package (packageSrc, packageBin, packageDoc) can share keys related to packaging, such as artifactName and packageOptions. Those keys can have distinct values for each packaging task.

#### 可以定义默认值

Each scope axis can be filled in with an instance of the axis type (for example the task axis can be filled in with a task), or the axis can be filled in with the special value Global.

#### Delegation 如果没有定义，可以回溯（inspect）

A scoped key may be undefined, if it has no value associated with it in its scope.

For each scope, sbt has a fallback search path made up of other scopes. Typically, if a key has no associated value in a more-specific scope, sbt will try to get a value from a more general scope, such as the Global scope or the entire-build scope.

This feature allows you to set a value once in a more general scope, allowing multiple more-specific scopes to inherit the value.


### 
important
http://www.scala-sbt.org/0.13/tutorial/Scopes.html#Referring+to+scoped+keys+when+running+sbt

```
{<build-uri>}<project-id>/config:intask::key
```

    name in (Compile, packageBin) := "hello", // configuration task
    name in Global := "sbthello",

### When to specify a scope
when needed
http://www.scala-sbt.org/0.13/tutorial/Scopes.html#When+to+specify+a+scope


## More kinds of setting 

### Appending to previous values: += and ++= 

sourceDirectories in Compile += file("source")
sourceDirectories in Compile ++= Seq(file("sources1"), file("sources2"))

// file() just creates a new File.

### Computing a value based on other keys’ values 

使用value方法

// name our organization after our project (both are SettingKey[String])
organization := name.value
// name is a Key[String], baseDirectory is a Key[File]
// name the project after the directory it's inside
name := baseDirectory.value.getName
// 使用多个值
name := "project " + name.value + " from " + organization.value + " version " + version.value

### Tasks based on other keys’ values 

You can compute values of some tasks or settings to define or append value for another task. It’s done by using Def.task and taskValue, as argument to :=, += or ++=.

根据另一个task的结果设定，例如增加一个代码生成的任务

```
sourceGenerators in Compile += Def.task {
  myGenerator(baseDirectory.value, (managedClasspath in Compile).value)
}.taskValue
```

### Tasks with dependencies 
task keys create a Setting[Task[T]] rather than a Setting[T] 
Tasks can use settings as inputs, but settings cannot use tasks as inputs.

```
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

## Library dependencies 

### Unmanaged dependencies 

To use custom_lib instead of lib:

```
unmanagedBase := baseDirectory.value / "custom_lib"
```

There’s also an unmanagedJars task which lists the jars from the unmanagedBase directory
如果排除Unmanaged dependencies 可以这样

···
unmanagedJars in Compile := Seq.empty[sbt.Attributed[java.io.File]]
···

### Managed Dependencies

```
val libraryDependencies = settingKey[Seq[ModuleID]]("Declares managed dependencies.")

libraryDependencies += groupID % artifactID % revision
libraryDependencies += groupID % artifactID % revision % configuration
```

libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
或
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test

#### Getting the right Scala version with %% 

If you use groupID %% artifactID % revision rather than groupID % artifactID % revision (the difference is the double %% after the groupID), sbt will add your project’s Scala version to the artifact name. This is just a shortcut. You could write this without the %%:

libraryDependencies += "org.scala-tools" % "scala-stm_2.11.1" % "0.3"

等价于

libraryDependencies += "org.scala-tools" %% "scala-stm" % "0.3" // Assuming the scalaVersion for your build is 2.11.1

%% 不会智能的匹配scala的大版本

The complexity in practice is that often a dependency will work with a slightly different Scala version; but %% is not smart about that. So if the dependency is available for 2.10.1 but you’re using scalaVersion := "2.10.4", you won’t be able to use %% even though the 2.10.1 dependency likely works. If %% stops working, just go see which versions the dependency is really built for, and hardcode the one you think will work (assuming there is one).

more detail
http://www.scala-sbt.org/0.13/docs/Cross-Build.html

### Ivy revisions
Instead of a fixed revision like "1.6.1", you specify "latest.integration", "2.9.+", or "[1.0,)".
https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency.html#revision

### Resolvers

val resolvers = settingKey[Seq[Resolver]]("The user-defined additional resolvers for automatically managed dependencies.")

resolvers += name at location

eg
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// The at method creates a Resolver object from two strings.

sbt can search your local Maven repository if you add it as a repository:

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

or, for convenience:

resolvers += Resolver.mavenLocal

detail
http://www.scala-sbt.org/0.13/docs/Resolvers.html


#### Overriding default resolvers  TODO 

resolvers does not contain the default resolvers; only additional ones added by your build definition.

sbt combines resolvers with some default repositories to form externalResolvers.

Therefore, to change or remove the default resolvers, you would need to override externalResolvers instead of resolvers.





## Multi-project builds 
多项目
There are two types of dependencies: aggregate and classpath.

Aggregation means that running a task on the aggregate project will also run it on the aggregated projects. For example,

```
lazy val root = (project in file(".")).
  aggregate(util, core)

lazy val util = project

lazy val core = project
```

聚集的项目会一起执行

### Classpath dependencies
会定义一个依赖顺序 
To depend on multiple projects, use multiple arguments to dependsOn, like dependsOn(bar, baz).

#### Per-configuration classpath dependencies 
http://www.scala-sbt.org/0.13/tutorial/Multi-Project.html#Per-configuration+classpath+dependencies

dependsOn(bar % "compile->compile")
dependsOn(bar % "test")
dependsOn(bar % "test->test;compile->compile")


### Default root project 

If a project is not defined for the root directory in the build, sbt creates a default one that aggregates all other projects in the build.
略

You cannot have a project subdirectory or project/*.scala files in the sub-projects. foo/project/Build.scala would be ignored.

```
projects 
project <projectname>
subProjectID/compile
```


## Using plugins 

### Declaring a plugin 
hello/project/site.sbt

```
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.0")

site.settings
```

If the build defines multiple projects, instead add it directly to the project:

```
// don't use the site plugin for the `util` project
lazy val util = (project in file("util"))

// enable the site plugin for the `core` project
lazy val core = (project in file("core")).
  settings(site.settings : _*)
```


addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

注意 resolvers += Resolver.sonatypeRepo("public")

If you’re using an auto plugin that requires explicit enablement, then you have to add the following to your build.sbt:

```
lazy val util = (project in file("util")).
  enablePlugins(FooPlugin, BarPlugin).
  disablePlugins(plugins.IvyPlugin).
  settings(
    name := "hello-util"
  )
```

```
plugins 
```

default:

CorePlugin: Provides the core parallelism controls for tasks.
IvyPlugin: Provides the mechanisms to publish/resolve modules.
JvmPlugin: Provides the mechanisms to compile/test/run/package Java/Scala projects.
JUnitXmlReportPlugin provides an experimental support for generating junit-xml.


### Global plugins 

any .sbt or .scala files in ~/.sbt/0.13/plugins/ behave as if they were in the project/ directory for all projects.



## Custom settings and tasks 

### Implementing a task 

```
val sampleStringTask = taskKey[String]("A sample string task.")
val sampleIntTask = taskKey[Int]("A sample int task.")

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT"
)

lazy val library = (project in file("library")).
  settings(commonSettings: _*).
  settings(
    sampleStringTask := System.getProperty("user.home"),
    sampleIntTask := {
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    }
  )
```

sbt has some utility libraries and convenience functions, in particular you can often use the convenient APIs in IO to manipulate files and directories.

http://www.scala-sbt.org/0.13/api/index.html#sbt.IO$

### Execution semantics of tasks 

http://www.scala-sbt.org/0.13/tutorial/Custom-Settings.html#Execution+semantics+of+tasks

Unlike plain Scala method calls, invoking value method on tasks will not be evaluated strictly. Instead, they simply act as placeholders to denote that sampleIntTask depends on startServer and stopServer tasks. 

```
val startServer = taskKey[Unit]("start server")
val stopServer = taskKey[Unit]("stop server")
val sampleIntTask = taskKey[Int]("A sample int task.")
val sampleStringTask = taskKey[String]("A sample string task.")

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT"
)

lazy val library = (project in file("library")).
  settings(commonSettings: _*).
  settings(
    startServer := {
      println("starting...")
      Thread.sleep(500)
    },
    stopServer := {
      println("stopping...")
      Thread.sleep(500)
    },
    sampleIntTask := {
      startServer.value
      val sum = 1 + 2
      println("sum: " + sum)
      stopServer.value // THIS WON'T WORK
      sum
    },
    sampleStringTask := {
      startServer.value
      val s = sampleIntTask.value.toString
      println("s: " + s)
      s
    }
  )
```

When sampleIntTask is invoked by you, sbt’s tasks engine will:

* evaluate the task dependencies before evaluating sampleIntTask (partial ordering)
* try to evaluate task dependencies in parallel if they are independent (parallelization)
* each task dependency will be evaluated once and only once per command execution (deduplication)

If we did not deduplicate the task dependencies, we will end up compiling test source code many times when test task is invoked since compile in Test appears many times as a task dependency of test in Test.


### Cleanup task 
http://www.scala-sbt.org/0.13/tutorial/Custom-Settings.html#Cleanup+task
TODO very confusing

### Use plain Scala 

to solve the confusing
http://www.scala-sbt.org/0.13/tutorial/Custom-Settings.html#Use+plain+Scala
 no deduplication, so you have to be careful about that.

### Turn them into plugins 

If you find you have a lot of custom code, consider moving it to a plugin for re-use across multiple builds.

It’s very easy to create a plugin, as teased earlier and discussed at more length here.

This page has been a quick taste; there’s much much more about custom tasks on the Tasks page.

http://www.scala-sbt.org/0.13/tutorial/Using-Plugins.html
http://www.scala-sbt.org/0.13/docs/Plugins.html



## Organizing the build 

#### sbt is recursive 

### Tracking dependencies in one place 

This technique is useful when you have a multi-project build that’s getting large, and you want to make sure that subprojects to have consistent dependencies.

project/Dependencies.scala

import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "2.3.8"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val specs2core = "org.specs2" %% "specs2-core" % "2.4.14"

  // Projects
  val backendDeps =
    Seq(akkaActor, specs2core % Test)
}


build.sbt

import Dependencies._

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.11.4"
)

lazy val backend = (project in file("backend")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= backendDeps
  )


#### When to use .scala files 

The recommended approach is to define most settings in a multi-project build.sbt file, and using project/*.scala files for task implementations or to share values, such as keys. The use of .scala files also depends on how comfortable you or your team are with Scala.

### Defining auto plugins 

For more advanced users, another way of organizing your build is to define one-off auto plugins in project/*.scala. By defining triggered plugins, auto plugins can be used as a convenient way to inject custom tasks and commands across all subprojects.


## Getting Started summary 
http://www.scala-sbt.org/0.13/tutorial/Summary.html


## Appendix: .scala build definition 
http://www.scala-sbt.org/0.13/tutorial/Full-Def.html

In summary:

* In .scala files, you can add settings to Build.settings for sbt to find, and they are automatically build-scoped.
* In .scala files, you can add settings to Project.settings for sbt to find, and they are automatically project-scoped.
* Any Build object you write in a .scala file will have its contents imported and available to .sbt files.
* The settings in .sbt files are appended to the settings in .scala files.
* The settings in .sbt files are project-scoped unless you explicitly specify another scope.


In fact, there are other sources of settings as well. They are appended in this order:

* Settings from Build.settings and Project.settings in your .scala files.
* Your user-global settings; for example in ~/.sbt/0.13/global.sbt you can define settings affecting all your projects.
* Settings injected by plugins, see using plugins coming up next.
* Settings from .sbt files in the project.
* Build definition projects (i.e. projects inside project) have settings from global plugins (~/.sbt/0.13/plugins/) added. Using plugins explains this more.









# Document
http://www.scala-sbt.org/0.13/docs/index.html

# General Information

## Community Plugins 
important!
http://www.scala-sbt.org/0.13/docs/Community-Plugins.html

## Bintray For Plugins 
## Deploying to Sonatype 
TODO 

## Setup Notes 
Terminal encoding 
JVM heap, permgen, and stack sizes 
Boot directory 
HTTP/HTTPS/FTP Proxy 

## Contributing to sbt 
TODO



# Detailed Topics 

## Using sbt 

### Command Line Reference 
命令 和 启动参数 的参考
如+  sbt.repository.config 等

### Console Project 

使用consoleProject  启动

可以直接使用process的库 和 IO库

```
> "tar -zcvf project-src.tar.gz src" !
> "find project -name *.jar" !
> "cat build.sbt" #| "grep version" #> new File("sbt-version") !
> "grep -r null src" #|| "echo null-free" !
> uri("http://databinder.net/dispatch/About").toURL #> file("About.html") !
```

读取配置

> val value = (<key> in <scope>).eval
> IO.delete( (classesDirectory in Compile).eval )
> (scalacOptions in Compile).eval foreach println
> resolvers.eval foreach println

执行任务

> val value = (<key> in <scope>).eval
> fullResolvers.eval foreach println // 重要
> (fullClasspath in Compile).eval.files foreach println
> (fullClasspath in Test).eval.files foreach println

state略


### Cross-building 

关于scala的二进制不兼容处理，如果要使生成多个scala版本的jar，可以这样

crossScalaVersions := Seq("2.8.2", "2.9.2", "2.10.0")

> + package
> + publish

细节略


### Interacting with the Configuration System 
和tutorial中讲的差不多，TODO

inspect console
inspect actual console
inspect actual compile:console::initialCommands

### Triggered Execution 

相关的设置
* watchSources defines the files for a single project that are monitored for changes. By default, a project watches resources and Scala and Java sources.
* watchTransitiveSources then combines the watchSources for the current project and all execution and classpath dependencies (see .scala build definition for details on interProject dependencies).
* pollInterval selects the interval between polling for changes in milliseconds. The default value is 500 ms.

~ ;clean ;test


### Scripts, REPL, and Dependencies 
sbt的脚本功能 似乎和sbt的plugin有重叠
TODO


### Understanding Incremental Recompilation 
非常重要！！！
TODO

To reduce compile times, sbt uses two strategies:

* Reduce the overhead for restarting Scalac
* Reduce the number of recompiled source.

#### sbt heuristics and How to take advantage of sbt heuristics 

sbt tracks source dependencies at the granularity of source files. 

### Implementation of incremental recompilation 
非常重要！！！
TODO

The incremental compiler algorithm needs to: index source files so it knows whether there were API changes that might affect other source files; e.g. it needs to detect changes to method signatures as in the example above track dependencies between source files; once the change to an API is detected the algorithm needs to determine the set of files that might be potentially affected by this change
要看下源代码
TODO




## Configuration 

### Classpaths, sources, and resources 

Classpath-related settings and tasks typically provide a value of type Classpath. This is an alias for Seq[Attributed[File]]. Attributed is a type that associates a heterogeneous map with each classpath entry.

##### Unmanaged vs managed 
Tasks that produce managed files should be inserted as follows:

```
sourceGenerators in Compile +=
    generate( (sourceManaged in Compile).value / "some_directory")
```

In this example, generate is some function of type File => Seq[File] that actually does the work. 


To insert a named task, which is the better approach for plugins:

```
val mySourceGenerator = taskKey[Seq[File]](...)

mySourceGenerator in Compile :=
  generate( (sourceManaged in Compile).value / "some_directory")

sourceGenerators in Compile += (mySourceGenerator in Compile).task
```

For resources, there are similar keys resourceGenerators and resourceManaged.

##### Excluding source files by name 

excludeFilter in unmanagedSources := "butler.scala" 


#### External vs internal 

Classpaths are also divided into internal and external dependencies. The internal dependencies are inter-project dependencies. These effectively put the outputs of one project on the classpath of another project.

External classpaths are the union of the unmanaged and managed classpaths.

keys
http://www.scala-sbt.org/0.13/docs/Classpaths.html#Keys


### Compiler Plugin Support 

autoCompilerPlugins := true
addCompilerPlugin("org.scala-tools.sxr" %% "sxr" % "0.3.0")
// declare the main Scala source directory as the base directory
scalacOptions :=
    scalacOptions.value :+ ("-Psxr:base-directory:" + (scalaSource in Compile).value.getAbsolutePath)


The compile and testCompile actions will use any compiler plugins found in the lib directory or in the plugin configuration.


### Configuring Scala 

#### Automatically managed Scala 
scalaVersion := "2.10.0"

#### Configuring the scala-library dependency 
autoScalaLibrary := false

libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value % "test"

没有看太懂



### Forking 

有时另一个jvm【如代码中有exit或有threads等http://www.scala-sbt.org/0.13/docs/Running-Project-Code.html】

fork in (Test, run) := true

Change working directory 
Forked JVM options 
Java Home 
Configuring input output


### Global Settings 

全局配置  ~/.sbt/0.13/*.sbt

例如修改shell指示符

```
~/.sbt/0.13/global.sbt

shellPrompt := { state =>
  "sbt (%s)> ".format(Project.extract(state).currentProject.id)
}
```

全局plugin

```
 ~/.sbt/0.13/plugins/build.sbt
```


### Java Sources 

javacOptions += "-g:none"
javacOptions ++= Seq("-source", "1.5")
compileOrder := CompileOrder.JavaThenScala
compileOrder in Test := CompileOrder.Mixed

#### Known issues in mixed mode compilation  
略

#### Ignoring the Scala source directories 
纯java工程



### Mapping Files 

package, packageSrc, and packageDoc -> Seq[(File, String)]
copy                                -> Seq[(File, File)]

Util  PathFinder and Path 

http://www.scala-sbt.org/0.13/api/sbt/PathFinder.html
http://www.scala-sbt.org/0.13/api/sbt/Path$.html

A common way of making this sequence is to start with a PathFinder or Seq[File] (which is implicitly convertible to PathFinder) and then call the pair method.

#### Relative to a directory 

```
import Path.relativeTo
val files: Seq[File] = file("/a/b/C.scala") :: Nil
val baseDirectories: Seq[File] = file("/a") :: Nil
val mappings: Seq[(File,String)] = files pair relativeTo(baseDirectories)

val expected = (file("/a/b/C.scala") -> "b/C.scala") :: Nil
assert( mappings == expected )
```

#### Rebase 

```
import Path.rebase
val files: Seq[File] = file("/a/b/C.scala") :: Nil
val baseDirectories: Seq[File] = file("/a") :: Nil
val mappings: Seq[(File,String)] = files pair rebase(baseDirectories, "pre/")

val expected = (file("/a/b/C.scala") -> "pre/b/C.scala" ) :: Nil
assert( mappings == expected )


import Path.rebase
val files: Seq[File] = file("/a/b/C.scala") :: Nil
val baseDirectories: Seq[File] = file("/a") :: Nil
val newBase: File = file("/new/base")
val mappings: Seq[(File,File)] = files pair rebase(baseDirectories, newBase)

val expected = (file("/a/b/C.scala") -> file("/new/base/b/C.scala") ) :: Nil
assert( mappings == expected )
```

#### Flatten 

```
import Path.flat
val files: Seq[File] = file("/a/b/C.scala") :: Nil
val mappings: Seq[(File,String)] = files pair flat

val expected = (file("/a/b/C.scala") -> "C.scala" ) :: Nil
assert( mappings == expected )


import Path.flat
val files: Seq[File] = file("/a/b/C.scala") :: Nil
val newBase: File = file("/new/base")
val mappings: Seq[(File,File)] = files pair flat(newBase)

val expected = (file("/a/b/C.scala") -> file("/new/base/C.scala") ) :: Nil
assert( mappings == expected )
```

#### Alternatives 
fallback

```
import Path.relativeTo
val files: Seq[File] = file("/a/b/C.scala") :: file("/zzz/D.scala") :: Nil
val baseDirectories: Seq[File] = file("/a") :: Nil
val mappings: Seq[(File,String)] = files pair ( relativeTo(baseDirectories) | flat )

val expected = (file("/a/b/C.scala") -> "b/C.scala") ) :: (file("/zzz/D.scala") -> "D.scala") ) :: Nil
assert( mappings == expected )
```


### Local Scala 
useless


### Macro Projects 
TODO

https://github.com/retronym/macrocosm



### Paths 

This page describes files, sequences of files, and file filters. The base type used is java.io.File, but several methods are augmented through implicits:

* RichFile adds methods to File
* PathFinder adds methods to File and Seq[File]
* Path and IO provide general methods related to files and I/O.

#### Constructing a File 

```
val source: File = file("/home/user/code/A.scala")
def readme(base: File): File = base / "README"

// Relative files should only be used when defining the base directory of a Project, where they will be resolved properly.
val root = Project("root", file(".")) 
unmanagedBase := baseDirectory.value /"custom_lib"

// irrespective of the project the setting is defined in
historyPath := Some( (baseDirectory in ThisBuild).value / ".history"),
```

#### Path Finders 

The ** method accepts a java.io.FileFilter and selects all files matching that filter.

```
def scalaSources(base: File): Seq[File] = {
  val finder: PathFinder = (base / "src") ** "*.scala" 
  finder.get
}
```

Selecting files that are immediate children of a subdirectory is done with a single *:

```
def scalaSources(base: File): PathFinder = (base / "src") * "*.scala"
```

```
def multiPath(base: File): PathFinder =
   (base / "src" / "main") +++
   (base / "lib") +++
   (base / "target" / "classes")

def jars(base: File): PathFinder =
   (base / "lib" +++ base / "target") * "*.jar"

// excluding version control directories. 
def sources(base: File) =
   ( (base / "src") ** "*.scala") --- ( (base / "src") ** ".svn" ** "*.scala")
```

```
// selects all directories under "src"
def srcDirs(base: File) = ( (base / "src") ** "*") filter { _.isDirectory }

// selects archives (.zip or .jar) that are selected by 'somePathFinder'
def archivesOnly(base: PathFinder) = base filter ClasspathUtilities.isArchive
```

```
assert( PathFinder.empty.get == Seq[File]() )
```


#### PathFinder to String conversions 

* toString is for debugging. It puts the absolute path of each component on its own line.
* absString gets the absolute paths of each component and separates them by the platform’s path separator.
* getPaths produces a Seq[String] containing the absolute paths of each component



##### Mappings 

The packaging and file copying methods in sbt expect values of type Seq[(File,String)] and Seq[(File,File)], respectively. These are mappings from the input file to its (String) path in the jar or its (File) destination. This approach replaces the relative path approach (using the ## method) from earlier versions of sbt.

Mappings are discussed in detail on the Mapping-Files page.

#### File Filters 

The argument to * and ** is of type java.io.FileFilter. sbt provides combinators for constructing FileFilters.

```
def testSrcs(base: File): PathFinder =  (base / "src") * "*Test*.scala"
def sources(base: File): PathFinder  =  (base / "src") ** ("*.scala" || "*.java")
def imageResources(base: File): PathFinder =
   (base/"src"/"main"/"resources") * ("*.png" -- "logo.png")
```


























