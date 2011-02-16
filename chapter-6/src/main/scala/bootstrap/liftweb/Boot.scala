package bootstrap.liftweb

import scala.xml.{Text,NodeSeq}
import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import net.liftweb.http.{LiftRules,S,RedirectResponse,SessionVar}
import net.liftweb.http.auth.{HttpBasicAuthentication,AuthRole,userRoles}
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._

import net.liftweb.widgets.autocomplete.AutoComplete

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
    
    AutoComplete.init
    
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "index",
      Menu("Listing 6.1 & 6.2") / "seven_dot_two",
      Menu("Listing 6.3") / "seven_dot_three",
      Menu("Embedding Example") / "embedding",
      Menu("Tail Example") / "tail",
      Menu("Resource ID Example") / "resource_ids",
      Menu("Listing 6.9: Accessing snippet attributes") / "seven_dot_nine",
      Menu("Listing 6.10: Class snippet and object singleton snippet") / "seven_dot_ten",
      Menu("Listing 6.11: Stateful snippet count incrementing") / "seven_dot_eleven",
      Menu("Listing 6.12: Wiring a () => NodeSeq into LiftRules.viewDispatch") / "seven_dot_tweleve" / "example",
      Menu("Listing 6.13: Implementing LiftView sub-type") / "MyView" / "sample",
      Menu("Listing 6.14: Implementing a RequestVar[Box[String]]") / "request_var",
      Menu("Listing 6.15: Getting and setting a cookie value") / "seven_dot_fifteen",
      Menu("Listing 6.16: Basic LiftScreen implementation") / "lift_screen_one",
      Menu("Listing 6.17: Applying validation to LiftScreen sample (7.16)") / "lift_screen_two",
      Menu("Listing 6.18: Building Wizard workflow") / "wizard_example",
      Menu("Listing 6.19: Implementing the AutoComplete snippet helper") / "auto_complete",
      Menu("Listing 6.20: The Gravatar Widget") / "gravatar_sample"
    ))
  }
  
}
