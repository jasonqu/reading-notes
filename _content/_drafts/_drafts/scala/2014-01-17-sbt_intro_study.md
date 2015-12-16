

### .sbt Build Definition

http://www.scala-sbt.org/0.13.1/docs/Getting-Started/Basic-Def.html

#### .sbt vs. .scala Definition
An sbt build definition can contain files ending in .sbt, located in the base directory, and files ending in .scala, located in the project subdirectory of the base directory.

You can use either one exclusively, or use both. A good approach is to use .sbt files for most purposes, and use .scala files only to contain what can't be done in .sbt:

to customize sbt (add new settings or tasks)
to define nested sub-projects
This page discusses .sbt files. See .scala build definition (later in Getting Started) for more on .scala files and how they relate to .sbt files.

这里介绍的是sbt

#### What is a build definition?
** PLEASE READ THIS SECTION **

After examining a project and processing any build definition files, sbt will end up with an immutable map (set of key-value pairs) describing the build.

For example, one key is name and it maps to a string value, the name of your project.

Build definition files do not affect sbt's map directly.

the build definition creates a huge list of objects with type Setting[T] where T is the type of the value in the map. A Setting describes a transformation to the map, such as adding a new key-value pair or appending to an existing value. (In the spirit of functional programming, a transformation returns a new map, it does not update the old map in-place.)

通过sbt能形成一个工程定义的map，不过sbt并不是直接处理map
sbt定义了一系列Setting[T]对象，T为最终的map中的值类型，Setting泽定义了一个map的转换transformation【修改或者添加各一个键值对，当然即使是更新，也会返回一个新的immutable map】

例如这个定义意味着生效后，新的工程定义中，name对应的值是“hello”

name := "hello"

sbt会排序，确定定义间的依赖关系，并确定相同键的更新顺序，然后sdt会依次应用这些Setting。

#### How build.sbt defines settings

这里有一个简单的例子

name := "hello"

version := "1.0"

scalaVersion := "2.9.2"

左边是key，key的类型可以为SettingKey[T], TaskKey[T], or InputKey[T]，T为期望的值的类型

Keys有一个方法:= 返回Setting[T]，所以可以这样写 name.:=("hello")
这里说明对name使用:=将返回一个Setting[String]，并且最终的值为hello


#### Keys are defined in the Keys object

因为sbt默认包含了
import sbt._
import Process._
import Keys._

所以内置的key定义在这里 http://www.scala-sbt.org/0.13.1/sxr/sbt/Keys.scala.html
val name = SettingKey[String]("name", "Project name.", APlusSetting)
val version = SettingKey[String]("version", "The version/revision of the current module.", APlusSetting)
val scalaVersion = SettingKey[String]("scala-version", "The version of Scala used for building.", APlusSetting)

#### Task Keys

上面介绍的是设置，一次设定后不会改变的。TaskKey每次都会重新计算
* SettingKey[T]: a key with a value computed once (the value is computed one time when loading the project, and kept around).
* TaskKey[T]: a key with a value that has to be recomputed each time, potentially creating side effects.
* InputKey[T]: a task key which has command line arguments as input. The Getting Started Guide doesn't cover InputKey, but when you finish this guide, check out Input Tasks for more.

他们的scala定义在这里可以找到：http://www.scala-sbt.org/0.13.1/sxr/sbt/Structure.scala.html


一个TaskKey定义了一个任务，如compile。他们可以返回Unit或该任务对应的值类型【如package对应的返回就是File】
这里摘录几个原始定义
val clean = TaskKey[Unit]("clean", "Deletes files produced by the build, such as generated sources, compiled classes, and task caches.", APlusTask)
val compile = TaskKey[Analysis]("compile", "Compiles sources.", APlusTask)
val `package` = TaskKey[File]("package", "Produces the main artifact, such as a binary jar.  This is typically an alias for the task that actually does the packaging.", APlusTask)

#### Custom Keys 自定义Key

自定义Key可以根据不同类型有不同的创建方法settingKey, taskKey, and inputKey

例如可以这样在sbt文件中定义一个任务hello

lazy val hello = taskKey[Unit]("An example task")

hello := { println("Hello!") }

定义之后就可以在命令行中运行sbt hello

注：一般建议使用 lazy val而不是val，来避免初始化顺序问题。
另外 Setting不能依赖于一个Task 来生成

可以使用show或inspect来查看task的详细信息

### Scopes

实际上每一个键值对都是带有上下文scope的键值对
There are three scope axes:
* Projects
* Configurations 如Compile Test Runtime
* Tasks
Settings can affect how a task works. For example, the packageSrc task is affected by the packageOptions setting.
To support this, a task key (such as packageSrc) can be a scope for another key (such as packageOptions).
The various tasks that build a package (packageSrc, packageBin, packageDoc) can share keys related to packaging, such as artifactName and packageOptions. Those keys can have distinct values for each packaging task.
val packageOptions = TaskKey[Seq[PackageOption]]("package-options", "Options for packaging.", BTask)

On the command line and in interactive mode, sbt displays (and parses) scoped keys like this:
{<build-uri>}<project-id>/config:intask::key
{file:/home/hp/checkout/hello/}/compile:doc::fullClasspath

If you get an error like "Reference to undefined setting", often you've failed to specify a scope, or you've specified the wrong scope. The key you're using may be defined in some other scope. sbt will try to suggest what you meant as part of the error message; look for "Did you mean compile:compile?"

One way to think of it is that a name is only part of a key. In reality, all keys consist of both a name, and a scope (where the scope has three axes). The entire expression packageOptions in (Compile, packageBin) is a key name, in other words. Simply packageOptions is also a key name, but a different one (for keys with no in, a scope is implicitly assumed: current project, global config, global task).

### More Kinds of Setting

#### Computing a value based on other keys' values

// name is a Key[String], baseDirectory is a Key[File]
// name the project after the directory it's inside
name := baseDirectory.value.getName

Using multiple inputs is similar. For example,
name := "project " + name.value + " from " + organization.value + " version " + version.value

Tasks can use settings as inputs, but settings cannot use tasks as inputs.
例如
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")

// The scalacOptions task may be defined in terms of the checksums setting
scalacOptions := checksums.value

// The checksums setting may not be defined in terms of the scalacOptions task
checksums := scalacOptions.value

### Library Dependencies

#### Managed Dependencies

libraryDependencies += groupID % artifactID % revision
val libraryDependencies = settingKey[Seq[ModuleID]]("Declares managed dependencies.")
例如
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
sbt should download Derby to ~/.ivy2/cache/org.apache.derby/.

resolvers += name at location
val resolvers = settingKey[Seq[Resolver]]("The user-defined additional resolvers for automatically managed dependencies.")

For example:
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

If you want a dependency to show up in the classpath only for the Test configuration and not the Compile configuration, add % "test" like this:
libraryDependencies += groupID % artifactID % revision % configuration
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"


### Multi-Project Builds

#### Multiple projects

It can be useful to keep multiple related projects in a single build, especially if they depend on one another and you tend to modify them together. Each sub-project in a build has its own source directories, generates its own jar file when you run package, and in general works like any other project.

A project is defined by declaring a lazy val of type Project. For example,
http://www.scala-sbt.org/0.13.1/api/sbt/Project.html

lazy val util = project

lazy val core = project in file("core")

#### Dependencies

##### Aggregation

Aggregation means that running a task on the aggregate project will also run it on the aggregated projects. For example,
组合：默认root所有工程都会被运行

lazy val root =
        project.in( file(".") )
   .aggregate(util, core)

可以设定组合时并不update

lazy val root =
        project.in( file(".") )
   .aggregate(util, core)
   .settings(
     aggregate in update := false
   )

#### Classpath dependencies

即一个工程的编译依赖于另一个工程

lazy val core = project.dependsOn(util)
也可以指定configuration
dependsOn(bar % "test->test;compile->compile").

可用命令 projects project 或 subProjectID/compile

#### Common code

共享代码要放在build root的 project目录下

<root>/project/Common.scala

import sbt._
import Keys._

object Common {
  def text = "org.example"
}

<root>/build.sbt

organization := Common.text

### Using Plugins

#### Declaring a plugin

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.0")

使用下面的代码来使设置生效
site.settings
或对多工程
// don't use the site plugin for the `util` project
lazy val util = project

// enable the site plugin for the `core` project
lazy val core = project.settings( site.settings : _*)

Available Plugins
There's a list of available plugins http://www.scala-sbt.org/release/docs/Community/Community-Plugins.html.

### Custom Settings and Tasks

默认的键在这里：http://www.scala-sbt.org/0.13.1/sxr/sbt/Keys.scala.html
默认实现在这里：http://www.scala-sbt.org/0.13.1/sxr/sbt/Defaults.scala.html

#### Implementing a task

下面是实现自己的task的例子

val sampleStringTask = taskKey[String]("A sample string task.")

val sampleIntTask = taskKey[Int]("A sample int task.")

sampleStringTask := System.getProperty("user.home")

sampleIntTask := {
  val sum = 1 + 2
  println("sum: " + sum)
  sum
}

The hardest part about implementing tasks is often not sbt-specific; tasks are just Scala code. The hard part could be writing the "meat" of your task that does whatever you're trying to do. For example, maybe you're trying to format HTML in which case you might want to use an HTML library (you would add a library dependency to your build definition and write code based on the HTML library, perhaps).

sbt has some utility libraries and convenience functions, in particular you can often use the convenient APIs in IO to manipulate files and directories.
http://www.scala-sbt.org/0.13.1/api/index.html#sbt.IO$

If you find you have a lot of custom code, consider moving it to a plugin for re-use across multiple builds.

### .scala Build Definition

build.sbt中的代码最终都会被混入project中的scala文件中
The Scala expressions in build.sbt are compiled alongside and merged with Build.scala (or any other .scala files in the project/ directory).

具体示例见这里 http://www.scala-sbt.org/0.13.1/docs/Getting-Started/Full-Def.html#relating-build-sbt-to-build-scala


## How to...
### Index with Examples
http://www.scala-sbt.org/0.13.1/docs/Howto/Howto.html

### Classpaths
### Customizing paths
### Generating files
### Inspect the build
### Interactive mode
### Configure and use logging
### Project metadata
### Configure packaging
### Running commands
### Configure and use Scala
### Generate Documentation
### Triggered execution










