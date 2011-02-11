package sample.comet

import akka.actor.Actor

sealed case class Compute(thiz: Double, that: Double, by: String)

class Calculator extends Actor {
  def receive = {
    case Compute(a,b,by) => {
      val result = by match {
        case "*" => a * b
        case "+" => a + b
        case "/" => a / b
        case _ => println("Unknown type of maths fool!"); 0D
      }
      self.reply(result)
    }
  }
}

import scala.xml.{NodeSeq,Text}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.Helpers._
import net.liftweb.http.{CometActor,SHtml}
import net.liftweb.http.js.JsCmds.{SetHtml,Noop}
import akka.actor.Actor.registry
import akka.dispatch.Future

class CalculatorDisplay extends CometActor {
  private var one, two = 0D
  private var operation: Box[String] = Empty
  
  def doubleInput(f: Double => Any) = 
    SHtml.text("0.0", v => f(asDouble(v).openOr(0D)))
  
  def render = 
    "#value_one" #> doubleInput(one = _) &
    "#value_two" #> doubleInput(two = _) &
    "#operation" #> SHtml.select(Seq("+","/","*").map(x => (x -> x)), 
      operation, v => operation = Full(v)) &
    "type=submit" #> SHtml.ajaxSubmit("Submit", () => {
      val future: Future[Double] = 
        registry.actorFor[Calculator].get !!! 
          Compute(one,two,operation.openOr("+"))
      future.onComplete(f => this ! f.result)
      Noop
    }) andThen SHtml.makeFormsAjax
  
  override def lowPriority = {
    case Some(value: Double) => 
      partialUpdate(SetHtml("result", Text(value.toString)))
  }
}


