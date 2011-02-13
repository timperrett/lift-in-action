package example.travel {
package snippet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
  import net.liftweb.util.Helpers._
  import net.liftweb.http.{S,StatefulSnippet,SHtml}
  import net.liftweb.http.js.JsCmds.{Noop}
  import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
  import example.travel.model.{Auction,Bid,Customer}
  import example.travel.lib.AuctionInstanceHelpers
  import example.travel.comet.{AuctionServer,NewBid,CurrentAuction}
  
  class Details extends StatefulSnippet with AuctionInstanceHelpers with Loggable {
    
    val dispatch: DispatchIt = {
      case "show" => show _
      case "bid" => bid _
    }
    
    lazy val auction = Auction.find(By(Auction.id,S.param("id").map(_.toLong).openOr(0L)))
    
    def bid(xhtml: NodeSeq): NodeSeq = {
      var amountBox: Box[String] = Empty
      def submit = boxToNotice(
        "Your bid was accepted!",
        "Unable to place bid at this time."){
          (for(a <- auction; v <- a.barter(amountBox)) yield v).pass(box => 
            if(!box.isEmpty)
              AuctionServer ! NewBid(auction.map(_.id.is).openOr(0L), 
                    amountBox.openOr("0").toDouble, S.session.map(_.uniqueId)))
        }
      SHtml.ajaxForm(bind("b",xhtml,
        "amount" -%> SHtml.text(amountBox.openOr(""), s => amountBox = Box.!!(s)) % ("id" -> "amount"),
        "submit" -> SHtml.ajaxSubmit("Place Bid", { () => submit; Noop })
      ))
    }
    
    def show(xhtml: NodeSeq): NodeSeq = {
      println("Looking for comet: " + S.session.map(_.findComet("AuctionUpdater")).openOr(Nil).toString)
      
      S.session.map(_.findComet("AuctionUpdater")).openOr(Nil).foreach(_ ! CurrentAuction(auction))
      auction.map(a => 
        bind("a", single(a, xhtml),
          "current_amount" -> <span>{leadingBid.toString}</span> % ("id" -> "current_amount"),
          "next_amount" -> <span>{minimumBid.toString}</span> % ("id" -> "next_amount")
        )).openOr(Text("That auction does not exist"))
    }
    
  }
  
}}