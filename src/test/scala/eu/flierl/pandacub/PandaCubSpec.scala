package eu.flierl.pandacub

import org.specs2._
import org.junit.runner._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class PandaCubSpec extends Specification { def is =
  "Panda Cub consists of"                                     ^ 
                                                             p^
    "the opcode grammar" ~ new GrammarSpec                    ^ 
                                                           end
}