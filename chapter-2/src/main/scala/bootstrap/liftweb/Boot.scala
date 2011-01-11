package bootstrap.liftweb

import net.liftweb.util.NamedPF
import net.liftweb.http.{LiftRules,NotFoundAsTemplate,ParsePath}
import net.liftweb.sitemap.{SiteMap,Menu}

class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("example.travel")
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    // Build the application SiteMap
    LiftRules.setSiteMap(SiteMap(Menu("Home") / "index"))
  }
}

