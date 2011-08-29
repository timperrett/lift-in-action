package sample.actor

import akka.actor.Actor

class HelloWorldActor extends Actor {
  def receive = {
    case "Hello" => println("Message Recived!")
  }
}

object HelloWorldRemoteCaller {
  import akka.actor.Actor.remote
  private val actor = remote.actorFor("hello-service", "localhost", 2552)
  def welcome = actor ! "Hello"
}
