package bootstrap.liftweb

import scala.xml.{Text,NodeSeq}
import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import net.liftweb.http.{LiftRules,S,RedirectResponse,SessionVar}
import net.liftweb.http.auth.{HttpBasicAuthentication,AuthRole,userRoles}
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("sample.snippet")
    
  }
}
