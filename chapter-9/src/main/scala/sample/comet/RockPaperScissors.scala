package sample.comet

import scala.xml.Text
import scala.collection.mutable.Map
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.actor.LiftActor
import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import net.liftweb.http.{CometActor,SHtml}
import net.liftweb.http.js.JsCmds.{SetHtml,Run}

sealed trait Move
final case object Rock extends Move
final case object Paper extends Move
final case object Scissors extends Move

sealed trait Outcome
final case object Tie
final case class Winner(is: CometActor)

final case class AddPlayer(who: CometActor)
final case class RemovePlayer(who: CometActor)
final case object PairPlayersInLobby
final case class NowPlaying(game: Game)
final case class Make(move: Move, from: CometActor)
final case object HurryUpAndMakeYourMove
final case object ResetGame
final case object LeaveGame
final case object Adjudicate

object Lobby extends LiftActor {
  private var games: List[Game] = Nil
  private var lobby: List[CometActor] = Nil
  
  def messageHandler = {
    case PairPlayersInLobby => {
      for(i <- 0 until (lobby.size / 2)){
        val players = lobby.take(2)
        val game = new Game(players.head, players.last)
        games ::= game
        players.foreach(_ ! NowPlaying(game))
        lobby = lobby diff players
      }
    }
    case AddPlayer(who) => 
      lobby ::= who
      this ! PairPlayersInLobby
    case RemovePlayer(who) =>
      lobby = lobby.filter(_ ne who)
  }
  
}


class Game(playerOne: CometActor, playerTwo: CometActor) extends LiftActor {
  private var moves: Map[CometActor, Box[Move]] = Map()
  clearMoves()
  
  private def sendToAllPlayers(msg: Any){
    moves.foreach(_._1 ! msg)
  }
  
  private def clearMoves() {
    moves = Map(playerOne -> Empty, playerTwo -> Empty)
  }
  
  def messageHandler = {
    case Adjudicate => {
      val p1move = moves(playerOne)
      val p2move = moves(playerTwo)
      if(p1move == p2move)
        sendToAllPlayers(Tie)
      else {
        (p1move, p2move) match {
          case (Full(Rock), Full(Scissors)) | 
               (Full(Paper), Full(Rock)) | 
               (Full(Scissors), Full(Paper)) => 
            sendToAllPlayers(Winner(playerOne))
          case _ => 
            // playerOne didnt win, and its not a tie, so playerTwo must have won
            sendToAllPlayers(Winner(playerTwo))
        }
      }
      Schedule.schedule(this, ResetGame, 5 seconds)
    }
      
    case Make(move, from) => {
      moves.update(from,Full(move))
      if(moves.flatMap(_._2).size == 2){
        this ! Adjudicate
      } else {
        // one of the players hasnt made their move,
        // prompt the other one to do something
        moves.filter(_._1 ne from).head._1 ! HurryUpAndMakeYourMove
      } 
    }
    case ResetGame => 
      clearMoves()
      sendToAllPlayers(ResetGame)
      
    case LeaveGame => 
      // one player left, you cant play on your own so 
      // both players are sent back to the lobby
      
  }
}

class RockPaperScissors extends CometActor {
  
  private var nickName = ""
  private var game: Box[Game] = Empty
  
  private def showInformation(msg: String) = 
    partialUpdate(SetHtml("information", Text(msg)))
  
  override def mediumPriority = {
    case NowPlaying(g) => 
      game = Full(g)
      reRender(true)
    case HurryUpAndMakeYourMove =>
      showInformation("Hurry up! Your opponent has already made their move!")
    case Tie =>
      showInformation("Damn, it was a tie!")
    case Winner(who) =>
      if(who eq this)
        showInformation("You are the WINNER!!!")
      else
        showInformation("Better luck next time, loser!")
    case ResetGame => 
      reRender(true)
  }
  
  def render = 
    if(!game.isEmpty)
      "#information *" #> Text("Now you're playing! Make your move...") &
      ".line" #> List(Rock, Paper, Scissors).map(move => 
        SHtml.ajaxButton(Text(move.toString), () => {
          game.foreach(_ ! Make(move, this))
          Run("$('button').attr('disabled',true);")
        }))
    else 
      "#game *" replaceWith "Waiting in the lobby for an opponent..."
  
  override def lifespan: Box[TimeSpan] = Full(2 minutes)
  
  override def localSetup(){
    askUserForNickname
    super.localSetup()  
  }
  override def localShutdown() {
    Lobby ! RemovePlayer(this)
    super.localShutdown()
  }
  
  private def askUserForNickname {
    if (nickName.length == 0){
      ask(new AskName, "What's your nickname?"){
        case s: String if (s.trim.length > 2) =>
          nickName = s.trim
          Lobby ! AddPlayer(this)
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
