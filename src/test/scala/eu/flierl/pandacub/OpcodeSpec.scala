package eu.flierl.pandacub

import org.specs2.mutable._
import org.junit.runner._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class OpcodeSpec extends Specification {
  "Opcodes" should { 
                                                                                         
    "be appendable" in {
      (Status("bli") +: Log("blubb")) must be equalTo List(Status("bli"), Log("blubb")) 
    }
    
  } 
}