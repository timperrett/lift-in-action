package example.travel.snippet

import net.liftweb.util.Props

class HelloWorld {
  def howdy = <span>Welcome to lift-travel at {new _root_.java.util.Date}. Mode is {Props.mode}</span>
}

