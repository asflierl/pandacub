package eu.flierl.pandacub

import org.specs2.mutable._
import org.junit.runner._
import org.specs2.runner._
import Show._

@RunWith(classOf[JUnitRunner])
class ShowSpec extends Specification {
  "Show" should { 
                                                                                         
    "sanitize property strings" in {
      sanitize("asd=(bl,,ub|b)") must be equalTo "asdblubb"
    }
    
    "show lists of bot opcodes" in {
      show(Say("abc") +: Log("huhu")) must be equalTo "Say(text=abc)|Log(text=huhu)"
    }
    
  }                                                                                
}