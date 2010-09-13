package bootstrap.liftweb

// import _root_.net.liftweb.util._
import scala.xml.{Text,NodeSeq}
import java.util.Locale
import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.mapper.{DefaultConnectionIdentifier,DB,Schemifier,StandardDBVendor,MapperRules}

class Boot extends Loggable {
  def boot {
    LiftRules.addToPackages("sample")
    
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    /**
     * Lift by default only looks for bundles prefixed with "lift_" 
     * so we need to tell it to look for other ones if you want to 
     * give them different (or multiple) names
     */
    LiftRules.resourceNames = "content" :: LiftRules.resourceNames
    
    LiftRules.localeCalculator = (request: Box[HTTPRequest]) => {
      println(request.flatMap(_.locale))
      request.flatMap(_.locale).openOr(Locale.getDefault())
    }
    
    LiftRules.statelessRewrite.prepend {
      case RewriteRequest(ParsePath("sample" :: Nil, _, _,_), _, _) => 
           RewriteResponse("index" :: Nil)
    }

    
    //LiftRules.setSiteMap(SiteMap(Application.sitemap:_*))
  }
}

object Application {
  val database = DBVendor
  
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:database/temp;DB_CLOSE_DELAY=-1"),
    Props.get("db.user"),
    Props.get("db.pass"))
  
}