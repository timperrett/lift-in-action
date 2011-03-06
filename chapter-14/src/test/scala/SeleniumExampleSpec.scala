package sample.test

import org.specs._

trait SeleniumExampleSpec { _: Specification with SeleniumSetupAndTearDown => 
  //extends Specification with SeleniumSetupAndTearDown {
  "/testkit/ajax" should {
    import SeleniumTestClient._
    "replace the button with text when clicked" in {
      browser.open("/testkit/ajax")
      browser.click("clickme")
      browser.waitForCondition("""
        selenium.browserbot
        .getCurrentWindow().document
        .getElementById('ajax_button')
        .innerHTML == 'Clicked'""", 
        "1000")
      browser.isTextPresent("Clicked") mustBe true
    }
  }
}
