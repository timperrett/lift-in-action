package bootstrap.liftweb

// import _root_.net.liftweb.util._
import scala.xml.{Text,NodeSeq}
import java.util.Locale
import net.liftweb.common.{Box,Full,Empty,LazyLoggable}
import net.liftweb.util.Helpers.{tryo,randomString}
import net.liftweb.http.{LiftRules,S,RequestMemoize,RedirectResponse}
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.sitemap.{SiteMap,Menu}
import net.liftweb.sitemap.Loc.{EarlyResponse,Hidden}

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
     * Log a wanring in the case that a translation key is missing.
     */
    LiftRules.localizationLookupFailureNotice = 
      Full((key,locale) => 
        logger.warn("No translation for %s exists for %s".format(key,locale.getDisplayName)))
    
    /**
     * Build the sitemap
     */
    LiftRules.setSiteMap(SiteMap(
      Menu.i("Home") / "index" >> EarlyResponse(() => Full(RedirectResponse("/localization/"))) >> Hidden,
      Menu.i("Localization") / "localization" / "index" submenus(
        Menu.i("XML Bundles") / "localization" / "with-xml",
        Menu.i("Properties Bundles") / "localization" / "with-properties"
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
