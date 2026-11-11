package com.kuet.ishara.logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
/** Pure Java game logic, so the lab can unit test it without an emulator. */
public final class QuestEngine {
 public static final int MATCH=0, SENTENCE=1;
 public static final class Item {
  public final String prompt,answer,emoji;public final String[] options;public final int mode;
  public Item(int mode,String prompt,String answer,String emoji,String[] options){this.mode=mode;this.prompt=prompt;this.answer=answer;this.emoji=emoji;this.options=options;}
 }
 private static final String[][] MATCHES={
  {"👍","Thumbs up","হ্যাঁ","না","অপেক্ষা করুন","বিদায়"},
  {"👎","Thumbs down","না","হ্যাঁ","ধন্যবাদ","পানি"},
  {"✋","Open palm","একটু থামুন","আরো বলুন","খাবার","কোথায়"},
  {"✊","Closed fist","আমার সাহায্য দরকার","আমি ভালো আছি","শুভ সকাল","পরে"},
  {"✌️","Victory","ধন্যবাদ","আমি আসছি","দয়া করে","ঘুম"},
  {"☝️","Pointing up","একটু অপেক্ষা করুন","আমি ক্ষুধার্ত","কথা বলুন","হ্যাঁ"},
  {"🤟","I Love You","আপনাকে ভালোবাসি","আমি যাব","পানি চাই","না"}
 };
 private static final String[] SENTENCES={"আমি পানি চাই","দয়া করে আবার বলুন","আমি এখন বাসায় যাব","আপনি কি সাহায্য করবেন","আমার ক্লাসের নোট দরকার","আমি একটু বিশ্রাম চাই","আপনাকে অনেক ধন্যবাদ"};
 private QuestEngine(){}
 public static Item next(int mode,Random rnd){if(mode==MATCH){String[] data=MATCHES[rnd.nextInt(MATCHES.length)];
   List<String> opts=new ArrayList<>(Arrays.asList(data[2],data[3],data[4],data[5]));Collections.shuffle(opts,rnd);
   return new Item(MATCH,"What does this demo gesture mean?",data[2],data[0]+"  "+data[1],opts.toArray(new String[0]));}
   String answer=SENTENCES[rnd.nextInt(SENTENCES.length)];List<String> words=new ArrayList<>(Arrays.asList(answer.split(" ")));
   Collections.shuffle(words,rnd);return new Item(SENTENCE,"Arrange the words into a useful sentence",answer,"✦",words.toArray(new String[0]));
 }
 public static String badgeFor(int points){return points>=300?"Communication Champion":points>=150?"Word Builder":points>=50?"Bridge Maker":"Explorer";}
 public static int points(boolean correct,int mode){return correct?(mode==MATCH?10:15):0;}
 public static boolean matches(String attempt,Item item){return item.answer.equals(attempt.trim());}
}
