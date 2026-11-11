#!/bin/sh
# Platform-independent tests; no Android SDK needed.
set -eu
BASE="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
mkdir -p "$TMP/src" "$TMP/classes"
cat > "$TMP/src/LogicSmoke.java" <<'JAVA'
import com.kuet.ishara.logic.GestureDictionary;
import com.kuet.ishara.logic.SentenceComposer;
import com.kuet.ishara.logic.PhraseCatalog;
public class LogicSmoke {
 public static void main(String[] args){
  if(GestureDictionary.all().size()!=7) throw new AssertionError("expected 7 built-in physical classes");
  int total=0;
  for(String g:GestureDictionary.all().keySet())
    for(int c=0;c<5;c++){
      String phrase=GestureDictionary.translateForContext(g,c);
      if(phrase==null || phrase.trim().isEmpty())throw new AssertionError(g+" context "+c);
      total++;
    }
  if(total!=35) throw new AssertionError("expected 35 gesture-context mappings");
  String sentence="";
  for(String word:new String[]{"আমি","পানি","চাই"})sentence=SentenceComposer.append(sentence,word);
  if(!"আমি পানি চাই".equals(sentence))throw new AssertionError(sentence);
  if(PhraseCatalog.common().size()<10)throw new AssertionError("phrase catalog incomplete");
  System.out.println("PASS Java logic: 7 handshapes, 5 contexts, 35 message mappings, sentence and phrase catalog.");
 }
}
JAVA
javac -encoding UTF-8 -d "$TMP/classes" \
 "$BASE/app/src/main/java/com/kuet/ishara/logic/GestureDictionary.java" \
 "$BASE/app/src/main/java/com/kuet/ishara/logic/SentenceComposer.java" \
 "$BASE/app/src/main/java/com/kuet/ishara/logic/PhraseCatalog.java" \
 "$TMP/src/LogicSmoke.java"
java -cp "$TMP/classes" LogicSmoke
