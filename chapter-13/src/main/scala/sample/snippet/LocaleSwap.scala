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
    .sortBy(_.getDisplayCountry(S.locale)).filter(_.getDisplayCountry(S.locale) != "").map { l => 
      <option value={l.toString}>{l.getDisplayCountry(S.locale) + " ("+l.getDisplayLanguage(S.locale)+")"}</option>
    }
}