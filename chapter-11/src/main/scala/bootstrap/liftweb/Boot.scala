package bootstrap.liftweb

import net.liftweb.http.LiftRules
import net.liftweb.sitemap.{SiteMap,Menu}

class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("sample")
    
    // Build the application SiteMap
    def sitemap = SiteMap(Menu("Home") / "index")
    LiftRules.setSiteMap(sitemap)
  }
}

