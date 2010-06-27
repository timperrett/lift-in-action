package example.travel {
package model {
  
  import net.liftweb.common.{Loggable,Full,Empty}
  import net.liftweb.machine.{ProtoStateMachine,MetaProtoStateMachine}
  import net.liftweb.mapper.MappedLongForeignKey
  import net.liftweb.util.Helpers._
  
  object AuctionStates extends Enumeration {
    val Initial, Active, LastFiveMins, Expired = Value
  }
  
  object AuctionMachine extends AuctionMachine with MetaProtoStateMachine[AuctionMachine, AuctionStates.type]{
    
    println("TOUCHED!")
    // override def timedEventInitialWait = 10000L
    def instantiate = new AuctionMachine
    val stateEnumeration = AuctionStates
    def initialState = AuctionStates.Initial
    def globalTransitions = Nil
    def states = List(
      State(AuctionStates.Initial, On({case _ => }, AuctionStates.Active))
      // State(AuctionStates.Initial, After(2 minutes, AuctionStates.Active)),
    )
    
    case object FirstEvent extends Event
  }
  
  class AuctionMachine extends ProtoStateMachine[AuctionMachine, AuctionStates.type]{
    def getSingleton = AuctionMachine
    
    object auction extends MappedLongForeignKey(this, Auction){
      override def dbColumnName = "auction_id"
    }
    
    override def transition(from: AuctionStates.Value, to: StV, why: Meta#Event){
      println("AuctionMachine.transition() from= " + from + " to= " + to + " why= " + why + " for auction " + auction)
      (from, to, auction.obj) match {
        case (AuctionStates.Initial, AuctionStates.Active, _) =>
          println("Initial to Active")
        case (AuctionStates.Active, AuctionStates.Expired, Full(a)) =>
          println("Active to Expired!")
        case (from,to,why) => println("default transition, don't do anything")
      }
      super.transition(from, to, why)
    }
    
  }
  
  
}}