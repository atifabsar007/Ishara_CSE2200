package com.kuet.ishara;
import com.kuet.ishara.logic.ScenarioEngine;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;
public class ScenarioEngineTest {
 @Test public void scenariosHaveValidBranchesAndEndings() {
  assertEquals(4,ScenarioEngine.all().size());
  for(ScenarioEngine.Scenario s:ScenarioEngine.all()) {
   assertNotNull(s.node(s.start));
   Set<String> visited=new HashSet<>();
   visit(s,s.start,visited);
   assertEquals(s.nodes.size(),visited.size());
  }
 }
 private void visit(ScenarioEngine.Scenario s,String id,Set<String> visited){
  if("end".equals(id)||!visited.add(id))return;
  ScenarioEngine.Node n=s.node(id);assertNotNull("Missing node "+id,n);
  assertTrue(n.choices.size()>0);
  for(ScenarioEngine.Choice c:n.choices){assertFalse(c.label.isEmpty());assertTrue(c.points>=0);visit(s,c.next,visited);}
 }
}
