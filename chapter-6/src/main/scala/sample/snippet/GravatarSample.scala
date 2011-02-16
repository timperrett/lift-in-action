package sample.snippet

import scala.xml.NodeSeq
import net.liftweb.widgets.gravatar.Gravatar

class GravatarSample {
  def display(xhtml: NodeSeq): NodeSeq = 
    Gravatar("your.email@domain.com")
}