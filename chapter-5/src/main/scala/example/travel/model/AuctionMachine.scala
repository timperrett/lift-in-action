package example.travel
package model

import net.liftweb.common.{Loggable,Full,Empty}
import net.liftweb.machine.{ProtoStateMachine,MetaProtoStateMachine}
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.util.Helpers._

/**
 * The enumeration of all possible states. In this instance, 
 * we want to keep it simple so only have three states.
 */
object AuctionStates extends Enumeration {
  val Initial, Active, Expired = Value
}

/**
 * The companion object for the machine table.
 * The real method of importants here is the states list that defines
 * the story of how states knit together and the actions that are to
 * to be executed for each transistion. 
 */
object AuctionMachine extends AuctionMachine with MetaProtoStateMachine[AuctionMachine, AuctionStates.type]{
  def instantiate = new AuctionMachine
  val stateEnumeration = AuctionStates
  def initialState = AuctionStates.Initial
  def globalTransitions = Nil
  def states = List(
    State(AuctionStates.Initial, On({case _ => }, AuctionStates.Active)),
    State(AuctionStates.Active, After(Auction.duration, AuctionStates.Expired))
  )
  case object FirstEvent extends Event
}

/** 
 * Instance defintion of the machine. The important part here is the 
 * transistion method that lets us specify code to execute 
 */
class AuctionMachine extends ProtoStateMachine[AuctionMachine, AuctionStates.type]{
  def getSingleton = AuctionMachine
  
  object auction extends MappedLongForeignKey(this, Auction){
    override def dbColumnName = "auction_id"
  }
  
  override def transition(from: AuctionStates.Value, to: StV, why: Meta#Event){
    // println("AuctionMachine.transition() from= " + from + " to= " + to + " why= " + why + " for auction " + auction)
    (from, to, auction.obj) match {
      case (AuctionStates.Initial, AuctionStates.Active, _) =>
        // println("Initial to Active")
      case (AuctionStates.Active, AuctionStates.Expired, Full(auc)) => {
        auc.attributeToWinningCustomer
        auc.close
      }
      case (from,to,why) =>
    }
    super.transition(from, to, why)
  }
}
