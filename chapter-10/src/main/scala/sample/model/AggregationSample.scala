package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object AggregationSample extends AggregationSample with LongKeyedMetaMapper[AggregationSample]

class AggregationSample extends LongKeyedMapper[AggregationSample] with IdPK {
  def getSingleton = AggregationSample
  
  object name extends MappedSplitName(this, 100)
}

