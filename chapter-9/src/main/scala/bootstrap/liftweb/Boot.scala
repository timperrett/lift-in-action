package bootstrap.liftweb

import net.liftweb.http.{LiftRules,RewriteRequest,RewriteResponse,ParsePath}
import sample.lib.BasicDispatchUsage

class Boot {
  def boot {
    LiftRules.addToPackages("sample.snippet")
    
    
    // listing 9.1
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath("category" :: cid :: "product" :: pid :: Nil,"",true,_),_,_) =>
           RewriteResponse("product" :: Nil, Map("pid" -> pid))
    }
    
    // listing 9.2
    
    
    LiftRules.dispatch.append(BasicDispatchUsage)
    
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