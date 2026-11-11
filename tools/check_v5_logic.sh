#!/bin/bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -encoding UTF-8 -d "$TMP" \
 "$ROOT/app/src/main/java/com/kuet/ishara/network/LocalAddress.java" \
 "$ROOT/app/src/main/java/com/kuet/ishara/logic/ScenarioEngine.java"
cat > "$TMP/Check.java" <<'JAVA'
import com.kuet.ishara.logic.ScenarioEngine;
import com.kuet.ishara.network.LocalAddress;
import java.util.HashSet;
import java.util.Set;
public class Check {
 static void check(boolean c,String msg){if(!c)throw new AssertionError(msg);}
 static void visit(ScenarioEngine.Scenario s,String id,Set<String> v){
  if("end".equals(id)||!v.add(id))return;
  ScenarioEngine.Node node=s.node(id);check(node!=null,"Bad path: "+id);
  check(!node.choices.isEmpty(),"No choices: "+id);
  for(ScenarioEngine.Choice ch:node.choices){check(ch.points>=0,"Negative points");visit(s,ch.next,v);}
 }
 public static void main(String[]a){
  check(ScenarioEngine.all().size()==4,"Missing scenarios");
  for(ScenarioEngine.Scenario s:ScenarioEngine.all()){Set<String> visited=new HashSet<>();visit(s,s.start,visited);check(visited.size()==s.nodes.size(),"Unreachable branch: "+s.id);}
  check(LocalAddress.isPrivateIPv4("192.168.1.8"),"Bad LAN detection");
  check(LocalAddress.isPrivateIPv4("172.31.2.5"),"Private address rejected");
  check(!LocalAddress.isPrivateIPv4("172.32.1.1"),"Public address accepted");
  check(!LocalAddress.isPrivateIPv4("8.8.8.8"),"Public IP accepted");
  check(!LocalAddress.isPrivateIPv4("256.1.1.1"),"Invalid IP accepted");
  check(LocalAddress.validCode("012345")&&!LocalAddress.validCode("123"),"Bad code validation");
  check(LocalAddress.validMessage("Hello")&&!LocalAddress.validMessage("a".repeat(401)),"Bad message validation");
  System.out.println("PASS: 4 scenarios, every branch reachable, private IPv4/session code/message limits");
 }
}
JAVA
javac -encoding UTF-8 -cp "$TMP" -d "$TMP" "$TMP/Check.java"
java -cp "$TMP" Check
