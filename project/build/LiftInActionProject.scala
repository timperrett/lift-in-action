import sbt._
import hoffrocket.YuiCompressorPlugin
import eu.getintheloop.Native2AsciiPlugin

class LiftInActionProject(info: ProjectInfo) extends ParentProject(info){
  val liftVersion = "2.2"
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
  lazy val chpTwelve = project("chapter-12", "12", new ChapterTwelve(_))
  lazy val chpThirteen = project("chapter-13", "13", new ChapterThirteen(_))
  lazy val chpFourteen = project("chapter-14", "14", new ChapterFourteen(_))
  
  // define each module and any specific dependencies that it has
  // As chapter one is so basic, it has no specilized deps
  class ChapterTwo(info: ProjectInfo) extends ProjectDefaults(info)
  
  // chapter two requires mapper, as we'll be doing some database stuff
  class ChapterThree(info: ProjectInfo) 
      extends ChapterTwo(info) 
      with DatabaseDrivers
      with DeployToStax 
  {
    val mapper = "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default"
  }
  
  class ChapterFour(info: ProjectInfo) 
      extends ChapterThree(info) 
      with DatabaseDrivers 
      with DeployToStax 
  {
    val textile = "net.liftweb" %% "lift-textile" % liftVersion % "compile->default"
  }
  
  class ChapterFive(info: ProjectInfo) 
      extends ChapterFour(info) 
      with DatabaseDrivers 
      with DeployToStax 
  {
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
  
  class ChapterEleven(info: ProjectInfo) 
      extends ProjectDefaults(info) 
      with DatabaseDrivers 
  {
    val mapper = "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default"
  }
  
  class ChapterTwelve(info: ProjectInfo) 
      extends ProjectDefaults(info) 
      with DatabaseDrivers 
  {
    val squeryl = "net.liftweb" %% "lift-squeryl-record" % liftVersion % "compile->default"
    val couch = "net.liftweb" %% "lift-couchdb" % liftVersion % "compile->default"
    val mongo = "net.liftweb" %% "lift-mongodb-record" % liftVersion % "compile->default"
    // statically supplied the 2.8.1 version as it does not currently exist in 
    // any public repository
    // val rogue = "com.foursquare" %% "rogue" % "1.0.2" % "compile->default"
  }
  
  class ChapterThirteen(info: ProjectInfo) 
      extends ChapterFour(info) 
      with Native2AsciiPlugin 
      with DatabaseDrivers 
  {
    val akka = "se.scalablesolutions.akka" % "akka-actor" % "1.0-RC6" % "compile"
    val remote = "se.scalablesolutions.akka" % "akka-remote" % "1.0-RC6" % "compile"
    val jpa = "net.liftweb" %% "lift-jpa" % liftVersion % "compile"
    val jta = "net.liftweb" %% "lift-jta" % liftVersion % "compile"
    val amqp = "net.liftweb" %% "lift-amqp" % liftVersion % "compile"
    val geronimoejb = "geronimo-spec" % "geronimo-spec-ejb" % "2.1-rc4" % "compile"
    val geronimojta = "geronimo-spec" % "geronimo-spec-jta" % "1.0.1B-rc4" % "provided"
    // because we need to specifically exclude the JTA transative dependency for the entity
    // manager, its nessicary to define the ivy xml directly as the DSL does not have a 
    // grammer for exclusions
    override def ivyXML =
      <dependencies>
        <dependency org="org.hibernate" name="hibernate-entitymanager" rev="3.4.0.GA">
          <exclude org="javax.transaction" module="jta"/>
        </dependency>
      </dependencies>
  }
  
  class ChapterFourteen(info: ProjectInfo)
      extends ProjectDefaults(info) 
      with DatabaseDrivers 
  {
    val ostrich = "com.twitter" % "ostrich" % "2.3.6" % "compile->default"
    val mapper = "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default"
  }
  
  trait DatabaseDrivers { _: DefaultWebProject => 
    // usually you would only use one database type, not three; this gives you
    // options as to what you wan to use though.
    val h2 = "com.h2database" % "h2" % h2Version % "compile->default"
    val mysql = "mysql" % "mysql-connector-java" % "5.1.12" % "compile->default"
    val postgresql = "postgresql" % "postgresql" % "9.0-801.jdbc4" % "compile->default"
  }
  
  trait DeployToStax extends stax.StaxPlugin { _: DefaultWebProject =>
    override def staxApplicationId = "liftinaction"
    override def staxUsername = "timperrett"
  }
  
  // define some defaults
  abstract class ProjectDefaults(info: ProjectInfo) 
      extends DefaultWebProject(info) 
      with YuiCompressorPlugin {
    
    override def compileOptions = Unchecked :: Deprecation :: super.compileOptions.toList
    
    // deployment
    override def managedStyle = ManagedStyle.Maven
    override def jettyWebappPath = webappPath 
    override def scanDirectories = Nil 
    
    // every chapter will be using lift (of course!)
    val webkit = "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default"
    val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.21" % "test"
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    val log4j = "org.slf4j" % "slf4j-log4j12" % "1.6.1" % "compile->default"
    
    // testing frameworks
    val scalatest = "org.scala-tools.testing" % "scalatest" % "0.9.5" % "test->default"
    
    lazy val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
    lazy val mavenCentral = "Central Maven Repository" at "http://repo1.maven.org/maven2/"
    lazy val jbossRepo = "jboss.repo" at "http://repository.jboss.org/nexus/content/groups/public-jboss/"
    lazy val twitterRepo = "twitter.repo" at "http://maven.twttr.com/"
    lazy val scalaReleases = "scala-tools.releases" at "http://scala-tools.org/repo-releases/"
    lazy val scalaSnapshots = "scala-tools.snapshots" at "http://scala-tools.org/repo-snapshots/"
    lazy val sonatype = "oss.sonatype.org" at "http://oss.sonatype.org/content/groups/github/"
    lazy val akkarepo = "akka.repo" at "http://akka.io/repository/"
    lazy val guiceyFruitRepo = "GuiceyFruit Repo" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
    
    
  }
}
