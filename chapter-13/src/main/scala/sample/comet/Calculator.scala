package sample.comet

import akka.actor.Actor
import akka.actor.Actor._

// incoming messages
// sealed trait Operation {
//   val sign: String
// }
// object Operation {
//   def fromSign(sign: String) = 
// }
// case object Multiply extends Operation {
//   val sign = "*"
// }
// case object Add extends Operation {
//   val sign = "+"
// }
// case object Divide extends Operation {
//   val sign = "/"
// }
sealed case class Compute(thiz: Double, that: Double, by: String)

// response
sealed case class Result(value: Double)

class Calculator extends Actor {
  def receive = {
    case Compute(a,b,by) => by match {
      case "*" => a * b
      case "+" => a + b
      case "/" => a / b
    }
  }
}

import scala.xml.{NodeSeq,Text}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers._
import net.liftweb.http.{CometActor,SHtml}
import net.liftweb.http.js.JsCmds.SetHtml
import akka.actor.Actor.registry

class CalculatorDisplay extends CometActor {
  private var one = 0D
  private var two = 0D
  private var operation: Box[String] = Empty
  
  def render = {
    "#value_one" #> SHtml.text(one.toString, a => one = asDouble(a).openOr(0D)) & 
    "form" #>((ns: NodeSeq) => SHtml.ajaxForm(ns)) //&
    // "#value_one" #> SHtml.text(one.toString, a => one = asDouble(a).openOr(0D)) & 
    // "#value_two" #> SHtml.text(two.toString, b => two = asDouble(b).openOr(0D)) //&
    // "name=value_operation" #> SHtml.select(
    //   Seq("+","-","*").map(x => (x -> x)), 
    //   operation, v => operation = Full(v)) &
    // "type=submit" #> SHtml.submit("Calculate!", () => {
    //   println("vals: %s, %s, %s".format(one,two,operation))
    //   val future = registry.actorFor[Calculator].get !!! Compute(one,two,operation.openOr("+"))
    //   future.onComplete(f => this ! f.result)
    // })
  }
  
    
//       

  //"#clock_time *" replaceWith timeNow.toString
  
  override def lowPriority = {
    case Result(value) => println(value)
      // partialUpdate(SetHtml("clock_time", Text(timeNow.toString)))
  }
  
  //
  
}


