package sample.test

import scala.xml.NodeSeq
import net.liftweb.http.S
import net.liftweb.mockweb.WebSpec
import net.liftweb.mocks.MockHttpServletRequest

object ExampleOneSpec extends WebSpec {
  "Thing Snippet" should {
    setSequential()
    val r = new MockHttpServletRequest("/")
    "List all cookies, seperated by a break line" withSFor(r) in {
      S.runTemplate(List("testkit","cookies")).pass(println) openOr NodeSeq.Empty
    }
  }
}