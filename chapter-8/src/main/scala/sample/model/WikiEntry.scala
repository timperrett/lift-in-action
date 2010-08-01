package sample {
package model {

import net.liftweb.mapper.{LongKeyedMetaMapper,LongKeyedMapper,MappedString,MappedTextarea,IdPK}

object WikiEntry extends WikiEntry with LongKeyedMetaMapper[WikiEntry]

class WikiEntry extends LongKeyedMapper[WikiEntry] with IdPK {
  def getSingleton = WikiEntry
  
  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true 
  }
  
  object entry extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }
}

}}
