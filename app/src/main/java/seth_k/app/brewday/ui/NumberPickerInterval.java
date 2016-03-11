package seth_k.app.brewday.ui;

import android.widget.NumberPicker;

/**
 * Created by Seth on 3/10/2016.
 */
public class NumberPickerInterval {
    private NumberPicker numberPicker;
    double start;
    double end;
    double step;

    public NumberPickerInterval(NumberPicker picker, double rangeStart, double rangeEnd, double step) {
        if (Math.signum(rangeEnd - rangeStart) != Math.signum(step)) {
            throw new IllegalArgumentException("The step value must be in the same direction as start -> end");
        }
        numberPicker = picker;
        start = rangeStart;
        end = rangeEnd;
        this.step = step;
        applyToNumberPicker();
    }

    public void applyToNumberPicker() {
        int rangeLength = ((Double) ((end - start) / step)).intValue() + 1;
        String[] pickerLabels = new String[rangeLength];
        for (Integer i = 0; i < rangeLength; i++) {
            pickerLabels[i] = Double.toString(start + step * i.doubleValue());
        }
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(rangeLength - 1);
        numberPicker.setDisplayedValues(pickerLabels);
    }

    public double getValue() {
        Integer value = numberPicker.getValue();
        return start + step * value.doubleValue();
    }

    public void setValue(double value) {
        Double pickerValue = (value - start) / step;
        numberPicker.setValue(pickerValue.intValue());
    }
}
