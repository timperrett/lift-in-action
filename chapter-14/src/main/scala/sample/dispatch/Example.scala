package sample.dispatch

import net.liftweb.http.{ForbiddenResponse,OkResponse,SessionVar}
import net.liftweb.http.rest.RestHelper

object Authenticated extends SessionVar(false)

object Example extends RestHelper {
  val days = List("Monday","Tuesday","Wednesday","Thursday","Friday")
  serve {
    case "t" :: "services" :: "days" :: Nil Get _ => 
      <days>{days.flatMap(d => <day>{d}</day>)}</days>
    case "t" :: "services" :: "login" :: Nil Post _ =>
      Authenticated(true); OkResponse()
    case "t" :: "services" :: "secret" :: Nil Get _ => 
      if(Authenticated.is == true) OkResponse() else ForbiddenResponse("Its secret!")
  }
}

