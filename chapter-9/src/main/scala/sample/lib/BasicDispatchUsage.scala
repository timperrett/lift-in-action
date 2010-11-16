package sample.lib

import net.liftweb.http.rest.RestHelper

object BasicDispatchUsage extends RestHelper {
  serve {
    case XmlGet("api" :: "static" :: _, _) => <b>Static</b>
  }
}
