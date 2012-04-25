package eu.flierl.pandacub

import org.specs2.mutable._
import org.junit.runner._
import org.specs2.runner._
import org.specs2.matcher.ParserMatchers
import Grammar.{ string, vec, welcome, react }
import java.io.File

@RunWith(classOf[JUnitRunner])
class GrammarSpec extends Specification with ParserMatchers {
  val parsers = Grammar
  
  args(sequential = true)
  
  "The opcode grammar" should { 
                                                                                         
    "parse property strings correctly" in {                                                   
      string must succeedOn("abc").withResult("abc")                                   
      string("ab,c") must beAFailure
    }
    
    "parse vectors correctly" in {                                                   
      vec must succeedOn("1:2").withResult(Vec(1, 2))
      vec must succeedOn("23:2").withResult(Vec(23, 2))
      vec must succeedOn("1:42").withResult(Vec(1, 42))
      vec must succeedOn("-1:2").withResult(Vec(-1, 2))
      vec must succeedOn("1:-2").withResult(Vec(1, -2))
      vec("x:1") must beAFailure
      vec("1:x") must beAFailure
      vec(":1") must beAFailure
      vec("1:") must beAFailure
    }
    
    "parse the 'Welcome' opcode correctly" in {
      welcome must succeedOn(
        "Welcome(name=String,path=string,apocalypse=42,round=23)"
      ).withResult(
        Welcome("String", new File("string"), 42, 23))
    }
    
    "parse the 'React' opcode correctly" in {
      react must succeedOn(
        "React(generation=0,time=23,view=aaabbbccc,entity=pandacub,energy=-5)"
      ).withResult(
        MasterReact(0, "pandacub", 23, "aaabbbccc", -5))
        
      react("React(generation=1,time=2,view=a,entity=p,energy=0)") must beAFailure
        
      react must succeedOn(
        "React(master=-8:12345,view=aaabbbccc,entity=pandacub,generation=42,time=23,energy=-5)"
      ).withResult(
        MiniReact(42, "pandacub", 23, "aaabbbccc", -5, Vec(-8, 12345)))
    } 
  }                                                                                
}