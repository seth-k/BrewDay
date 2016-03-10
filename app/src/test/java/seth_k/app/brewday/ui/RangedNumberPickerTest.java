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

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class RangedNumberPickerTest {
    private RangedNumberPicker mPicker;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void constructsNumberPickerFromXml() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        mPicker = (RangedNumberPicker) LayoutInflater.from(activity)
                .inflate(R.layout.test_ranged_number_picker, null);
        assertNotNull(mPicker);
    }
}