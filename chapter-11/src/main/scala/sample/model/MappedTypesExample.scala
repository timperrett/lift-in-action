package sample.model

// Generally speaking I wouldnt import the whole namespace, 
// but as it happens with mapper models you are pretty much using
// most of the types, and it becomes rather verbose to import
// more than 10 or 15 types.
import net.liftweb.mapper._

object MappedTypesExample extends MappedTypesExample with LongKeyedMetaMapper[MappedTypesExample]{
  override def dbTableName = "mapped_types_example"
}

class MappedTypesExample extends LongKeyedMapper[MappedTypesExample] with IdPK {
  def getSingleton = MappedTypesExample
  
  // @param Field owner 
  object mappedBinary extends MappedBinary(this)
  
  // @param Field owner 
  // @param minium age
  object mappedBirthYear extends MappedBirthYear(this, 18)
  
  // @param Field owner 
  object mappedBoolean extends MappedBoolean(this)
  
  // @param Field owner 
  object mappedCountry extends MappedCountry(this)
  
  // @param Field owner 
  object mappedDate extends MappedDate(this)
  
  // @param Field owner 
  object mappedDateTime extends MappedDateTime(this)
  
  import java.math.MathContext
  // @param Field owner 
  // @param Given math context instance to determine how to handle things like rounding ete
  // @param Decimal scale
  object mappedDecimal extends MappedDecimal(this, MathContext.DECIMAL128, 10)
  
  // @param Field owner 
  object mappedDouble extends MappedDouble(this)
  
  // @param Field owner 
  // @param Minimum length  
  object mappedEmail extends MappedEmail(this, 200)
  
  // @param Field owner 
  // @param An Enumeration of the possible values 
  object mappedEnum extends MappedEnum(this, Titles)
  
  // @param Field owner 
  object mappedEnumList extends MappedEnumList(this, Titles)
  
  // @param Field owner 
  object mappedFakeClob extends MappedFakeClob(this)
  
  // MappedGender is a subtype of MappedEnum and uses the gender
  // enumeration from net.liftweb.util.
  // @param Field owner 
  object mappedGender extends MappedGender(this)
  
  // @param Field owner 
  object mappedInt extends MappedInt(this)
  
  // @param Field owner 
  object mappedLocale extends MappedLocale(this)
  
  // @param Field owner 
  object mappedLong extends MappedLong(this)
  
  // @param Field owner 
  object mappedNullableLong extends MappedNullableLong(this)
  
  // @param Field owner 
  object mappedPassword extends MappedPassword(this)
  
  // Polite strings are regular MappedStrings that are truncated 
  // to fit in the column
  // @param Field owner 
  // @param Maximum length 
  object mappedPoliteString extends MappedPoliteString(this, 10)
  
  // @param Field owner 
  // @param The column that defines the country
  object mappedPostalCode extends MappedPostalCode(this, mappedCountry)
  
  // @param Field owner 
  // @param Maximum length of this string 
  object mappedString extends MappedString(this, 50)
  
  // @param Field owner 
  object mappedText extends MappedText(this)
  
  // @param Field owner 
  object mappedTextArea extends MappedTextarea(this, 200){
    override def textareaCols = 20
    override def textareaRows = 10
  }
  
  // @param Field owner 
  object mappedTime extends MappedTime(this)
  
  // @param Field owner 
  object mappedTimeZone extends MappedTimeZone(this)
  
  // @param Field owner 
  // @param Maximum length of the unique Id
  object mappedUniqueId extends MappedUniqueId(this, 35)
  
}