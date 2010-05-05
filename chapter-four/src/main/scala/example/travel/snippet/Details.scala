package example.travel {
package snippet {
  
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.common.{Full,Box,Empty,Failure,Loggable}
  import net.liftweb.util.Helpers._
  import net.liftweb.http.{S,StatefulSnippet,SHtml}
  import net.liftweb.http.js.JsCmd 
  import net.liftweb.http.js.JsCmds.{Noop,SetHtml,SetValueAndFocus,Alert}
  import net.liftweb.http.js.jquery.JqJsCmds.{DisplayMessage,Show}
  import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
  import example.travel.model.{Auction,Bid,Customer}
  
  class Details extends StatefulSnippet with AuctionHelpers with Loggable {
    val dispatch: DispatchIt =  {
      case "show" => show _
      case "bid" => bid _
    }
    
    val currentAmountSpan = "current_amount"
    val nextAmountSpan = "next_amount"
    val amountInputId = "amount"
    
    val auction = Auction.find(By(Auction.id,S.param("id").map(_.toLong).openOr(0L)))
    
    
    def boxToNoticeWithJsCmd[T](win: (String, JsCmd), loose: (String,JsCmd))(f: => Box[T]): JsCmd = {
        f match {
           case Full(value) => S.notice(win._1); win._2
           case Failure(msg,_,_) => S.error(msg); Noop
           case _ => S.warning(loose._1); win._2
         }
       }
    
    def bid(xhtml: NodeSeq): NodeSeq = if(!Customer.loggedIn_?){
      S.warning("You must be logged in to bid on auctions.")
      NodeSeq.Empty
    } else {
      var amountBox: Box[String] = Empty
      def submit = boxToNoticeWithJsCmd(
        ("Your bid was accepted!", 
          SetHtml(currentAmountSpan, Text(amountBox.openOr("n/a"))) & 
          SetValueAndFocus(amountInputId, "")
        ),
        ("Unable to place bid at this time.", SetValueAndFocus(amountInputId, ""))){
          logger.debug(amountBox)
          auction.open_!.barter(amountBox)
        }
      SHtml.ajaxForm(bind("b",xhtml,
        "amount" -%> SHtml.text(amountBox.openOr(""), s => amountBox = Box.!!(s)) % ("id" -> amountInputId),
        "submit" -> SHtml.ajaxSubmit("Place Bid", submit _)
      ))
    }
    
    // def bid(xhtml: NodeSeq): NodeSeq = 
    //   if(!Customer.loggedIn_?){
    //     S.warning("You must be logged in to bid on auctions.")
    //     NodeSeq.Empty
    //   } else {
    //     var amountBox: Box[String] = Empty
    //     def submit = 
    //       boxToNoticeWithJsCmd(
    //         "Your bid was accepted!", 
    //         "Unable to place bid at this time."){
    //         for(ass <- amountBox ?~! "Amount is not a number";
    //           amo <- tryo(BigDecimal(ass).doubleValue) ?~! "Amount is not a number";
    //           auc <- auction;
    //           vld <- tryo(amo).filter(_ >= auc.nextAmount) ?~ "Your bid is lower than required!"
    //         ) yield (
    //           new Bid().auction(auction).customer(Customer.currentUser).amount(amo).saveMe
    //         )
    //       }
        // SHtml.ajaxForm(bind("b",xhtml,
        //   "amount" -%> SHtml.text(amountBox.openOr(""), s => amountBox = Full(s)) % ("id" -> amountInputId),
        //   "submit" -> SHtml.ajaxSubmit("Place Bid", {() => submit; Noop})
        // ), 
        // Noop,
        // SetHtml(currentAmountSpan, Text(amountBox.openOr("n/a"))) & 
        // SetHtml(nextAmountSpan, Text(auction.map(_.nextAmount.toString).openOr("n/a"))) & 
        // SetValueAndFocus(amountInputId, ""))
    //   }
    
    def show(xhtml: NodeSeq): NodeSeq = auction.map(a => 
      bind("a", single(a, xhtml),
        "countdown" -> <span class="countdown">1:27:22</span>,
        "current_amount" -> <span>{a.currentAmount.toString}</span> % ("id" -> currentAmountSpan),
        "next_amount" -> <span>{a.nextAmount.toString}</span> % ("id" -> nextAmountSpan)
      )).openOr(Text("That auction does not exist"))
    
  }
  
}}