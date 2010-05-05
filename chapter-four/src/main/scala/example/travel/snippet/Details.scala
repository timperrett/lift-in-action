package example.travel {
package snippet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
  import net.liftweb.util.Helpers._
  import net.liftweb.http.{S,StatefulSnippet,SHtml}
  import net.liftweb.http.js.JsCmd 
  import net.liftweb.http.js.JsCmds.{Noop,SetValueAndFocus,Alert}
  import net.liftweb.http.js.jquery.JqJsCmds.{DisplayMessage,Show}
  import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
  import example.travel.model.{Auction,Bid,Customer}
  
  class Details extends StatefulSnippet with AuctionHelpers with Loggable {
    val dispatch: DispatchIt =  {
      case "show" => show _
      case "bid" => bid _
    }
    
    val auction = Auction.find(By(Auction.id,S.param("id").map(_.toLong).openOr(0L)))
    
    def show(xhtml: NodeSeq): NodeSeq = auction.map(a => 
      bind("a", single(a, xhtml),
        "countdown" -> <span class="countdown">1:27:22</span>,
        "current_amount" -> a.currentAmount.toString,
        "next_amount" -> a.nextAmount.toString
      )).openOr(Text("That auction does not exist"))
    
    def bid(xhtml: NodeSeq): NodeSeq = {
      var amountBox: Box[String] = Empty
      def submit = {
        for(ass <- amountBox ?~ "Amount not entered";
            amo <- tryo(BigDecimal(ass).doubleValue) ?~! "Amount not a number";
            auc <- auction;
            vld <- tryo(amo).filter(_ >= auc.nextAmount) ?~ "Less that required amount!"
        ) yield {
          new Bid().auction(auction).customer(Customer.currentUser).amount(amo).save
        }
      } match {
        case Full(x) => S.notice("Great, your bid was accepted!")
        case Failure(msg, _, _) => S.error(msg)
        case _ => S.warning("Unable to place bid at this time.") 
      }
      if(Customer.loggedIn_?){
        SHtml.ajaxForm(bind("b",xhtml,
          "amount" -%> SHtml.text(amountBox.openOr(""), s => amountBox = Full(s)) % ("id" -> "amount"),
          "submit" -> SHtml.ajaxSubmit("Place Bid", () => { submit; Noop })
        ))
      } else {
        S.warning("You must be logged in to bid on auctions.")
        NodeSeq.Empty
      } 
    }
    
  }
  
}}