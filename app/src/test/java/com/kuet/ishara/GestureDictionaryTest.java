package com.kuet.ishara;

import com.kuet.ishara.logic.GestureDictionary;
import com.kuet.ishara.logic.SentenceComposer;
import com.kuet.ishara.logic.PhraseCatalog;
import org.junit.Test;
import static org.junit.Assert.*;

public class GestureDictionaryTest {
    @Test public void allSevenGesturesHavePhrases() {
        assertEquals(7,GestureDictionary.all().size());
        assertEquals("হ্যাঁ",GestureDictionary.translate("Thumb_Up"));
    }
    @Test public void contextSpecificPhrasesDifferWithoutInventingSigns() {
        assertEquals("আমি বুঝতে পেরেছি",GestureDictionary.translateForContext("Thumb_Up",1));
        assertEquals("হ্যাঁ, আমি যেতে চাই",GestureDictionary.translateForContext("Thumb_Up",2));
        assertEquals("হ্যাঁ",GestureDictionary.translateForContext("Thumb_Up",0));
        assertNull(GestureDictionary.translateForContext("Unknown",2));
    }
    @Test public void unknownGestureDoesNotInventPhrase() { assertNull(GestureDictionary.translate("None")); }
    @Test public void sentenceBuilderHandlesEmptyInputs() {
        assertEquals("ধন্যবাদ",SentenceComposer.append("", " ধন্যবাদ "));
        assertEquals("হ্যাঁ না",SentenceComposer.append(" হ্যাঁ "," না "));
        assertEquals("হ্যাঁ",SentenceComposer.append(" হ্যাঁ ",null));
    }
    @Test public void quickPhraseCatalogNotEmpty() { assertTrue(PhraseCatalog.common().size()>=10); }
}
