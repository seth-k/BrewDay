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
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class SpinnerPickerTest {
    private SpinnerPicker mPicker;
    private CharSequence[] planets;
//    private String[] planets = {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
//    private List<CharSequence> planetList = new ArrayList<>(Arrays.asList((CharSequence[])planets));

    private String[] mItems = {"ITEM 1", "ITEM 2", "ITEM 3"};

    @Before
    public void setUp() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        mPicker = (SpinnerPicker) LayoutInflater.from(activity)
                .inflate(R.layout.test_spinner_picker, null);
        planets = activity.getResources().getStringArray(R.array.planets);
        mPicker.setItems(mItems);
    }

    @Test
    public void acceptsAnArrayOfString() throws Exception {
        assertEquals(3, mPicker.getMaxValue() + 1);
    }

    @Test
    public void theAdapterIsNull() throws Exception {
        assertNull(mPicker.getAdapter());
    }

    @Test
    public void displaysStringRepOfItem() throws Exception {
        assertEquals("ITEM 1", mPicker.format(0));
    }

    @Test
    public void returnsTheSelectedItem() throws Exception {
        mPicker.setValue(2);

        assertEquals("ITEM 3", mPicker.getSelectedItem());
    }

    @Test
    public void displayedValuesSetToTheItems() throws Exception {
        String[] displayedItems = mPicker.getDisplayedValues();

        assertEquals(mItems[1], displayedItems[1]);
    }
}