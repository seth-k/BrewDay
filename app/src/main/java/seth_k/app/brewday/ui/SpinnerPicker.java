package seth_k.app.brewday.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.NumberPicker;

import java.util.Locale;

public class SpinnerPicker extends NumberPicker {
    private Adapter mAdapter;
    private String[] mItems;

    public SpinnerPicker(Context context) {
        this(context, null);
    }

    public SpinnerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(Adapter adapter) {
//        throw new UnsupportedOperationException();
        mAdapter = adapter;
        setMinValue(0);
        setMaxValue(adapter.getCount() - 1);
        updateDisplayedItems();
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    String format(int i) {
        if (mAdapter != null) {
            return mAdapter.getItem(i).toString();
        } else if (mItems != null) {
            return mItems[i];
        } else {
            return String.format(Locale.getDefault(), "%d", i);
        }
    }

    public Object getSelectedItem() {
        Object item = null;
        if (mAdapter != null) {
            item = mAdapter.getItem(getValue());
        } else if (mItems != null) {
            item = mItems[getValue()];
        }
        return item;
    }

    public void setItems(String[] items) {
        setMinValue(0);
        setMaxValue(items.length - 1);
        mAdapter = null;
        mItems = items;
        updateDisplayedItems();
    }

    private void updateDisplayedItems() {
        String[] displayItems = new String[getMaxValue()+1];
        for(int i=0; i<=getMaxValue(); i++) {
            displayItems[i] = format(i);
        }
        setDisplayedValues(displayItems);
    }
}
