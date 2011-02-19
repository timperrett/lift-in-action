import sbt._
class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  lazy val stax = "eu.getintheloop" % "sbt-stax-plugin" % "0.1.1"
  lazy val n2a = "eu.getintheloop" % "sbt-native2ascii-plugin" % "0.1.0"
  lazy val sbtYui = "hoffrocket" % "sbt-yui" % "0.2"
  lazy val scctPlugin = "reaktor" % "sbt-scct-for-2.8" % "0.1-SNAPSHOT"
  lazy val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.3.0"
  // repos
  val staxReleases = "stax.repo" at "http://mvn.stax.net/content/repositories/public/"
  val sonaTypeRepo = "sonatype.repo" at "http://oss.sonatype.org/content/groups/github/"
  val scctRepo = "scct.repo" at "http://mtkopone.github.com/scct/maven-repo/"
  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
}