package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

class Boot {
  def boot {
    LiftRules.addToPackages("sample.snippet")
    
    LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))
  }
}

object Application {
  val sitemap = List(
    Menu("Home") / "index",
    Menu("Sample") / "sample" submenus(
      Menu("Another") / "sample" / "another",
      Menu("Example") / "sample" / "example"
    )
  )
}