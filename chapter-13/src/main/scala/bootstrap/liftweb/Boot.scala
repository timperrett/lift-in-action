package bootstrap.liftweb

// import _root_.net.liftweb.util._
import scala.xml.{Text,NodeSeq}
import java.util.Locale
import net.liftweb.common.{Box,Full,Empty,LazyLoggable}
import net.liftweb.http.{LiftRules}
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.sitemap.{SiteMap,Menu}

class Boot extends LazyLoggable {
  def boot {
    LiftRules.addToPackages("sample")
    
    /**
     * Set the character encoding to utf-8 early in the pipline
     */
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    /**
     * Lift by default only looks for bundles prefixed with "lift_" 
     * so we need to tell it to look for other ones if you want to 
     * give them different (or multiple) names
     */
    LiftRules.resourceNames = "content" :: LiftRules.resourceNames
    
    /**
     * What stratagy should be used to determine the correct locale
     * for this request.
     */
    LiftRules.localeCalculator = (request: Box[HTTPRequest]) => {
      println(request.flatMap(_.locale))
      request.flatMap(_.locale).openOr(Locale.getDefault())
    }
    
    /**
     * Build the sitemap
     */
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "index"
    ))
  }
}
