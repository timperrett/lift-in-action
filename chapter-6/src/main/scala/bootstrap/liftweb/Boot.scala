package bootstrap.liftweb

import net.liftweb.common.{Box,Full,Empty,Loggable}
import net.liftweb.util.{Props,FormBuilderLocator}
import net.liftweb.util.Helpers._
import net.liftweb.http.{LiftRules,SHtml,Req,XHtmlInHtml5OutProperties}
import net.liftweb.sitemap.{SiteMap,Menu}
import net.liftweb.widgets.autocomplete.AutoComplete
import sample.snippet.{RequestVarSample,Book}

class Boot {
  def boot {
    LiftRules.addToPackages("sample")
    
    LiftRules.snippetDispatch.append {
      case "request_var_sample" => RequestVarSample
    }
    
    LiftRules.viewDispatch.append {
      case "viewthing" :: "example" :: Nil => Left(() => Full(<h1>Manual Sample</h1>))
    }
    
    LiftRules.appendGlobalFormBuilder(FormBuilderLocator[List[Book]](
      (books,setter) => SHtml.select(books.map(b => (b.reference.toString, b.title)), Empty, v => println(v))
    ))
    
    AutoComplete.init
    
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)
    
    LiftRules.htmlProperties.default.set((r: Req) => 
      new XHtmlInHtml5OutProperties(r.userAgent)) 
    
    
    LiftRules.setSiteMap(SiteMap(
      Menu("Home") / "index",
      Menu("Templating") / "templating" / "index" submenus(
        Menu("Single Bind Point") / "templating" / "single_bind_point",
        Menu("Multiple Bind Points") / "templating" / "multiple_bind_points",
        Menu("Embedding Example") / "templating" / "embedding",
        Menu("Tail Example") / "templating" / "tail",
        Menu("Resource ID Example") / "templating" / "resource_ids"
      ),
      Menu("Snippets & Views") / "snippets" / "index" submenus(
        Menu("Basic Snippet Example") / "snippets" / "basic_example",
        Menu("CSS Transformers") / "snippets" / "css_transformers",
        Menu("Intermediate Snippet Example") / "snippets" / "intermediate_example",
        Menu("Accessing snippet attributes") / "snippets" / "snippet_attributes",
        Menu("Class snippet vs Singleton snippet") / "snippets" / "classes_vs_singletons",
        Menu("Stateful snippet count incrementing") / "snippets" / "stateful_snippet_count",
        Menu("Lazy Loading Snippets") / "snippets" / "lazy_loading",
        Menu("Wiring a () => NodeSeq into LiftRules.viewDispatch") / "viewthing" / "example",
        Menu("Implementing LiftView sub-type") / "MyView" / "sample"
      ),
      Menu("Requests, Sessions & Cookies") / "state" / "index" submenus(
        Menu("Implementing a RequestVar[Box[String]]") / "wizard" / "request_var",
        Menu("Getting and setting a cookie value") / "wizard" / "cookie_handling"
      ),
      Menu("LiftScreen and Wizard") / "wizard" / "index" submenus(
        Menu("Basic LiftScreen implementation") / "wizard" / "lift_screen_one",
        Menu("Applying validation to LiftScreen sample") / "wizard" / "lift_screen_two",
        Menu("LiftScreen custom field types") / "wizard" / "lift_screen_custom",
        Menu("Building Wizard workflow") / "wizard" / "wizard_example"
      ),
      Menu("Widgets") / "widgets" / "index" submenus(
        Menu("Implementing the AutoComplete snippet helper") / "widgets" / "auto_complete",
        Menu("The Gravatar Widget") / "widgets" / "gravatar_sample"
      )
    ))
  }
  
}
