package example.travel {
package snippet {
  
  import example.travel.model.Auction
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.util.Helpers._
  import net.liftweb.mapper.{MaxRows,OrderBy,Descending}
  
  class Auctions {
    
    // def all(xhtml: NodeSeq): NodeSeq = {
    //   list()
    // }
    
    def top(xhtml: NodeSeq): NodeSeq = 
      list(Auction.findAll(MaxRows(3), OrderBy(Auction.id, Descending)), xhtml)
    
    private def list(items: List[Auction], xhtml: NodeSeq): NodeSeq = items.flatMap(auction => 
      bind("a", xhtml,
        "name" -> Text(auction.name),
        "description" -> Text(auction.description)
      )
    )
  }
  
}}