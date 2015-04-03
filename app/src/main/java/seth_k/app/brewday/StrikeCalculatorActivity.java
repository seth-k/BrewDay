package seth_k.app.brewday;

import android.content.res.Resources;
import android.os.Bundle;
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
    private static DecimalFormat tempFormat = new DecimalFormat("##0.0");
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
            tempET.setText(Double.toString(field_vals[i]));
        }

        updateCalcAndDisplay();
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
            return true;
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
                        mMash.setWaterRatio(input);
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

            updateCalcAndDisplay();
        }
        return false;  // Don't consume. Lets 'Next'/'Done' cycle through input fields as intended.
    }

    private void updateCalcAndDisplay() {
        double strike_volume = mMash.calculateStrikeVolume();
        double strike_temp = mMash.calculateStrikeTemp();
        Resources res = getResources();

        ((TextView) findViewById(R.id.sc_result_volume)).setText(volFormat.format(strike_volume)
                + " " + res.getString(R.string.unit_quart));
        ((TextView) findViewById(R.id.sc_result_temp)).setText(tempFormat.format(strike_temp)
                + res.getString(R.string.unit_deg_f));
    }
}
