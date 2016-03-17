package seth_k.app.brewday.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seth_k.app.brewday.BuildConfig;
import seth_k.app.brewday.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class SpinnerPickerTestWithAdapter {
    private SpinnerPicker mPicker;
    private ArrayAdapter<CharSequence> mAdapter; // list of planets: "Mercury", "Venus", etc.
    private String[] planets = {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
    private List<CharSequence> planetList = new ArrayList<>(Arrays.asList((CharSequence[])planets));

    private String[] mItems = {"ITEM 1", "ITEM 2", "ITEM 3"};

    @Before
    public void setUp() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        mPicker = (SpinnerPicker) LayoutInflater.from(activity)
                .inflate(R.layout.test_spinner_picker, null);
        mAdapter = new ArrayAdapter<>(activity, 0, planetList);
        mPicker.setAdapter(mAdapter);
    }

    @Test
    public void constructsPickerFromXml() throws Exception {
        assertNotNull(mPicker);
    }

    @Test
    public void acceptsAnAdapterAndMaxValueWillReflectNumberOfItems() throws Exception {
        assertEquals(8, mPicker.getMaxValue() + 1);
    }

    @Test
    public void withAnAdapterDisplaysStringRepOfItem() throws Exception {
        assertEquals("Mercury", mPicker.format(0));
    }

    @Test
    public void returnsTheSelectedItem() throws Exception {
        mPicker.setValue(2);

        assertEquals("Earth", mPicker.getSelectedItem());
    }

    /* Supporting this is more involved than it looks because in Spinner it relies on a package-local
       inner class of AdapterView.
     */
//    @Test
//    public void updatesWhenAdapterIs() throws Exception {
//        mAdapter.add("Pluto");
//
//        assertEquals(9, mPicker.getMaxValue() + 1);
//    }

    //TODO Test for failure of Picker to resize

}