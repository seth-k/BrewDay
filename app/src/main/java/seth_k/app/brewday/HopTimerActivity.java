package seth_k.app.brewday;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class HopTimerActivity extends ActionBarActivity {

    public static final long DEFAULT_BOIL_TIME = 60l;
    public static final long MIN_TO_MILLIS = 60000;
    public static final long SEC_TO_MLLIS = 1000;

    public static final String BOIL_TIME_KEY = "boil_time";
    public static final String BOIL_END_KEY = "boil_end";
    public static final String IS_RUNNING_KEY = "is_running";

    public static final int DEBUG_HOPS_LIST = 1;

    @InjectView(R.id.chronometer) TextView mTimer;
    @InjectView(R.id.hops_list) ListView mHopsList;
    @InjectView(R.id.start_timer_button) ImageButton mStartButton;
    @InjectView(R.id.pause_timer_button) ImageButton mPauseButton;
    @InjectView(R.id.add_hops_button) ImageButton mAddButton;

    List<Hops> mHopsToAdd;
    private long mBoilTime; // Time to end of boil in millisec from start or last pause
    private long mBoilStopTime; // Time of end of boil in system clock time
    private boolean isRunning;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hop_timer);
        ButterKnife.inject(this);
        mHopsToAdd = new ArrayList<>();

        if (DEBUG_HOPS_LIST > 0) {
            Hops hop1 = new Hops("Northern Brewer", 1.5, 60);
            Hops hop2 = new Hops("Cascade", 1.0, 15);
            Hops hop3 = new Hops("Tettnanger", .75, 5);

            mHopsToAdd.add(hop1);
            mHopsToAdd.add(hop2);
            mHopsToAdd.add(hop3);
        }
        HopsListAdapter adapter = new HopsListAdapter(this, mHopsToAdd);
        mHopsList.setAdapter(adapter);

        if (savedInstanceState != null) {
            mBoilTime = savedInstanceState.getLong(BOIL_TIME_KEY, DEFAULT_BOIL_TIME * MIN_TO_MILLIS);
            mBoilStopTime = savedInstanceState.getLong(BOIL_END_KEY, 0);
            isRunning = savedInstanceState.getBoolean(IS_RUNNING_KEY, false);
        } else {
            mBoilTime = DEFAULT_BOIL_TIME * MIN_TO_MILLIS;
            mBoilStopTime = 0;
            isRunning = false;
        }

        updateTimerDisplay(mBoilTime);
        if (isRunning) {
            startTimerDisplay();
        }
    }

    @OnClick(R.id.start_timer_button)
    public void startTimer(View view) {
        if (mBoilStopTime == 0) {
            mBoilStopTime = SystemClock.elapsedRealtime() + mBoilTime;
        }
        startTimerDisplay();
        isRunning = true;
        mStartButton.setVisibility(View.INVISIBLE);
        mPauseButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.pause_timer_button)
    public void pauseTimer() {
        mCountDownTimer.cancel();  //stop the display clock
        mBoilTime = mBoilStopTime - SystemClock.elapsedRealtime(); //find the remaining time
        mBoilStopTime = 0; // reset the ending time to undefined
        isRunning = false;
        mStartButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.INVISIBLE);

    }

    private void updateTimerDisplay(long millis) {
        long mins = millis / MIN_TO_MILLIS;
        long secs = (millis % MIN_TO_MILLIS) / SEC_TO_MLLIS;
        DecimalFormat f = new DecimalFormat("00");
        mTimer.setText(f.format(mins) + ":" + f.format(secs));
    }

    private void startTimerDisplay() {
        long interval = mBoilStopTime - SystemClock.elapsedRealtime();

        mCountDownTimer= new CountDownTimer(interval, SEC_TO_MLLIS) {
            @Override
            public void onTick(long l) {
                updateTimerDisplay(l);
            }

            @Override
            public void onFinish() {
                mTimer.setText(("Done!"));
            }
        }.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BOIL_TIME_KEY, mBoilTime);
        outState.putLong(BOIL_END_KEY, mBoilStopTime);
        outState.putBoolean(IS_RUNNING_KEY, isRunning);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hop_timer, menu);
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
}
