#!/bin/bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEMP="$(mktemp -d)"; trap 'rm -rf "$TEMP"' EXIT
javac -encoding UTF-8 -d "$TEMP" "$ROOT/app/src/main/java/com/kuet/ishara/logic/SentenceRules.java" "$ROOT/app/src/main/java/com/kuet/ishara/logic/GestureDictionary.java" "$ROOT/app/src/main/java/com/kuet/ishara/logic/SentenceComposer.java"
cat > "$TEMP/TestRules.java" <<'JAVA'
import com.kuet.ishara.logic.SentenceRules;
import com.kuet.ishara.logic.GestureDictionary;
import java.util.Arrays;
public class TestRules { public static void main(String[] a){
 if(!SentenceRules.suggest(Arrays.asList("আমি","পানি","চাই")).equals("আমি পানি চাই।"))throw new RuntimeException("rules failed");
 if(!SentenceRules.suggest("আমি সাহায্য চাই").equals("আমার সাহায্য দরকার।"))throw new RuntimeException("repair failed");
 if(!SentenceRules.suggest(" ").equals(""))throw new RuntimeException("empty failed");
 if(GestureDictionary.all().size()!=7)throw new RuntimeException("gesture count");
 for(String key:GestureDictionary.all().keySet()) for(int i=0;i<5;i++) if(GestureDictionary.translateForContext(key,i)==null)throw new RuntimeException("missing mapping");
 System.out.println("PASS: 7 gesture categories, 35 context mappings, 3 sentence rule checks");
}}
JAVA
javac -encoding UTF-8 -cp "$TEMP" -d "$TEMP" "$TEMP/TestRules.java"
java -cp "$TEMP" TestRules
python "$ROOT/tools/check_source.py"
