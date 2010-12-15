package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmds.{Alert,Script,Run} 

class AbstractionExamples {
  def alert(xhtml: NodeSeq): NodeSeq = 
    Script(Run(Alert("Important Alert Goes Here!")))
}