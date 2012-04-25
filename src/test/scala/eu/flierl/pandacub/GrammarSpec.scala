package eu.flierl.pandacub

import org.specs2.mutable._
import org.junit.runner._
import org.specs2.runner._
import org.specs2.matcher.ParserMatchers
import Grammar.{ string, welcome }

@RunWith(classOf[JUnitRunner])
class GrammarSpec extends Specification with ParserMatchers {
  val parsers = Grammar
  
  "The opcode grammar" should { 
                                                                                         
    "parse property strings correctly" in {                                                   
      string must succeedOn("abc").withResult("abc")                                   
      string("ab,c") must beAFailure
    }
    
    "parse the 'Welcome' opcode correctly" in {
      welcome must succeedOn(
        "Welcome(name=String,path=string,apocalypse=42,round=23)"
      ).withResult(
        Welcome("String", "string", 42, 23))
    }                                  
  }                                                                                
}