import sbt._
import hoffrocket.YuiCompressorPlugin
import eu.getintheloop.Native2AsciiPlugin

class LiftInActionProject(info: ProjectInfo) extends ParentProject(info){
  val liftVersion = "2.2-RC6"
  val h2Version = "1.3.146"
  
  // implement the module definitions
  lazy val chpTwo = project("chapter-2", "2", new ChapterTwo(_))
  lazy val chpThree = project("chapter-3", "3", new ChapterThree(_))
  lazy val chpFour = project("chapter-4", "4", new ChapterFour(_))
  lazy val chpFive = project("chapter-5", "5", new ChapterFive(_))
  lazy val chpSeven = project("chapter-7", "7", new ChapterSeven(_))
  lazy val chpEight = project("chapter-8", "8", new ChapterEight(_))
  lazy val chpNine = project("chapter-9", "9", new ChapterNine(_))
  lazy val chpTen = project("chapter-10", "10", new ChapterTen(_))
  lazy val chpEleven = project("chapter-11", "11", new ChapterEleven(_))
  lazy val chpThirteen = project("chapter-13", "13", new ChapterThirteen(_))
  lazy val chpFourteen = project("chapter-14", "14", new ChapterFourteen(_))
  
  // define each module and any specific dependencies that it has
  // As chapter one is so basic, it has no specilized deps
  class ChapterTwo(info: ProjectInfo) extends ProjectDefaults(info)
  
  // chapter two requires mapper, as we'll be doing some database stuff
  class ChapterThree(info: ProjectInfo) extends ChapterTwo(info){
    val mapper = "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default"
  }
  
  class ChapterFour(info: ProjectInfo) extends ChapterThree(info){
    val textile = "net.liftweb" %% "lift-textile" % liftVersion % "compile->default"
  }
  
  class ChapterFive(info: ProjectInfo) extends ChapterFour(info){
    val machine = "net.liftweb" %% "lift-machine" % liftVersion % "compile->default"
    val paypal = "net.liftweb" %% "lift-paypal" % liftVersion % "compile->default"
  }
  
  class ChapterSeven(info: ProjectInfo) extends ProjectDefaults(info){
    val wizard = "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default"
    val widgets = "net.liftweb" %% "lift-widgets" % liftVersion % "compile->default"
    val scalate = "net.liftweb" %% "lift-scalate" % liftVersion % "compile->default"
  }
  
  // we only need mapper and textile, so just extend chapter four
  class ChapterEight(info: ProjectInfo) extends ChapterFour(info)
  
  class ChapterNine(info: ProjectInfo) extends ProjectDefaults(info)
  
  class ChapterTen(info: ProjectInfo) extends ProjectDefaults(info)
  
  class ChapterEleven(info: ProjectInfo) extends ProjectDefaults(info){
    val mapper = "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default"
  }
  
  class ChapterThirteen(info: ProjectInfo) extends ChapterFour(info) with Native2AsciiPlugin
  
  class ChapterFourteen(info: ProjectInfo) extends ProjectDefaults(info){
    val ostrich = "com.twitter" % "ostrich" % "2.3.1" % "compile->default"
    val mapper = "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default"
  }
  
  
  // define some defaults
  abstract class ProjectDefaults(info: ProjectInfo) 
      extends DefaultWebProject(info) 
      with AutoCompilerPlugins
      with YuiCompressorPlugin
      with stax.StaxPlugin {
    
    override def compileOptions = Unchecked :: super.compileOptions.toList
    
    // deployment
    override def managedStyle = ManagedStyle.Maven
    override def jettyWebappPath = webappPath 
    override def scanDirectories = Nil 
    
    // every chapter will be using lift (of course!)
    val webkit = "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default"
    val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.21" % "test"
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    val log4j = "org.slf4j" % "slf4j-log4j12" % "1.6.1" % "compile->default"
    //val h2 = "com.h2database" % "h2" % h2Version % "compile->default"
    //val mysql = "mysql" % "mysql-connector-java" % "5.1.12" % "compile->default"
    val postgresql = "postgresql" % "postgresql" % "9.0-801.jdbc4" % "compile->default"
    
    // stax
    override def staxApplicationId = "liftinaction"
    override def staxUsername = "timperrett"
    
    // testing frameworks
    val scalatest = "org.scala-tools.testing" % "scalatest" % "0.9.5" % "test->default"
    
    // scala x-ray compiller plugin
    def sxrMainPath = outputPath / "classes.sxr"
    def sxrTestPath = outputPath / "test-classes.sxr"
    def sxrPublishPath = normalizedName / version.toString
    lazy val publishSxr = 
      syncTask(sxrMainPath, sxrPublishPath / "main") dependsOn(
        syncTask(sxrTestPath, sxrPublishPath / "test") dependsOn(testCompile)
      )
    
    val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
    val jbossRepo = "jboss-repo" at "http://repository.jboss.org/nexus/content/groups/public-jboss/"
    val twitterRepo = "twitter-repo" at "http://maven.twttr.com/"
    val scalaReleases = "scala-tools.snapshots" at "http://scala-tools.org/repo-releases/"
    val scalaSnapshots = "scala-tools.snapshots" at "http://scala-tools.org/repo-snapshots/"
    val sonatype = "oss.sonatype.org" at "http://oss.sonatype.org/content/groups/github/"
    
  }
}
