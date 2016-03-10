package seth_k.app.brewday.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import seth_k.app.brewday.R;

public class RangedNumberPicker extends NumberPicker implements NumberPicker.Formatter {

    public static final String FORMAT = "%.1f";
    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private double mRangeStart;
    private double mRangeEnd;
    private double mRangeStep;
    private String mFormatString;


    public RangedNumberPicker(Context context) {
        super(context);
    }

    public RangedNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RangedNumberPicker,0,0);

        try {
            mRangeStart = a.getFloat(R.styleable.RangedNumberPicker_rangeStart, 0.0f);
            mRangeEnd = a.getFloat(R.styleable.RangedNumberPicker_rangeEnd, 10.0f);
            mRangeStep = a.getFloat(R.styleable.RangedNumberPicker_rangeStep, 1.0f);
            mFormatString = a.getString(R.styleable.RangedNumberPicker_formatString);
            if(mFormatString == null) {
                mFormatString = FORMAT;
            }
        } finally {
            a.recycle();
        }

        int rangeLength = ((Double)( (mRangeEnd - mRangeStart) / mRangeStep)).intValue() + 1;
        setMinValue(0);
        setMaxValue(rangeLength-1);
    }

    public void setRangeStart(double rangeStart) {
        mRangeStart = rangeStart;
    }

    public void setRangeEnd(double rangeEnd) {
        mRangeEnd = rangeEnd;
    }

    public double getRangeStart() {
        return mRangeStart;
    }

    public double getRangeEnd() {
        return mRangeEnd;
    }

    public double getRangeStep() {
        return mRangeStep;
    }

    public void setRangeStep(double rangeStep) {
        mRangeStep = rangeStep;
    }

    public String getFormatString() {
        return mFormatString;
    }

    public void setFormatString(String formatString) {
        mFormatString = formatString;
    }

    @Override
    public String format(int value) {
        return String.format(mFormatString, mRangeStart + mRangeStep * ((Integer)value).doubleValue());
    }
}
