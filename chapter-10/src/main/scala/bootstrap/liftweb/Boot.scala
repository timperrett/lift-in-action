package bootstrap.liftweb

import net.liftweb.common.{Full}
import net.liftweb.http.LiftRules
import net.liftweb.http.js.jquery.JQuery14Artifacts 
import net.liftweb.sitemap.{SiteMap,Menu}

class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("sample")
    
    // set the JSArtifacts
    LiftRules.jsArtifacts = JQuery14Artifacts
    
    // make the furniture appear
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("loading").cmd)
    
    // make the furniture go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("loading").cmd)
    
    
    // build the application SiteMap
    def sitemap = SiteMap(
      Menu("Home") / "index",
      Menu("Basic JavaScript") / "basic_javascript",
      Menu("Basic AJAX") / "basic_ajax",
      Menu("Sophisticated AJAX") / "more_ajax",
      Menu("JSON Form") / "json_form",
      Menu("Wiring: Basic") / "simple_wiring"
    )
    LiftRules.setSiteMap(sitemap)
  }
}

