package sample.lib

import net.liftweb.http.rest.RestHelper

object BasicDispatchUsage extends RestHelper {
  serve {
    case "my" :: "sample" :: _ Get _ => <b>Static</b>
  }
}
