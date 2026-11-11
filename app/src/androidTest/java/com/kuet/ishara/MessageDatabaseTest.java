package com.kuet.ishara;

import android.content.Context;
import android.database.Cursor;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.kuet.ishara.data.MessageDbHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MessageDatabaseTest {
    @Test public void savedPhraseCanBeRead() {
        Context context=ApplicationProvider.getApplicationContext();
        try(MessageDbHelper db=new MessageDbHelper(context)) {
            db.savePhrase("test phrase " + System.nanoTime());
            try(Cursor c=db.getSavedPhrases()) { assertTrue(c.moveToFirst()); }
        }
    }
}
