package sample.test

import org.specs.Specification
import net.liftweb.http.testing.{TestKit,ReportFailure,HttpResponse}

class WebServiceSpec extends Specification with JettySetupAndTearDown with TestKit {
  
  implicit val reportError = new ReportFailure {
    def fail(msg: String): Nothing = {
      WebServiceSpec.this.fail(msg)
    }
  }
  
  val baseUrl = JettyTestServer.baseUrl
  
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
    
    "Not have access to secret stuff if not authenticated" in {
      get("/testkit/services/secret") ! (
        403, "Access should fail with unauthorized status"
      ) must haveClass[HttpResponse]
    }
    
    "Gain access to secret stuff if they are loggedin" in {
      for {
        auth <- post("/testkit/services/login") !@ "Unable to login!"
        resp <- auth.get("/testkit/services/secret") !@ "Not properly authenticated"
      }{
        resp must haveClass[HttpResponse]
      }
    }
    
  }
  
}