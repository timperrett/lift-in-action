package sample.test

import net.liftweb.http.LiftRules

trait SetupAndTearDown {
  def construcEnvironment() = 
    new bootstrap.liftweb.Boot().boot
  def tearDownEnvironment() = 
    LiftRules.unloadHooks.toList.foreach(_())
}