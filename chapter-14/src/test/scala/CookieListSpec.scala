package sample.test

import scala.xml.NodeSeq
import javax.servlet.http.Cookie
import net.liftweb.http.S
import net.liftweb.mockweb.WebSpec
import net.liftweb.mocks.MockHttpServletRequest

object CookieListSpec extends WebSpec with SetupAndTearDown {
  construcEnvironment().beforeSpec
  "Thing Snippet" should {
    setSequential()
    val cookieName = "thing"
    val r = new MockHttpServletRequest("/")
    r.cookies = List(new Cookie(cookieName, "value"))
    
    "List all cookies, seperated by a break line" withSFor(r) in {
      val xml = S.runTemplate(List("testkit","cookies")) openOr NodeSeq.Empty
      for(div <- xml \\ "div"; id <- (div \ "@id")){
        if(id.text == "output")
          div.text.trim must_== cookieName
      }
    }
  }
  tearDownEnvironment().afterSpec
}