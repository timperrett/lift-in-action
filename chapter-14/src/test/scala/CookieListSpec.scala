package sample.test

import scala.xml.NodeSeq
import javax.servlet.http.Cookie
import net.liftweb.http.S
import net.liftweb.mockweb.WebSpec
import net.liftweb.mocks.MockHttpServletRequest

trait CookieListSpec { _: WebSpec =>  //extends WebSpec with BootSetupAndTearDown {
  "CookieList Snippet" should {
    val cookieName = "thing"
    val r = new MockHttpServletRequest("/")
    r.cookies = List(new Cookie(cookieName, "value"))
    
    "List all cookies, seperated by a break line (runTemplate)" withSFor(r) in {
      val xml = S.runTemplate(List("testkit","cookies")) openOr NodeSeq.Empty
      xml must \\(<div id="output">thing<br></br></div>)
    }
  }
}
