package sample.snippet

import net.liftweb.http.S
import net.liftweb.wizard.Wizard

object PetSurveyWizard extends Wizard {
  object completeInfo extends WizardVar(false)
  
  val you = new Screen {
    val yourName = field("First Name", "",
      valMinLen(2, "Name Too Short"),
      valMaxLen(40, "Name Too Long"))
    
    val yourAge = field("Age", 1, 
      minVal(5, "Too young"),
      maxVal(125, "You should be dead"))
    
    override def nextScreen = if (yourAge.is < 18) parents else pets
  }
  
  val parents = new Screen {
    val parentName = field("Parent or Guardian's name", "",
      valMinLen(2, "Name Too Short"),
      valMaxLen(40, "Name Too Long"))
  }
  
  val pets = new Screen {
    val pet = field("Pet's name", "",
      valMinLen(2, "Name Too Short"),
      valMaxLen(40, "Name Too Long"))
  }
  
  def finish() {
    S.notice("Thank you for registering your pet")
    completeInfo.set(true)
  }
}
