package sample.env

// import net.liftweb.ht
import sample.snippet.{Service,StubService,CookieList}

trait Environment {
  def serviceSnippet: Service
  def cookieList = new CookieList
}

object Development extends Environment {
  def serviceSnippet = new StubService
}

object Production extends Environment {
  def serviceSnippet = new Service
}
