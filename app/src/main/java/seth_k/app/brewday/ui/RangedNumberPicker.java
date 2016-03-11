package seth_k.app.brewday.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import seth_k.app.brewday.R;

public class RangedNumberPicker extends NumberPicker {

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
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RangedNumberPicker,0,0);

        obtainAttributes(a);
        reformatLabels();
    }

    public RangedNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RangedNumberPicker,
                defStyleAttr,0);

        obtainAttributes(a);
        reformatLabels();
    }


    private void obtainAttributes(TypedArray a) {
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
    }

    public void reformatLabels() {
        int rangeLength = ((Double)( (mRangeEnd - mRangeStart) / mRangeStep)).intValue() + 1;
        String[] pickerLabels = new String[rangeLength];
        for (int  i = 0; i < rangeLength; i++) {
            pickerLabels[i] = format(i);
        }
        setMinValue(0);
        setMaxValue(rangeLength-1);
        setDisplayedValues(pickerLabels);
    }

    public void setRangeStart(double rangeStart) {
        mRangeStart = rangeStart;
        reformatLabels();
    }

    public void setRangeEnd(double rangeEnd) {
        mRangeEnd = rangeEnd;
        reformatLabels();
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
        reformatLabels();
    }

    public String getFormatString() {
        return mFormatString;
    }

    public void setFormatString(String formatString) {
        mFormatString = formatString;
        reformatLabels();
    }

    public double getScaledValue() {
        int value = super.getValue();
        return mRangeStart + mRangeStep * value;

    }

    @Override
    public int getValue() {
        return (int) getScaledValue();
    }

    public void setValue(double value) {
        Double pickerValue = (value - mRangeStart) / mRangeStep;
        super.setValue(pickerValue.intValue());
        invalidate();
        requestLayout();
    }

    public String format(int value) {
        return String.format(mFormatString, mRangeStart + mRangeStep * ((Integer)value).doubleValue());
    }
}
