package sample.snippet

import net.liftweb.util.Helpers._

class LongTime {
  def render = {
    val start = now
    // sleep for up to 15 seconds
    Thread.sleep(randomLong(15 seconds))
    // send the result back
    "#start" #> start.toString &
    "#end" #> now.toString
  }
}
