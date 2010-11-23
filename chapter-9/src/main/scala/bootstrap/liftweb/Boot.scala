package bootstrap.liftweb

import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.http.{LiftRules,RewriteRequest,RewriteResponse,ParsePath,Req,GetRequest}
import sample.lib.{BasicDispatchUsage,SecondDispatchUsage,BookshopService}

class Boot {
  def boot {
    LiftRules.addToPackages("sample.snippet")
    
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    // listing 9.1
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath("category" :: cid :: "product" :: pid :: Nil,"",true,_),_,_) =>
           RewriteResponse("product" :: Nil, Map("pid" -> pid))
    }
    
    // listing 9.2
    LiftRules.dispatch.append(BasicDispatchUsage)
    
    // listing 9.3
    LiftRules.dispatch.append(SecondDispatchUsage)
    
    LiftRules.urlDecorate.prepend {
      case url => if(url.contains("?")) url + "&srv_id=001" else "?srv_id=001"
    }
    
    LiftRules.liftRequest.append {
      case Req("nolift" :: Nil,"xml",_) => false
    }
    
    LiftRules.dispatch.append {
      case Req("bookshop" :: "books" :: Nil, "xml", GetRequest) => 
        BookshopService.xml.list
      case Req("bookshop" :: "books" :: publisher :: Nil, "xml", GetRequest) => 
        BookshopService.xml.listByPublisher(publisher)
    }
  }
}


// object ParamsExtractor { 
//   def unapply(pp: ParsePath): Option[(Account, OrgUnit)] = { 
//     val result:Box[(Account, OrgUnit)] = if (pp.wholePath.startsWith(path) && pp.wholePath.length == (path.length + 2)){ 
//       val res = Full((XX,YY)) 
//       debug("Decoded URL: %s=%s".format(pp,res)) 
//       res 
//     } else None 
//     result 
//   } 
// }