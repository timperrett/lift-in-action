package example.travel {
package snippet {
  
  import example.travel.model.Auction
  import scala.xml.{NodeSeq,Text}
  import net.liftweb.util.Helpers._
  import net.liftweb.http.{S,DispatchSnippet}
  import net.liftweb.mapper.{MaxRows,By,OrderBy,Descending,StartAt}
  import net.liftweb.mapper.view.{MapperPaginatorSnippet}
  
  class Listings extends DispatchSnippet with AuctionHelpers {
    override def dispatch = {
      case "all" => all _
      case "top" => top _ 
      case "paginate" => paginator.paginate _
    }
    
    val paginator = new MapperPaginatorSnippet(Auction){
      override def itemsPerPage = 5
      constantParams = OrderBy(Auction.id, Descending) :: Nil
    }
    
    def all(xhtml: NodeSeq): NodeSeq = many(paginator.page, xhtml)
    
    def top(xhtml: NodeSeq): NodeSeq = 
      many(Auction.findAll(MaxRows(3), OrderBy(Auction.id, Descending)), xhtml)
  }
  
}}