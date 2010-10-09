package bootstrap.liftweb

import scala.xml.{Text,NodeSeq}
import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import net.liftweb.http.{LiftRules,S,RedirectResponse,SessionVar}
import net.liftweb.http.auth.{HttpBasicAuthentication,AuthRole,userRoles}
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._

import sample.snippet.RequestVarSample

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("sample")
    
    LiftRules.snippetDispatch.append {
      case "request_var_sample" => RequestVarSample
    }
    
    LiftRules.viewDispatch.append {
      case "seven_dot_tweleve" :: "example" :: Nil => Left(() => Full(<h1>Manual Sample</h1>))
    }
    
    LiftRules.setSiteMap(SiteMap(sitemap:_*))
  }
  
  lazy val sitemap = List(
    Menu("Home") / "index",
    Menu("Listing 7.1 & 7.2") / "seven_dot_two",
    Menu("Listing 7.3") / "seven_dot_three",
    Menu("Embedding Example") / "embedding",
    Menu("Tail Example") / "tail",
    Menu("Resource ID Example") / "resource_ids",
    Menu("Listing 7.9: Accessing snippet attributes") / "seven_dot_nine",
    Menu("Listing 7.10: Class snippet and object singleton snippet") / "seven_dot_ten",
    Menu("Listing 7.11: Stateful snippet count incrementing") / "seven_dot_eleven",
    Menu("Listing 7.12: Wiring a () => NodeSeq into LiftRules.viewDispatch") / "seven_dot_tweleve" / "example",
    Menu("Listing 7.13: Implementing LiftView sub-type") / "MyView" / "sample",
    Menu("Listing 7.14: Implementing a RequestVar[Box[String]]") / "request_var",
    Menu("Listing 7.15: Getting and setting a cookie value") / "seven_dot_fifteen"
  )
}
