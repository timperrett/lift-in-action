package sample.comet

import scala.xml.Text
import net.liftweb.util.ActorPing
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.liftweb.http.js.JsCmds.SetHtml

case object Tick

class Clock extends CometActor {
  
  ActorPing.schedule(this, Tick, 5 seconds)
  
  def render = "#clock_time *" replaceWith timeNow.toString
  
  override def lowPriority = {
    case Tick =>
      partialUpdate(SetHtml("clock_time", Text(timeNow.toString)))
      ActorPing.schedule(this, Tick, 5 seconds)
  }
}