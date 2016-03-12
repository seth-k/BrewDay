package seth_k.app.brewday.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.NumberPicker;

public class SpinnerPicker extends NumberPicker implements NumberPicker.Formatter {
    private Adapter mAdapter;

    public SpinnerPicker(Context context) {
        super(context);
    }

    public SpinnerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        setMaxValue(adapter.getCount() - 1);
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public String format(int i) {
        return mAdapter.getItem(i).toString();
    }

    public Object getSelectedItem() {
        return mAdapter.getItem(getValue());
    }
}
