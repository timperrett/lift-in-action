package sample.actor

import akka.actor.Actor

class IntTransformer extends Actor {
  def receive = {
    case (in: String) => println(in.toInt)
  }
}

object IntTransformerRemoteCaller {
  import akka.actor.Actor.remote
  private val actor = remote.actorFor("int-service", "localhost", 2552)
  def send(msg: String) = actor ! msg
}