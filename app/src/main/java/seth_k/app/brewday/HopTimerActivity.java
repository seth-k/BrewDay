package seth_k.app.brewday;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class HopTimerActivity extends Activity {

    public static final String TAG = HopTimerActivity.class.getSimpleName();

    public static final long DEFAULT_BOIL_TIME = 10l;
    public static final long MIN_TO_MILLIS = 60000;
    public static final long SEC_TO_MLLIS = 1000;

    public static final String BOIL_TIME_KEY = "boil_time";
    public static final String BOIL_END_KEY = "boil_end";
    public static final String IS_RUNNING_KEY = "is_running";
    public static final String HOPS_TO_ADD_EXTRA = "HOPS_TO_ADD";

    public static final int DEBUG_HOPS_LIST = 1;

    @InjectView(R.id.chronometer) TextView mTimer;
    @InjectView(R.id.hops_list) ListView mHopsList;
    @InjectView(R.id.start_timer_button) ImageView mStartButton;
    @InjectView(R.id.pause_timer_button) ImageView mPauseButton;
    @InjectView(R.id.edit_time_button) ImageView mEditTimeButton;
    @InjectView(R.id.add_hops_button) ImageButton mAddButton;

    List<Hops> mHopsToAdd;
    private long mBoilTime; // Time to end of boil in millisec from start or last pause
    private long mBoilStopTime; // Time of end of boil in system clock time
    private boolean isRunning;
    private CountDownTimer mCountDownTimer;
    private AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hop_timer);
        ButterKnife.inject(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        mHopsToAdd = new ArrayList<>();

        if (DEBUG_HOPS_LIST > 0) {
            Hops hop1 = new Hops("Northern Brewer", 1.5, 9);
            Hops hop2 = new Hops("Cascade", 1.0, 8);
            Hops hop3 = new Hops("Tettnanger", .75, 7);

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
        long realtime = SystemClock.elapsedRealtime(); //get current time
        if (mBoilStopTime == 0) {  // Check to see if it is already set .ie the timer is only
            // Starting if activity is recreated.
            mBoilStopTime = realtime + mBoilTime;

            // add notifications for each hops addition and boil end
            int requestCode = 0;
            for (Hops hop : mHopsToAdd) {
                long atMillis = mBoilStopTime - hop.getBoilTime() * MIN_TO_MILLIS;
                String message = hop.toString();
                setAlarm(requestCode++, atMillis, message);
                Log.d(TAG, "Adding alarm for: " + message);
            }
            setAlarm(requestCode, mBoilStopTime, "End of Boil. Please turn burner off.");
        }
        startTimerDisplay();
        isRunning = true;
        mStartButton.setVisibility(View.INVISIBLE);
        mPauseButton.setVisibility(View.VISIBLE);
        mEditTimeButton.setVisibility(View.INVISIBLE);
    }

    private void setAlarm(int requestCode, long atMillis, String message) {
        Intent alarmIntent = new Intent(this, HopAlarmReceiver.class);
        alarmIntent.putExtra(HOPS_TO_ADD_EXTRA, message);
        PendingIntent pending = PendingIntent.getBroadcast(this, requestCode,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                atMillis, pending);
    }

    @OnClick(R.id.pause_timer_button)
    public void pauseTimer(View view) {
        mCountDownTimer.cancel();  //stop the display clock
        mBoilTime = mBoilStopTime - SystemClock.elapsedRealtime(); //find the remaining time
        mBoilStopTime = 0; // reset the ending time to undefined
        isRunning = false;
        mStartButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.INVISIBLE);
        mEditTimeButton.setVisibility(View.VISIBLE);

        // Cancel all notifications for each hops addition and boil end
        int requestCode = 0;
        for (Hops hop : mHopsToAdd) {
            cancelAlarm(requestCode++);
        }
        cancelAlarm(requestCode);
    }

    private void cancelAlarm(int requestCode) {
        Intent alarmIntent = new Intent(this, HopAlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, requestCode,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(pending);
    }

    @OnClick(R.id.edit_time_button)
    public void editTimer(View view) {
        // Create pop-up dialog for selecting boil time

        View npView = getLayoutInflater().inflate(R.layout.boil_time_dialog, null);
        final NumberPicker picker = (NumberPicker) npView;
        String[] pickerValues = new String[25];
        for (int i = 0; i < 25; i++) {
            pickerValues[i] = Integer.toString(i * 10);
        }
        picker.setMinValue(0);
        picker.setMinValue(24);
        picker.setDisplayedValues(pickerValues);
        AlertDialog boilTimePicker = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.boil_time_edit_title))
                .setView(npView)
                .setPositiveButton(R.string.dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mBoilTime = picker.getValue() * 10 * MIN_TO_MILLIS;
                                updateTimerDisplay(mBoilTime);

                            }
                        })
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updateTimerDisplay(mBoilTime);
                            }
                        })
                .create();
        picker.setMinValue(0);
        picker.setMaxValue(24);
        picker.setValue((int) (mBoilTime / MIN_TO_MILLIS / 10));
        picker.setDisplayedValues(pickerValues);
        boilTimePicker.show();
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBoilTime = savedInstanceState.getLong(BOIL_TIME_KEY, DEFAULT_BOIL_TIME * MIN_TO_MILLIS);
        mBoilStopTime = savedInstanceState.getLong(BOIL_END_KEY, 0);
        isRunning = savedInstanceState.getBoolean(IS_RUNNING_KEY, false);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
