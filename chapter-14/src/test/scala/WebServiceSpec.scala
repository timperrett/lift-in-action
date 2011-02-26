package sample.test

import org.specs.Specification
import net.liftweb.http.testing.ReportFailure
import net.liftweb.http.testing.TestKit

class WebServiceSpec extends Specification with JettySetupAndTearDown with TestKit {
  
  implicit val reportError = new ReportFailure {
    def fail(msg: String): Nothing = WebServiceSpec.this.fail(msg)
  }
  
  val baseUrl = JettyTestServer.baseUrl
  
  setup().beforeSpec
  destroy().afterSpec
  
  "Example web service" should {
    "List the days of the week in order" in {
      for {
        days <- get("/testkit/services/days", theHttpClient, Nil) !@ "Unable to get day list"
        xml <- days.xml
      } {
        xml must ==/(<days>
          <day>Monday</day>
          <day>Tuesday</day>
          <day>Wednesday</day>
          <day>Thursday</day>
          <day>Friday</day>
        </days>)
      }
    }
  }
  
}