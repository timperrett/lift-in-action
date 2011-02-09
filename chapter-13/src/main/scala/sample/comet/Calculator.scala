package sample.comet

import akka.actor.Actor

// incoming messages
sealed trait Operation
case object Multiply extends Operation
case object Add extends Operation
case object Divide extends Operation
sealed case class Compute(thiz: Double, that: Double, by: Operation)

// response
sealed case class Result(value: Double)

class Calculator extends Actor {
  def receive = {
    case Compute(a,b,by) => by match {
      case Multiply => a * b
      case Add => a + b
      case Divide => a / b
    }
  }
}

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.{CometActor,SHtml}
import net.liftweb.http.js.JsCmds.SetHtml

class CalculatorDisplay extends CometActor {
  private var one = 0D
  private var two = 0D
  
  def render = 
    "#value_one" #> SHtml.text(one.toString, a => one = asDouble(a).openOr(0D)) & 
    "#value_two" #> SHtml.text(two.toString, b => two = asDouble(b).openOr(0D)) &
    "select" #> SHtml.select()
    "type=submit" #> SHtml.submit("Calculate!", () => {})
  
  //"#clock_time *" replaceWith timeNow.toString
  
  override def lowPriority = {
    case Result(value) =>
      partialUpdate(SetHtml("clock_time", Text(timeNow.toString)))
  }
  
  //(Actor.actorFor[Calculator] !!! Compute(a,b,o)).onComplete(f => this ! f.result)
  
}


