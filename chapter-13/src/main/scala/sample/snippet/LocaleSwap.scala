package sample.snippet

import java.util.Locale
import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.http.{DispatchSnippet,S}

class LocaleSwap extends DispatchSnippet {
  def dispatch = {
    case _ => render _
  }
  def render(xhtml: NodeSeq): NodeSeq = Locale.getAvailableLocales.toList
    .sortBy(l => l.getDisplayCountry(l)).filter(l => l.getDisplayCountry(l) != "").map { l => 
      <option value={l.toString}>{l.getDisplayCountry(l) + " ("+l.getDisplayLanguage(l)+")"}</option>
    }
}