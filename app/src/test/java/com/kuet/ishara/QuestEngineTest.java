package com.kuet.ishara;
import com.kuet.ishara.logic.QuestEngine;
import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;
public class QuestEngineTest {
 @Test public void everyQuestionHasAnAnswer(){for(int mode=0;mode<=1;mode++)for(int i=0;i<80;i++){QuestEngine.Item q=QuestEngine.next(mode,new Random(i));assertNotNull(q.answer);assertTrue(q.options.length>=3);assertEquals(mode,q.mode);if(mode==0)assertTrue(java.util.Arrays.asList(q.options).contains(q.answer));else{java.util.List<String> w=java.util.Arrays.asList(q.options);for(String s:q.answer.split(" "))assertTrue(w.contains(s));}}}
 @Test public void scoresAndChecks(){QuestEngine.Item q=QuestEngine.next(1,new Random(7));assertTrue(QuestEngine.matches(q.answer,q));assertFalse(QuestEngine.matches("wrong answer",q));assertEquals(15,QuestEngine.points(true,1));assertEquals(10,QuestEngine.points(true,0));assertEquals(0,QuestEngine.points(false,0));}
}
