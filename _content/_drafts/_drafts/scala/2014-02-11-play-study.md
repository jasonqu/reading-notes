play

先看play.bat

有conf\application.conf project的走existingApplication分支
否则走noApplication

noApplication分支是这样的
java -Dsbt.ivy.home="%~dp0repository" -Dplay.home="%~dp0framework" -Dsbt.boot.properties="%fp%framework/sbt/play.boot.properties" %PLAY_OPTS% -jar "%~dp0framework\sbt\sbt-launch.jar" %*

对existingApplication
如果是clean-all命令，结束
如果没有命令，enterConsole 即 call %buildScript% play，如果有附加参数，则也加上
如果设定了debug信息，则set JPDA_PORT=9999

buildScript在一开始定义的 set buildScript="%~dp0framework\build.bat"，代码如下：

set PLAY_VERSION="2.2.1"

if defined JPDA_PORT set DEBUG_PARAM="-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%JPDA_PORT%"

set p=%~dp0
set p=%p:\=/%
set fp=file:///!p: =%%20!

java -Xms512M -Xmx1024M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M %DEBUG_PARAM% %JAVA_OPTS% -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8888 -Dfile.encoding=UTF-8 -Dinput.encoding=Cp1252 -Dplay.version="%PLAY_VERSION%" -Dsbt.ivy.home="%~dp0..\repository" -Dplay.home="%~dp0." -Dsbt.boot.properties="%fp%sbt/sbt.boot.properties" %PLAY_OPTS% -jar "%~dp0sbt\sbt-launch.jar" %*

显然，play就是一个sbt工具

看看play的文件夹
repository 是ivy下载依赖的地方
framework是play的源代码
samples是play的例子

看看play的源代码：
project 是play插件提供的功能
sbt 是sbt的配置参数
script是play的脚本
skeletons就是new命令创建的工程模板
src是源代码，里面有个各个工程

首先看看project下的内容
build.properties指明了sbt的版本号：
sbt.version=0.12.4

Build.scala
object BuildSettings 基本设置
并定义了几个Project: PlaySharedJavaProject PlayRuntimeProject PlaySbtProject
和一些设置: playCommonSettings playRuntimeSettings dontPublishSettings

object Resolvers Resolver定义

object PlayBuild extends Build 是核心文件
  import Resolvers._
  import Dependencies._
  import BuildSettings._
  import Generators._
  import Tasks._
关注一下template的工程就可以看到
一个是runtime的工程，一个是sbt工程
lazy val TemplatesProject = PlayRuntimeProject("Templates", "templates")
lazy val TemplatesCompilerProject = PlaySbtProject("Templates-Compiler", "templates-compiler")

我们来细看一下：
SbtLinkProject = PlaySharedJavaProject("SBT-link", "sbt-link") TODO
TODO

Dependencies.scala
object Dependencies 依赖信息

Docs.scala
object Docs 文档配置信息

Tasks.scala
import sbt._
import Keys._
import sbt.File

object Generators 定义了一个代码生成的方法
def PlayVersion(dir: File): Seq[File] 
// Generates a scala file that contains the play version for use at runtime.

object Tasks 定义了一些任务 生成分发包
val generateDistTask: Setting[_]
def generateDistribution(repoReport: RepositoryBuilder.LocalRepoReport, bd: File, target: File, version: String): File

Template方法
详解：
1. 用反射获得两个clas实例 play.templates.ScalaTemplateCompiler play.templates.GeneratedSource
2. 对generatedDir中的每一个template.scala文件，执行sync.invoke(content)
3. 取ScalaTemplateCompiler的compile方法，并执行
compile.invoke(null, template, sourceDirectory, generatedDir, "play.api.templates.HtmlFormat", "import play.api.templates._\nimport play.api.templates.PlayMagic._")
4. 最后将这些生成的scala文件返回： (generatedDir ** "*.scala").get.map(_.getAbsoluteFile)


  // ----- Compile templates

  lazy val ScalaTemplates = {
    (classpath: Seq[Attributed[File]], templateEngine: File, sourceDirectory: File, generatedDir: File, streams: sbt.std.TaskStreams[sbt.Project.ScopedKey[_]]) =>
      // Parent classloader must be null to ensure that we get the right scala on the classpath
      val classloader = new java.net.URLClassLoader(classpath.map(_.data.toURI.toURL).toArray, null)
      val compiler = classloader.loadClass("play.templates.ScalaTemplateCompiler")
      val generatedSource = classloader.loadClass("play.templates.GeneratedSource")

      (generatedDir ** "*.template.scala").get.foreach {
        source =>
          val constructor = generatedSource.getDeclaredConstructor(classOf[java.io.File])
          val sync = generatedSource.getDeclaredMethod("sync")
          val generated = constructor.newInstance(source)
          try {
            sync.invoke(generated)
          } catch {
            case e: java.lang.reflect.InvocationTargetException => {
              val t = e.getTargetException
              t.printStackTrace()
              throw t
            }
          }
      }

      (sourceDirectory ** "*.scala.html").get.foreach {
        template =>
          val compile = compiler.getDeclaredMethod("compile", classOf[java.io.File], classOf[java.io.File], classOf[java.io.File], classOf[String], classOf[String])
          try {
            compile.invoke(null, template, sourceDirectory, generatedDir, "play.api.templates.HtmlFormat", "import play.api.templates._\nimport play.api.templates.PlayMagic._")
          } catch {
            case e: java.lang.reflect.InvocationTargetException => {
              streams.log.error("Compilation failed for %s".format(template))
              throw e.getTargetException
            }
          }
      }

      (generatedDir ** "*.scala").get.map(_.getAbsoluteFile)
  }

  def scalaTemplateSourceMappings = (excludeFilter in unmanagedSources, unmanagedSourceDirectories in Compile, baseDirectory) map {
    (excludes, sdirs, base) =>
      val scalaTemplateSources = sdirs.descendantsExcept("*.scala.html", excludes)
      ((scalaTemplateSources --- sdirs --- base) pair (relativeTo(sdirs) | relativeTo(base) | flat)) toSeq
  }
  方法只在这里用到：
  177        sourceGenerators in Compile <+= sourceManaged in Compile map PlayVersion,
  178:       mappings in(Compile, packageSrc) <++= scalaTemplateSourceMappings,


scala template 处理
1. sbt中的ScalaTemplates任务，用法
  181:       sourceGenerators in Compile <+= (dependencyClasspath in TemplatesCompilerProject in Runtime, packageBin in TemplatesCompilerProject in Compile, scalaSource in Compile, sourceManaged in Compile, streams) map ScalaTemplates
  182      ).dependsOn(SbtLinkProject, PlayExceptionsProject, TemplatesProject, IterateesProject % "test->test;compile->compile", JsonProject)

2. 代码中也有定义
F:\Software\OpenSource\scala\play\play-2.2.1\framework\src\sbt-plugin\src\main\scala\PlaySourceGenerators.scala:
   28    }
   29  
   30:   val ScalaTemplates = (state: State, sourceDirectories: Seq[File], generatedDir: File, templateTypes: Map[String, String], additionalImports: Seq[String]) => {
   31      import play.templates._
用法类似
F:\Software\OpenSource\scala\play\play-2.2.1\framework\src\sbt-plugin\src\main\scala\PlaySettings.scala:
  111:     sourceGenerators in Compile <+= (state, unmanagedSourceDirectories in Compile, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates,



前面提到的
play.templates.ScalaTemplateCompiler play.templates.GeneratedSource
这两个类都在templates-compiler工程中
用法
sync.invoke(content)

compile.invoke(null, template, sourceDirectory, generatedDir, "play.api.templates.HtmlFormat", "import play.api.templates._\nimport play.api.templates.PlayMagic._")

分析一下：
首先是import
import scala.util.parsing.input.OffsetPosition
  import scalax.file._
  import java.io.File
  import scala.annotation.tailrec
  import io.Codec
  import scala.reflect.internal.Flags

object Hash 就是使用java.security.MessageDigest计算hash SHA-1

case class TemplateCompilationError(source: File, message: String, line: Int, column: Int)

object MaybeGeneratedSource 定义了一个unapply方法：
GeneratedSource(source).meta.isDefinedAt("SOURCE") ? Some(generated) : None

sealed trait AbstractGeneratedSource 是处理已经生成的scala文件的工具
content: String 是文件内容
meta: Map[String, String] 是元数据
用的是这些内容
                /*
                    -- GENERATED --
                    DATE: Wed Feb 12 14:51:43 CST 2014
                    SOURCE: E:/Project/scala/play2.2.1/computer-database/app/views/list.scala.html
                    HASH: 0e11a34186e4826159305ae6eb9ae6c70db34fbb
                    MATRIX: 624->1|830->628|844->634|958->667|1014->687|1043->694|1088->703|1143->750|1228->811|1285->832|1329->854|1368->857|1395->862|1433->258|1444->262|1729->129|1758->256|1786->516|1816->626|1844->878|1882->881|1894->885|1933->887|1984->902|2057->953|2104->965|2118->970|2146->989|2197->1002|2314->1083|2343->1090|2395->1111|2474->1154|2489->1160|2537->1199|2577->1201|2679->1267|2714->1280|2894->1428|2987->1485|3002->1491|3045->1512|3135->1567|3169->1592|3203->1617|3256->1632|3412->1752|3460->1778|3518->1800|3563->1823|3621->1845|3668->1870|3726->1892|3768->1912|3886->1994|3904->2003|3917->2007|3928->2031|3964->2058|4004->2060|4112->2132|4127->2138|4183->2172|4222->2175|4239->2183|4266->2188|4378->2264|4395->2272|4454->2322|4494->2324|4540->2337|4677->2438|4694->2446|4755->2498|4795->2500|4841->2513|4978->2614|4994->2621|5025->2643|5065->2645|5110->2658|5229->2762|5389->2886|5409->2897|5427->2906|5475->2916|5584->2989|5616->2999|5701->3065|5720->3075|5760->3077|5930->3215|6039->3288|6085->3312|6126->3317|6186->3354|6228->3359|6249->3370|6278->3376|6358->3420|6378->3431|6396->3440|6444->3450|6553->3523|6585->3533|6666->3595|6685->3605|6725->3607|6891->3741|6958->3789|6978->3799|7019->3801|7157->3907|7201->3919
                    LINES: 19->1|21->16|21->16|23->16|24->17|24->17|24->17|24->17|24->17|25->18|25->18|25->18|25->18|27->6|27->6|33->1|35->5|36->11|38->15|39->20|41->22|41->22|41->22|43->24|43->24|45->26|45->26|45->26|45->26|47->28|47->28|49->30|53->34|53->34|53->34|53->34|54->35|54->35|56->37|58->39|58->39|58->39|62->43|62->43|62->43|62->43|67->48|67->48|68->49|68->49|69->50|69->50|70->51|70->51|75->56|75->56|75->56|75->57|75->57|75->57|77->59|77->59|77->59|77->59|77->59|77->59|79->61|79->61|79->61|79->61|79->61|82->64|82->64|82->64|82->64|82->64|85->67|85->67|85->67|85->67|85->67|88->71|95->78|95->78|95->78|95->78|97->80|97->80|99->82|99->82|99->82|103->86|105->88|105->88|105->88|105->88|105->88|105->88|105->88|107->90|107->90|107->90|107->90|109->92|109->92|111->94|111->94|111->94|115->98|119->102|119->102|119->102|125->108|128->111
                    -- GENERATED --
                */
matrix 应该是列对应关系
lines 应该是行对应关系
mapPosition 找列
mapLine     找行

case class GeneratedSource(file: File) extends AbstractGeneratedSource
needRecompilation(imports: String): Boolean 确定文件是不是被修改了
toSourcePosition(marker: Int): (Int, Int) 通过marker来找到具体的位置
source: Option[File] 源文件
sync 就是删掉生成的文件

case class GeneratedSourceVirtual(path: String) extends AbstractGeneratedSource

Notice
object ScalaTemplateCompiler 核心类

    import scala.util.parsing.input.Positional
    import scala.util.parsing.input.CharSequenceReader
    import scala.util.parsing.combinator.JavaTokenParsers

这里用到了scala parse api
具体可以参考 programming scala chap31
beginning scala
计算器，怎么还用到栈了呢 http://bitwalker.github.io/blog/2013/08/10/learn-by-example-scala-parser-combinators/
排版不好 http://henkelmann.eu/2011/01/13/an_introduction_to_scala_parser_combinators
http://www.slideshare.net/daltontf/implementing-external-dsls-using-scala-parser-combinators
http://www.codecommit.com/blog/scala/the-magic-behind-parser-combinators

定义需要解析的部分：
    abstract class TemplateTree
    abstract class ScalaExpPart


应该是开始的参数
    case class Params(code: String) extends Positional

    case class Template(name: PosString, comment: Option[Comment], params: PosString, imports: Seq[Simple], defs: Seq[Def], sub: Seq[Template], content: Seq[TemplateTree]) extends Positional
    case class PosString(str: String) extends Positional {
      override def toString = str
    }
。。。后略

    def compile(source: File, sourceDirectory: File, generatedDirectory: File, formatterType: String, additionalImports: String = "") = {
      先generatedFile，如果需要重新编译，调用 parseAndGenerateCode

compileVirtual与compile类似，不过不看是否需要重新编译

parseAndGenerateCode是解析模板的和新方法
如果解析成功就调用 generateFinalTemplate 方法

generatedFile 嗲用GeneratedSource 生成 .template.scala 文件
generatedFileVirtual 与之一样 不过调用的是GeneratedSourceVirtual

source2TemplateName @tailrec

然后是class  TemplateParser 长长的定义

后略







