package sample.env

// import net.liftweb.ht
import sample.snippet.{Service,StubService}

trait Environment {
  def serviceSnippet: Service
}

object Development extends Environment {
  def serviceSnippet = new StubService
}

object Production extends Environment {
  def serviceSnippet = new Service
}
