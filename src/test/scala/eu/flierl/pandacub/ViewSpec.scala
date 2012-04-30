package eu.flierl.pandacub

import org.specs2.mutable._
import org.junit.runner._
import org.specs2.runner._
import Utils.viewFrom

@RunWith(classOf[JUnitRunner])
class ViewSpec extends Specification {
  "The view" should { 
                                                                                         
    "build a graph correctly" in {
      val graph = viewFrom(
        "_b__P"
      + "_Sp__"
      + "W_M_s"
      + "__W__"
      + "m???B").graph()
                     
      graph.nodes.map(_.value) must haveTheSameElementsAs (List(
        (0, 0), (2, 0), (3, 0), (4, 0),
        (0, 1), (1, 1), (3, 1), (4, 1),
        (1, 2), (2, 2), (3, 2),
        (0, 3), (1, 3), (3, 3), (4, 3),
        (1, 4), (2, 4), (3, 4), (4, 4)
      ) map Vec.tupled)
    }
    
    "connect graph nodes correctly" in {
      val graph = viewFrom(
        "___"          // 3 + 3 + 1
      + "_M_"          // 3 + 4 + 1
      + "W__").graph() // 0 + 2 + 0
        
      graph.nodes.map(_.value) must not contain(Vec(0, 2))
      
      graph.edges must have size(17)
    }
    
  }                           
}