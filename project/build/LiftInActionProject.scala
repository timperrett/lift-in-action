import sbt._
import hoffrocket.YuiCompressorPlugin

class LiftInActionProject(info: ProjectInfo) extends ParentProject(info){
  val liftVersion = "2.1-SNAPSHOT"
  
  // implement the module definitions
  lazy val chptwo = project("chapter-two", "two", new ChapterTwo(_))
  lazy val chpthree = project("chapter-three", "three", new ChapterThree(_))
  lazy val chpfour = project("chapter-four", "four", new ChapterFour(_))
  lazy val chpfive = project("chapter-five", "five", new ChapterFive(_))
  lazy val chpEight = project("chapter-eight", "eight", new ChapterEight(_))
  
  // define each module and any specific dependencies that it has
  // As chapter one is so basic, it has no specilized deps
  class ChapterTwo(info: ProjectInfo) extends ProjectDefaults(info)
  
  // chapter two requires mapper, as we'll be doing some database stuff
  class ChapterThree(info: ProjectInfo) extends ChapterTwo(info){
    val mapper = "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default"
    val h2 = "com.h2database" % "h2" % "1.2.137" % "compile"
    val mysql = "mysql" % "mysql-connector-java" % "5.1.12" % "compile"
  }
  
  class ChapterFour(info: ProjectInfo) extends ChapterThree(info){
    val textile = "net.liftweb" %% "lift-textile" % liftVersion % "compile->default"
  }
  
  class ChapterFive(info: ProjectInfo) extends ChapterFour(info){
    val machine = "net.liftweb" %% "lift-machine" % liftVersion % "compile->default"
    val paypal = "net.liftweb" %% "lift-paypal" % liftVersion % "compile->default"
  }
  
  // we only need mapper and textile, so just extend chapter four
  class ChapterEight(info: ProjectInfo) extends ChapterFour(info)
  
  // define some defaults
  abstract class ProjectDefaults(info: ProjectInfo) 
      extends DefaultWebProject(info) 
      with AutoCompilerPlugins
      with YuiCompressorPlugin
      with stax.StaxPlugin {
    
    // deployment
    override def managedStyle = ManagedStyle.Maven
    override def jettyWebappPath = webappPath 
    override def scanDirectories = Nil 
    
    // every chapter will be using lift (of course!)
    val webkit = "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default"
    val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.21" % "test"
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    
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
    
    val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
    val scalaSnapshots = "scala-tools.snapshots" at "http://scala-tools.org/repo-snapshots/"
    val sonatype = "oss.sonatype.org" at "http://oss.sonatype.org/content/groups/github/"
    
  }
}
