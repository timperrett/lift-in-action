package example.travel {
package lib {
  
  import net.liftweb.machine.{ProtoStateMachine,MetaProtoStateMachine}
  import net.liftweb.util.Helpers._
  import example.travel.model.Auction
  
  object AuctionStates extends Enumeration {
    val Initial, Active, LastFiveMins, Expired = Value
  }
  
  object AuctionMachine extends AuctionMachine with MetaProtoStateMachine[AuctionMachine, AuctionStates.type]{
    def instantiate = new AuctionMachine
    val stateEnumeration = AuctionStates
    def initialState = AuctionStates.Initial
    def globalTransitions = Nil
    def states = State(AuctionStates.Initial, After(2 minutes, AuctionStates.Active)) :: Nil
  }
  
  class AuctionMachine extends ProtoStateMachine[AuctionMachine, AuctionStates.type]{
    def getSingleton = AuctionMachine
  }
  
  
}}