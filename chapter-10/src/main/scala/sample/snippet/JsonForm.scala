package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.util.JsonCmd
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml,JsonHandler}
import net.liftweb.http.js.{JsCmd}
import net.liftweb.http.js.JsCmds.{SetHtml,Script}

class JsonForm {
  
  def head = Script(json.jsCmd)
  
  def show = {
    "#form" #>((ns: NodeSeq) => SHtml.jsonForm(json, ns))
  }
  
  object json extends JsonHandler {
    def apply(in: Any): JsCmd =
      SetHtml("json_result", in match {
        case JsonCmd("processForm", _, params: Map[String, Any], _) => 
          <p>Publisher: {params("publisher")}, Title: {params("title")}</p>
        case x => <span class="error">Unknown issue handling JSON: {x}</span>
      })
  }
}