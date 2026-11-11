package com.kuet.ishara.ui;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.kuet.ishara.R;
import java.util.List;

/** Spinner labels with guaranteed legible contrast on light Ishara surfaces. */
public final class IsharaSpinnerAdapter extends ArrayAdapter<String> {
    public IsharaSpinnerAdapter(Context context, String[] values) {
        super(context, R.layout.ishara_spinner_item, values);
        setDropDownViewResource(R.layout.ishara_spinner_dropdown_item);
    }

    public IsharaSpinnerAdapter(Context context, List<String> values) {
        super(context, R.layout.ishara_spinner_item, values);
        setDropDownViewResource(R.layout.ishara_spinner_dropdown_item);
    }
}
