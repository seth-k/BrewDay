package seth_k.app.brewday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

import static android.widget.TextView.OnEditorActionListener;


public class StrikeCalculatorActivity extends ActionBarActivity implements OnEditorActionListener {

    private static DecimalFormat volFormat = new DecimalFormat("##0.00");
    private static DecimalFormat tempOutFormat = new DecimalFormat("##0.0");
    private static DecimalFormat tempInFormat = new DecimalFormat("##0");
    private Mash mMash = new Mash();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strike_calcualtor);

        // set listener and initial value for input fields
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

        updateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // grab relavent settings (could have been changed from settings panel)
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
        getMenuInflater().inflate(R.menu.menu_strike_calcualtor, menu);
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
            } catch (NumberFormatException e) { // Ignore empty fields.
            }

            updateDisplay();
        }
        return false;  // Don't consume. Lets 'Next'/'Done' cycle through input fields as intended.
    }

    private void updateDisplay() {
        double strike_volume = mMash.getStrikeVolume();
        double strike_temp = mMash.getStrikeTemp();
        Resources res = getResources();

        ((TextView) findViewById(R.id.sc_result_volume)).setText(volFormat.format(strike_volume)
                + " " + res.getString(R.string.unit_quart));
        ((TextView) findViewById(R.id.sc_result_temp)).setText(tempOutFormat.format(strike_temp)
                + res.getString(R.string.unit_deg_f));
    }
}
