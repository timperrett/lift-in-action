package sample.snippet

import scala.xml.{Text,NodeSeq}
import net.liftweb.http.PageName

class Sample {
  def name: NodeSeq = Text(PageName.is)
}
