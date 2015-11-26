package seth_k.app.brewday;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.TextView.OnEditorActionListener;


public class StrikeCalculatorActivity extends Activity implements OnEditorActionListener {

    private static DecimalFormat volFormat = new DecimalFormat("##0.00");
    private static DecimalFormat tempOutFormat = new DecimalFormat("##0.0");
    private static DecimalFormat tempInFormat = new DecimalFormat("##0");
    private Mash mMash = new Mash();
    @Bind(R.id.sc_result_volume)
    TextView mVolumeResultText;
    @Bind(R.id.sc_result_temp)
    TextView mTemperatureResultText;
    private String mVolumeUnits;
    private String mTemperatureUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strike_calcualtor);

        ButterKnife.bind(this);
        mVolumeUnits = getString(R.string.unit_quart);
        mTemperatureUnits = getString(R.string.unit_deg_f);

        // set listener and initial value for input fields
        // TODO Initializing the strike calculator is ugly and brittle.
        int[] field_ids = {R.id.grain_weight, R.id.water_ratio, R.id.mash_target, R.id.grain_temp};
        double[] field_vals = {mMash.getGrainWeight(), mMash.getWaterRatio(), mMash.getStrikeTarget(), mMash.getGrainTemp()};
        for (int i = 0; i < field_ids.length; i++) {
            EditText tempET = (EditText) findViewById(field_ids[i]);
            tempET.setOnEditorActionListener(this);
            if (i < 2) {
                tempET.setText(Double.toString(field_vals[i]));
            } else {
                tempET.setText(tempInFormat.format(field_vals[i]));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // grab relevant settings (could have been changed from settings panel)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        double tunCorrection = Double.parseDouble(prefs.getString("mash_tun_correction", "0.0"));
        double infusionTemp = Double.parseDouble(prefs.getString("infusion_water_temp", "212.0"));

        mMash.setMashTunCorrection(tunCorrection);
        mMash.setInfusionTemp(infusionTemp);
        updateDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
            try {
                double input = Double.valueOf(v.getText().toString());
                switch (v.getId()) {
                    case R.id.grain_weight:
                        mMash.setGrainWeight(input);
                        break;
                    case R.id.water_ratio:
                        mMash.setInitialWaterRatio(input);
                        break;
                    case R.id.mash_target:
                        mMash.setStrikeTarget(input);
                        break;
                    case R.id.grain_temp:
                        mMash.setGrainTemp(input);
                        break;
                }
            } catch (NumberFormatException e) {
                v.setError("Must be a number; can't be blank.");
            }

            updateDisplay();
        }
        return false;  // Don't consume. Lets 'Next'/'Done' cycle through input fields as intended.
    }

    private void updateDisplay() {
        double strike_volume = mMash.getStrikeVolume();
        double strike_temp = mMash.getStrikeTemp();

        mVolumeResultText.setText(String.format("%s %s", volFormat.format(strike_volume), mVolumeUnits));
        mTemperatureResultText.setText(String.format("%s%s", tempOutFormat.format(strike_temp), mTemperatureUnits));
    }
}
