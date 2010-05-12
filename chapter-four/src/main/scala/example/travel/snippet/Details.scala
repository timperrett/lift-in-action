package example.travel {
package snippet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
  import net.liftweb.util.Helpers._
  import net.liftweb.http.{S,StatefulSnippet,SHtml}
  import net.liftweb.http.js.JsCmds.{Noop}
  import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
  import example.travel.model.{Auction,Bid,Customer}
  import example.travel.lib.AuctionHelpers
  import example.travel.comet.{AuctionServer,AuctionValueChange}
  
  class Details extends StatefulSnippet with AuctionHelpers with Loggable {
    val dispatch: DispatchIt =  {
      case "show" => show _
      case "bid" => bid _
    }
    
    val auction = currentAuction
    
    def bid(xhtml: NodeSeq): NodeSeq = if(!Customer.loggedIn_?){
      S.warning("You must be logged in to bid on auctions.")
      NodeSeq.Empty
    } else {
      var amountBox: Box[String] = Empty
      def submit = boxToNotice(
        "Your bid was accepted!",
        "Unable to place bid at this time."){
          (for(a <- auction; v <- a.barter(amountBox)) yield v).pass(box => 
            if(!box.isEmpty)
              AuctionServer ! AuctionValueChange(amountBox.openOr("0").toDouble))
        } 
      SHtml.ajaxForm(bind("b",xhtml,
        "amount" -%> SHtml.text(amountBox.openOr(""), s => amountBox = Box.!!(s)) % ("id" -> "amount"),
        "submit" -> SHtml.ajaxSubmit("Place Bid", { () => submit; Noop })
      ))
    }
    
    def show(xhtml: NodeSeq): NodeSeq = auction.map(a => 
      bind("a", single(a, xhtml),
        "current_amount" -> <span>{a.currentAmount.toString}</span> % ("id" -> "current_amount"),
        "next_amount" -> <span>{a.nextAmount.toString}</span> % ("id" -> "next_amount")
      )).openOr(Text("That auction does not exist"))
    
  }
  
}}