package com.kuet.ishara;
import org.junit.Test;
import static org.junit.Assert.*;
import com.kuet.ishara.logic.SentenceRules;
import java.util.Arrays;
public final class SentenceRulesTest {
 @Test public void formsBanglaSentence(){assertEquals("আমি পানি চাই।",SentenceRules.suggest(Arrays.asList("আমি","পানি","চাই")));}
 @Test public void respectsKnownTemplate(){assertEquals("আমার সাহায্য দরকার।",SentenceRules.suggest("আমি সাহায্য চাই"));}
 @Test public void leavesEmptyInputEmpty(){assertEquals("",SentenceRules.suggest(" "));}
 @Test public void noDoublePunctuation(){assertEquals("ধন্যবাদ।",SentenceRules.suggest("ধন্যবাদ।"));}
}
