package eu.flierl.pandacub

import org.specs2._
import org.junit.runner._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class PandaCubSpec extends Specification { def is = args(sequential=true) ^
  "Panda Cub consists of"                                                 ^ 
                                                                         p^
    "the opcode grammar" ~ new GrammarSpec                                ^
    "showable stuff" ~ new ShowSpec                                       ^
    "opcodes from the server and from the bot" ~ new OpcodeSpec           ^
    "the view and its graph" ~ new ViewSpec                               ^
                                                                       end
}