---
layout: post
title: sbt get started
tagline: code better
tags : ["sbt","scala"]
---


## Getting Started
### Welcome!
### Setup
### Hello, World

#### Create a project directory with source code

In this case, sbt works purely by convention. sbt will find the following automatically:

* Sources in the base directory
* Sources in src/main/scala or src/main/java
* Tests in src/test/scala or src/test/java
* Data files in src/main/resources or src/test/resources
* jars in lib

#### Build definition

in base dir to create the build.sbt:

name := "hello"

version := "1.0"

scalaVersion := "2.10.3"

Other sbt files appear in a project subdirectory. project can contain .scala files, which are combined with .sbt files to form the complete build definition.

fix sbt version in build.properties:

sbt.version=0.13.1

### Directory structure
### Running

#### Batch mode

$ sbt clean compile "testOnly TestA TestB"

#### Continuous build and test

> ~ compile

#### Common commands

clean, compile, test, console, run <argument>*, package, help <command>, reload

#### History Commands

!, !!, !:, !:n, !n, !-n, !string, !?string



### .sbt Build Definition

#### What is a build definition?

After examining a project and processing any build definition files, sbt will end up with an immutable map (set of key-value pairs) describing the build. For example, one key is name and it maps to a string value, the name of your project.

Build definition files do not affect sbt's map directly. Instead, the build definition creates a huge list of objects with type Setting[T] where T is the type of the value in the map. A Setting describes a transformation to the map, such as adding a new key-value pair or appending to an existing value. (In the spirit of functional programming, a transformation returns a new map, it does not update the old map in-place.)

In build.sbt, you might create a Setting[String] for the name of your project like this:

name := "hello"

This Setting[String] transforms the map by adding (or replacing) the name key, giving it the value "hello". The transformed map becomes sbt's new map.

To create its map, sbt first sorts the list of settings so that all changes to the same key are made together, and values that depend on other keys are processed after the keys they depend on. Then sbt walks over the sorted list of Setting and applies each one to the map in turn.

Summary: A build definition defines a list of Setting[T], where a Setting[T] is a transformation affecting sbt's map of key-value pairs and T is the type of each value.

#### How build.sbt defines settings

build.sbt defines a Seq[Setting[_]]; it's a list of Scala expressions, separated by blank lines, where each one becomes one element in the sequence. If you put Seq( in front of the .sbt file and ) at the end and replace the blank lines with commas, you'd be looking at the equivalent .scala code.

A build.sbt file is a list of Setting, separated by blank lines.

#### Key

On the left, name, version, and scalaVersion are keys. A key is an instance of SettingKey[T], TaskKey[T], or InputKey[T] where T is the expected value type.

* SettingKey[T]: a key for a value computed once (the value is computed one time when loading the project, and kept around).
* TaskKey[T]: a key for a value, called a task, that has to be recomputed each time, potentially creating side effects.
* InputKey[T]: a key for a task that has command line arguments as input. The Getting Started Guide doesn't cover InputKey, but when you finish this guide, check out Input Tasks for more

##### Built-in Keys

The built-in keys are just fields in an object called Keys. A build.sbt implicitly has an import sbt.Keys._, so sbt.Keys.name can be referred to as name.

http://www.scala-sbt.org/0.12.2/sxr/Keys.scala.html

##### Custom Keys
Custom keys may be defined with their respective creation methods: settingKey, taskKey, and inputKey. Each method expects the type of the value associated with the key as well as a description. The name of the key is taken from the val the key is assigned to. For example, to define a key for a new task called hello,

lazy val hello = taskKey[Unit]("An example task")

##### Task v. Setting keys

A TaskKey[T] is said to define a task. Tasks are operations such as compile or package. They may return Unit (Unit is Scala for void), or they may return a value related to the task, for example package is a TaskKey[File] and its value is the jar file it creates.

For example, to implement the hello task

hello := { println("Hello!") }

The T vs. Task[T] type difference has this implication: a setting can't depend on a task, because a setting is evaluated only once on project load and is not re-run.

#### Keys in sbt interactive mode

In sbt's interactive mode, you can type the name of any task to execute that task. This is why typing compile runs the compile task. compile is a task key.

If you type the name of a setting key rather than a task key, the value of the setting key will be displayed. Typing a task key name executes the task but doesn't display the resulting value; to see a task's result, use show <task name> rather than plain <task name>. The convention for keys names is to use camelCase so that the command line name and the Scala identifiers are the same.

To learn more about any key, type inspect <keyname> at the sbt interactive prompt. Some of the information inspect displays won't make sense yet, but at the top it shows you the setting's value type and a brief description of the setting.

##### Imports in build.sbt

You can place import statements at the top of build.sbt; they need not be separated by blank lines.

There are some implied default imports, as follows:

import sbt._
import Process._
import Keys._

#### Adding library dependencies

To depend on third-party libraries, there are two options. The first is to drop jars in lib/ (unmanaged dependencies) and the other is to add managed dependencies, which will look like this in build.sbt:

libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"



### Scopes

#### The whole story about keys

Previously we pretended that a key like name corresponded to one entry in sbt's map of key-value pairs. This was a simplification. In truth, each key can have an associated value in more than one context, called a "scope."

Some concrete examples:

* if you have multiple projects in your build definition, a key can have a different value in each project.
* the compile key may have a different value for your main sources and your test sources, if you want to compile them differently.
* the packageOptions key (which contains options for creating jar packages) may have different values when packaging class files (packageBin) or packaging source code (packageSrc).

There is no single value for a given key name, because the value may differ according to scope. However, there is a single value for a given scoped key.

#### Scope axes

A scope axis is a type, where each instance of the type can define its own scope (that is, each instance can have its own unique values for keys). There are three scope axes:

* Projects
* Configurations : Compile、Test、Runtime
* Tasks : various tasks that build a package (packageSrc, packageBin, packageDoc) can share keys related to packaging, such as artifactName and packageOptions.

#### Global scope

Global means what you would expect: the setting's value applies to all instances of that axis. For example if the task axis is Global, then the setting would apply to all tasks.

#### Referring to scoped keys when running sbt

On the command line and in interactive mode, sbt displays (and parses) scoped keys like this:

{<build-uri>}<project-id>/config:intask::key

##### Examples of scoped key notation

* fullClasspath specifies just a key, so the default scopes are used: current project, a key-dependent configuration, and global task scope.
* test:fullClasspath specifies the configuration, so this is fullClasspath in the test configuration, with defaults for the other two scope axes.
* *:fullClasspath specifies Global for the configuration, rather than the default configuration.
* doc::fullClasspath specifies the fullClasspath key scoped to the doc task, with the defaults for the project and configuration axes.
* {file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath specifies a project, {file:/home/hp/checkout/hello/}default-aea33a, where the project is identified with the build {file:/home/hp/checkout/hello/} and then a project id inside that build default-aea33a. Also specifies configuration test, but leaves the default task axis.
* {file:/home/hp/checkout/hello/}/test:fullClasspath sets the project axis to "entire build" where the build is {file:/home/hp/checkout/hello/}
* {.}/test:fullClasspath sets the project axis to "entire build" where the build is {.}. {.} can be written ThisBuild in Scala code.
* {file:/home/hp/checkout/hello/}/compile:doc::fullClasspath sets all three scope axes.

define in scope:

name in (Compile, packageBin) := "hello"

#### When to specify a scope

You need to specify the scope if the key in question is normally scoped. For example, the compile task, by default, is scoped to Compile and Test configurations, and does not exist outside of those scopes.

To change the value associated with the compile key, you need to write compile in Compile or compile in Test. Using plain compile would define a new compile task scoped to the current project, rather than overriding the standard compile tasks which are scoped to a configuration.

If you get an error like "Reference to undefined setting", often you've failed to specify a scope, or you've specified the wrong scope. The key you're using may be defined in some other scope. sbt will try to suggest what you meant as part of the error message; look for "Did you mean compile:compile?"

One way to think of it is that a name is only part of a key. In reality, all keys consist of both a name, and a scope (where the scope has three axes). The entire expression packageOptions in (Compile, packageBin) is a key name, in other words. Simply packageOptions is also a key name, but a different one (for keys with no in, a scope is implicitly assumed: current project, global config, global task).







### More Kinds of Setting

#### Appending to previous values: += and ++=

Assignment with := is the simplest transformation, but keys have other methods as well. If the T in SettingKey[T] is a sequence, i.e. the key's value type is a sequence, you can append to the sequence rather than replacing it.

* += will append a single element to the sequence.
* ++= will concatenate another sequence.

For example

sourceDirectories in Compile += new File("source")

Or, using the file() function from the sbt package for convenience:

sourceDirectories in Compile += file("source")

You could use ++= to add more than one directory at a time:

sourceDirectories in Compile ++= Seq(file("sources1"), file("sources2"))

To replace the default source directories entirely, you use := of course:

sourceDirectories in Compile := Seq(file("sources1"), file("sources2"))

#### Computing a value based on other keys' values

// name our organization after our project (both are SettingKey[String])
organization := name.value

// name is a Key[String], baseDirectory is a Key[File]
// name the project after the directory it's inside
name := baseDirectory.value.getName

name := "project " + name.value + " from " + organization.value + " version " + version.value

##### Settings with dependencies

In the setting name := baseDirectory.value.getName, name will have a dependency on baseDirectory. This is how sbt knows which settings depend on which other settings. Remember that some settings describe tasks, so this approach also creates dependencies between tasks.

For example, if you inspect compile you'll see it depends on another key compileInputs, and if you inspect compileInputs it in turn depends on other keys. Keep following the dependency chains and magic happens. When you type compile sbt automatically performs an update, for example. It Just Works because the values required as inputs to the compile computation require sbt to do the update computation first.

##### Tasks with dependencies

As noted in .sbt build definition, task keys create a Setting[Task[T]] rather than a Setting[T] when you build a setting with :=, etc. Tasks can use settings as inputs, but settings cannot use tasks as inputs.

#### Appending with dependencies: += and ++=

For example, say you have a coverage report named after the project, and you want to add it to the files removed by clean:

cleanFiles += file("coverage-report-" + name.value + ".txt")






### Library Dependencies

#### Managed Dependencies

sbt uses Apache Ivy to implement managed dependencies, so if you're familiar with Maven or Ivy, you won't have much trouble.

Declaring a dependency looks like this, where groupId, artifactId, and revision are strings:

libraryDependencies += groupID % artifactID % revision

or like this, where configuration is also a string:

libraryDependencies += groupID % artifactID % revision % configuration

libraryDependencies is declared in Keys like this:

val libraryDependencies = settingKey[Seq[ModuleID]]("Declares managed dependencies.")

libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"

If you type that in build.sbt and then update, sbt should download Derby to ~/.ivy2/cache/org.apache.derby/. (By the way, update is a dependency of compile so there's no need to manually type update most of the time.)

Of course, you can also use ++= to add a list of dependencies all at once:

libraryDependencies ++= Seq(
    groupID % artifactID % revision,
    groupID % otherID % otherRevision
)

##### Getting the right Scala version with %%

If you use groupID %% artifactID % revision rather than groupID % artifactID % revision (the difference is the double %% after the groupID), sbt will add your project's Scala version to the artifact name. This is just a shortcut. You could write this without the %%:

libraryDependencies += "org.scala-tools" % "scala-stm_2.9.1" % "0.3"

Assuming the scalaVersion for your build is 2.9.1, the following is identical:

libraryDependencies += "org.scala-tools" %% "scala-stm" % "0.3"

##### Ivy revisions

The revision in groupID % artifactID % revision does not have to be a single fixed version. Ivy can select the latest revision of a module according to constraints you specify. Instead of a fixed revision like "1.6.1", you specify "latest.integration", "2.9.+", or "[1.0,)". See the Ivy revisions documentation for details.

##### Resolvers

Not all packages live on the same server; sbt uses the standard Maven2 repository by default. If your dependency isn't on one of the default repositories, you'll have to add a resolver to help Ivy find it.

To add an additional repository, use

resolvers += name at location

For example:

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

The resolvers key is defined in Keys like this:

val resolvers = settingKey[Seq[Resolver]]("The user-defined additional resolvers for automatically managed dependencies.")

The at method creates a Resolver object from two strings.

sbt can search your local Maven repository if you add it as a repository:

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"



### Multi-Project Builds

#### Multiple projects

It can be useful to keep multiple related projects in a single build, especially if they depend on one another and you tend to modify them together.

Each sub-project in a build has its own source directories, generates its own jar file when you run package, and in general works like any other project.

A project is defined by declaring a lazy val of type Project. For example,

lazy val util = project

lazy val core = project

equals to

lazy val util = project.in( file("util") )

lazy val core = project in file("core")


#### Dependencies

Projects in the build can be completely independent of one another, but usually they will be related to one another by some kind of dependency. There are two types of dependencies: aggregate and classpath.

##### Aggregation

Aggregation means that running a task on the aggregate project will also run it on the aggregated projects. For example,

lazy val root =
        project.in( file(".") )
   .aggregate(util, core)

lazy val util = project

lazy val core = project

也可以添加设置，如关闭update

lazy val root =
        project.in( file(".") )
   .aggregate(util, core)
   .settings(
     aggregate in update := false
   )

#### Classpath dependencies

A project may depend on code in another project. This is done by adding a dependsOn method call. For example, if core needed util on its classpath, you would define core as:

lazy val core = project.dependsOn(util)

Now code in core can use classes from util. This also creates an ordering between the projects when compiling them; util must be updated and compiled before core can be compiled.

To depend on multiple projects, use multiple arguments to dependsOn, like dependsOn(bar, baz).

...







### Using Plugins

#### Declaring a plugin

If your project is in directory hello, edit hello/project/plugins.sbt and declare the plugin dependency by passing the plugin's Ivy module ID to addSbtPlugin:

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.0")

Not every plugin is located on one of the default repositories and a plugin's documentation may instruct you to also add the repository where it can be found:

resolvers += ...

#### Adding settings for a plugin¶

...

#### Global plugins

Plugins can be installed for all your projects at once by dropping them in ~/.sbt/0.13/plugins/.

Available Plugins¶
There's a list of available plugins http://www.scala-sbt.org/release/docs/Community/Community-Plugins.html.

Some especially popular plugins are:

* those for IDEs (to import an sbt project into your IDE)
* those supporting web frameworks, such as xsbt-web-plugin.







### Custom Settings and Tasks

#### Implementing a task

Once you've defined a key for your task, you'll need to complete it with a task definition. You could be defining your own task, or you could be planning to redefine an existing task. Either way looks the same; use := to associate some code with the task key:

val sampleStringTask = taskKey[String]("A sample string task.")

val sampleIntTask = taskKey[Int]("A sample int task.")

sampleStringTask := System.getProperty("user.home")

sampleIntTask := {
  val sum = 1 + 2
  println("sum: " + sum)
  sum
}














### .scala Build Definition
### Getting Started Summary




