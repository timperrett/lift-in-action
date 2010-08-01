package bootstrap.liftweb

// import _root_.net.liftweb.util._
import scala.xml.{Text,NodeSeq}
import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import net.liftweb.http.{LiftRules,S,RedirectResponse,SessionVar}
import net.liftweb.http.auth.{HttpBasicAuthentication,AuthRole,userRoles}
import net.liftweb.mapper.{DefaultConnectionIdentifier,DB,Schemifier,StandardDBVendor,MapperRules}
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("sample.snippet")
  
    LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))
  }
}

object Application {
  
  val sitemap = List(
    Menu("Home") / "index"
  )
  
  val database = DBVendor
  
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/temp;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
}