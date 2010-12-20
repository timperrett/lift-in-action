package sample.comet

import scala.xml.Text
import net.liftweb.actor.LiftActor
import net.liftweb.util.ActorPing
import net.liftweb.util.Helpers._
import net.liftweb.http.{CometActor,SHtml}
import net.liftweb.http.js.JsCmds.SetHtml

trait Move
final case object Rock extends Move
final case object Paper extends Move
final case object Scissors extends Move

final case object WaitingOnOpponent

final case class Game(playerOne: CometActor, playerTwo: CometActor)
case object StartGame

case class AddPlayer(who: CometActor)
case class RemovePlayer(who: CometActor)
case object PairPlayersInLobby

object GameServer extends LiftActor {
  private var games: List[Game] = Nil
  private var lobby: List[CometActor] = Nil
  
  // ActorPing.schedule(this, PairPlayersInLobby, 5 seconds)
  
  def messageHandler = {      
    case PairPlayersInLobby => {
      println("before: " + lobby)
      for(i <- 0 until (lobby.size / 2)){
        val players = lobby.take(2)
        val game = Game(players.head, players.last)
        players.foreach(_ ! StartGame)
        games ::= game
        lobby = lobby diff players
      }
      println("after: " + lobby)
    }
    case AddPlayer(who) => 
      lobby ::= who
      this ! PairPlayersInLobby
    case RemovePlayer(who) =>
      lobby = lobby.filter(_ ne who)
  }
  
}

class RockPaperScissors extends CometActor {
  
  private var nickName = ""
  private var inGame_? = false
  
  override def mediumPriority = {
    case StartGame => 
      inGame_? = true
      partialUpdate(SetHtml("information", Text("Starting the game, one moment " + nickName)))
    case WaitingOnOpponent =>  
      
  }
  
  def render = 
    if(inGame_?)
      "#information *" replaceWith "You're playing!"
    else 
      "#information *" replaceWith "Waiting in the lobby for an opponent..."
  
  def registerWith = GameServer
  
  override def localSetup(){
    askUserForNickname
    super.localSetup()  
  }
  override def localShutdown() {
    registerWith ! RemovePlayer(this)
    super.localShutdown()
  }
  
  private def askUserForNickname {
    if (nickName.length == 0){
      ask(new AskName, "What's your nickname?"){
        case s: String if (s.trim.length > 2) =>
          nickName = s.trim
          registerWith ! AddPlayer(this)
          reRender(true)
        case _ =>
          askUserForNickname
          reRender(false)
      }
    }
  }
}

class AskName extends CometActor {
  def render = SHtml.ajaxForm(
    <p>What is your player nickname? <br />{
      SHtml.text("",n => answer(n.trim))}</p> ++ 
      <input type="submit" value="Enter Lobby"/>)
}
