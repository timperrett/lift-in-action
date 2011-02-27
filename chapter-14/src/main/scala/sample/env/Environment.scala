package sample.env

// import net.liftweb.ht
import sample.snippet._

trait Environment {
  def serviceSnippet: Service
  def cookieList = new CookieList
  def ajaxSample = new AjaxSample
  def formSample = new FormSample
}

object Development extends Environment {
  def serviceSnippet = new StubService
}

object Production extends Environment {
  def serviceSnippet = new Service
}
