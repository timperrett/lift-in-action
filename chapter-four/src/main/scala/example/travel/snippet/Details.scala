package example.travel {
package snippet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Box,Empty,Failure}
  import net.liftweb.util.Helpers._
  import net.liftweb.http.{S,StatefulSnippet,SHtml}
  import net.liftweb.http.js.JsCmd 
  import net.liftweb.http.js.JsCmds.{Noop,SetValueAndFocus,Alert}
  import net.liftweb.http.js.jquery.JqJsCmds.{DisplayMessage,Show}
  import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
  import example.travel.model.{Auction,Bid,Customer}
  
  class Details extends StatefulSnippet {
    val dispatch: DispatchIt =  {
      case "show" => show _
      case "bid" => bid _
    }
    private val messageDiv = "messages"
    private val amountInput = "amount"
    val auction = Auction.find(By(Auction.id,S.param("id").map(_.toLong).openOr(0L)))
    
    def show(xhtml: NodeSeq): NodeSeq = auction.map(a => 
      bind("a", xhtml,
        "name" -> a.name,
        "description" -> a.description,
        "countdown" -> <span class="countdown">1:27:22</span>,
        "highest_bid" -> a.highestBid.map(_.amount.toString).openOr("£0.00")
      )).openOr(Text("That auction does not exist"))
    
    // ajax bidding
    def bid(xhtml: NodeSeq): NodeSeq = {
      var amountBox: Box[String] = Empty
      def submit = {
        for {
          amountString <- amountBox ?~ "Amount not entered"
          amount <- tryo(BigDecimal(amountString).longValue) ?~! "Amount not a number"
        } yield {
          new Bid()
            .auction(auction)
            .customer(Customer.currentUser)
            .amount(amount)
            .save
          message(messageDiv, "Completed")
        }
      } match {
        case Full(js) => js
        case Failure(msg, _, _) => message(messageDiv, msg)
        case _ => message(messageDiv, "Unable to place bid at this time.")
      }
      
      SHtml.ajaxForm(bind("b",xhtml,
        "amount" -%> SHtml.text(amountBox.openOr(""), s => amountBox = Full(s)) % ("id" -> "amount"),
        "submit" -> SHtml.ajaxSubmit("Place Bid", submit _)
      ))
    }
    
    private def message(id: String, msg: String): JsCmd =
      Show(messageDiv, 1 seconds) & DisplayMessage(id, Text(msg), 2 seconds, 2 seconds)
    
  }
  
}}