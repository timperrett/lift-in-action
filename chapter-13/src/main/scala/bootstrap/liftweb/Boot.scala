package bootstrap.liftweb

// import _root_.net.liftweb.util._
import scala.xml.{Text,NodeSeq}
import java.util.Locale
import net.liftweb.common.{Box,Full,Empty,LazyLoggable}
import net.liftweb.util.Helpers.{tryo,randomString}
import net.liftweb.http.{LiftRules,S,RequestMemoize}
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
     * for this request. This sample simple determines it based upon
     * the query string paramater "hl". /foo?hl=en_GB
     */
    LiftRules.localeCalculator = (request: Box[HTTPRequest]) => 
      localeMemo(request.hashCode, (for { 
        r <- request
        p <- tryo(r.param("hl").head.split(Array('_','-')))
      } yield p match {
        case Array(lang) => new Locale(lang)
        case Array(lang,country) => new Locale(lang,country)
      }).openOr(Locale.getDefault))
    
    /**
     * Build the sitemap
     */
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "index",
      Menu("Localization") / "localization" / "index" submenus(
        Menu("XML Bundles") / "localization" / "with-xml",
        Menu("Properties Bundles") / "localization" / "with-properties",
        Menu("Custom Bundles") / "localization" / "with-custom"
      ),
      Menu("Java Enterprise Integration") / "jee" submenus(
        Menu("Lift JPA") / "jee" / "jpa",
        Menu("Lift JTA") / "jee" / "jta"
      ),
      Menu("Messaging and Distribution") / "distributed" submenus(
        Menu("Lift AMQP") / "distributed" / "amqp",
        Menu("Leveraging Akka") / "distributed" / "akka"
      )
    ))
  }
  
  /**
   * Memoization object for the locale calculator
   */
  object localeMemo extends RequestMemoize[Int, Locale] {
    override protected def __nameSalt = randomString(20)
  }
}
