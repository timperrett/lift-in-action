package sample.lib

import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{Req,GetRequest,PlainTextResponse}

object SecondDispatchUsage extends RestHelper {
  serve {
    case "sample" :: "one" :: _ XmlGet _ => <b>Static</b>
    case "sample" :: "two" :: Nil XmlGet _ => <b>Static</b>
    case XmlGet("sample" :: "three" :: Nil, _) => <b>Static</b>
    case Req("sample" :: "four" :: Nil, "xml", GetRequest) => <b>Static</b>
	case "test" :: Nil Get _ => PlainTextResponse("ok")
  }
}
