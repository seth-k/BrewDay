package seth_k.app.brewday.ui;

import android.app.Activity;
import android.view.LayoutInflater;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import seth_k.app.brewday.BuildConfig;
import seth_k.app.brewday.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class RangedNumberPickerTest {
    public static final double DELTA = 0.0000001;
    private RangedNumberPicker mPicker;

    /**
     * Sets up a RangedNumberPicker from a test XML file.  The file specifies a range of [10, 20]
     * with a step of 2.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        mPicker = (RangedNumberPicker) LayoutInflater.from(activity)
                .inflate(R.layout.test_ranged_number_picker, null);
    }

    @Test
    public void constructsNumberPickerFromXml() throws Exception {

        assertNotNull(mPicker);
    }

    @Test
    public void setsRangeStart() throws Exception {
        mPicker.setRangeStart(2.0);

        assertEquals(2.0, mPicker.getRangeStart(), DELTA);
    }

    @Test
    public void setsRangeStartFromXml() throws Exception {
        // XML will set range to (10, 20, 2)
        assertEquals(10.0, mPicker.getRangeStart(), DELTA);
        assertEquals(20.0, mPicker.getRangeEnd(), DELTA);
        assertEquals(2.0, mPicker.getRangeStep(), DELTA);
    }

    @Test
    public void rangeIntervalHasSixSteps() throws Exception {
        assertEquals(0, mPicker.getMinValue());
        assertEquals(5, mPicker.getMaxValue());
        assertEquals(6, mPicker.getMaxValue() - mPicker.getMinValue() + 1);
        assertEquals(6, mPicker.getDisplayedValues().length);
    }

    @Test
    public void setsStartLabelToTenPointOh() throws Exception {
        assertEquals("10.0", mPicker.getDisplayedValues()[0]);
    }

    @Test
    public void setsSecondLabelToTwelvePointOh() throws Exception {
        assertEquals("12.0", mPicker.getDisplayedValues()[1]);
    }

    @Test
    public void setsLastLabelToTwentyPointOh() throws Exception {
        assertEquals("20.0", mPicker.getDisplayedValues()[mPicker.getMaxValue()]);
    }

    @Test
    public void changingFormatStringUsesNewFormat() throws Exception {
        mPicker.setFormatString("%2.0f");
        assertEquals("10", mPicker.getDisplayedValues()[0]);
    }

    @Test
    public void formatStringCanBeSetInXml() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        RangedNumberPicker picker = (RangedNumberPicker) LayoutInflater.from(activity)
                .inflate(R.layout.test_ranged_number_picker_formated, null);
        assertEquals("10.00", picker.getDisplayedValues()[0]);
    }

    @Test
    public void getValueReturnsValueScaledToRange() throws Exception {
        assertEquals(10.0, mPicker.getScaledValue(), DELTA);
        assertEquals(10, mPicker.getValue());
    }

    @Test
    public void setValueWithDoubleSetsScaledValue() throws Exception {
        mPicker.setValue(12.0);

        assertEquals(12, mPicker.getValue());
    }

    @Test
    public void setValueWithIntSetsIndexValue() throws Exception {
        // 12.0 should be the second entry, i.e., index 1
        mPicker.setValue(1);

        assertEquals(12, mPicker.getValue());
    }
}