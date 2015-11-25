package seth_k.app.brewday;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HopTimerActivity extends Activity implements EditHopsFragment.OnHopsEditListener {

    public static final String TAG = HopTimerActivity.class.getSimpleName();

    public static final long DEFAULT_BOIL_TIME = 60l;
    public static final long MIN_TO_MILLIS = 60000;
    public static final long SEC_TO_MILLIS = 1000;

    private static final String HOPS_LIST = "HOPS_LIST";
    public static final boolean DEBUG_HOPS_LIST = true;

    @Bind(R.id.chronometer) TextView mTimerView;
    @Bind(R.id.hops_list) ListView mHopsListView;
    @Bind(R.id.start_timer_button) ImageView mStartButton;
    @Bind(R.id.pause_timer_button) ImageView mPauseButton;
    @Bind(R.id.reset_timer_button) ImageView mResetButton;
    @Bind(R.id.edit_time_button) ImageView mEditTimeButton;
    @Bind(R.id.edit_fragment) FrameLayout mEditFragmentFrame;

    List<Hops> mHopsToAdd;
    private HopTimer mHopTimer;
    private CountDownTimer mCountDownTimer;

    public List<Hops> getHopsToAdd() {
        return mHopsToAdd;
    }

    public void setHopsToAdd(List<Hops> hopsToAdd) {
        mHopsToAdd = hopsToAdd;
    }

    public HopTimer getHopTimer() {
        return mHopTimer;
    }

    public void setHopTimer(HopTimer hopTimer) {
        mHopTimer = hopTimer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hop_timer);
        ButterKnife.bind(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mHopsToAdd = new ArrayList<>();
        mHopTimer = new HopTimer(this, mHopsToAdd);

        if (DEBUG_HOPS_LIST) {
            Hops hop1 = new Hops("Northern Brewer", 1.5, 9);
            Hops hop2 = new Hops("Cascade", 1.0, 9);
            Hops hop3 = new Hops("Tettnanger", .75, 7);

            mHopsToAdd.add(hop1);
            mHopsToAdd.add(hop2);
            mHopsToAdd.add(hop3);
        }

        HopsListAdapter adapter = new HopsListAdapter(this, mHopsToAdd, mHopTimer);
        mHopsListView.setAdapter(adapter);

        // Edit the hops item on long click
        mHopsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mEditFragmentFrame.setVisibility(View.VISIBLE);
                FragmentManager fm = getFragmentManager();
                EditHopsFragment fragment = EditHopsFragment.newInstance(
                        mHopsToAdd.get(i),
                        i,
                        EditHopsFragment.MODE_EDIT);
                fm.beginTransaction()
                        .add(R.id.edit_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {
            mHopTimer.loadFromSavedPrefs();
        }

        updateTimerDisplay();
        if (mHopTimer.isRunning()) {
            startTimer(null);
        }
    }

    @OnClick(R.id.add_hops_button)
    public void addHops(View view) {
        mEditFragmentFrame.setVisibility(View.VISIBLE);
        FragmentManager fm = getFragmentManager();
        EditHopsFragment fragment = EditHopsFragment.newInstance(
                new Hops("Cascade", 0.0, 60), 0,
                EditHopsFragment.MODE_ADD);
        fm.beginTransaction()
                .add(R.id.edit_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void closeEditFragment() {
        // update the hops list and remove the edit fragment
        ((HopsListAdapter) mHopsListView.getAdapter()).notifyDataSetChanged();
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        mEditFragmentFrame.setVisibility(View.INVISIBLE);
    }


    @OnClick(R.id.start_timer_button)
    public void startTimer(View view) {
        mHopTimer.start();

        startTimerDisplay();
        mStartButton.setVisibility(View.INVISIBLE);
        mPauseButton.setVisibility(View.VISIBLE);
        mResetButton.setVisibility(View.INVISIBLE);
        mEditTimeButton.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.pause_timer_button)
    public void pauseTimer(View view) {
        mCountDownTimer.cancel();  //stop the display clock
        mStartButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.INVISIBLE);
        mEditTimeButton.setVisibility(View.VISIBLE);

        mHopTimer.pause();
    }

    @OnClick(R.id.reset_timer_button)
    public void resetTimer(View view) {
        mResetButton.setVisibility(View.INVISIBLE);
        mStartButton.setVisibility(View.VISIBLE);
        mEditTimeButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.INVISIBLE);
        mHopsToAdd.clear();
        ((HopsListAdapter) mHopsListView.getAdapter()).notifyDataSetChanged();
        mHopTimer.reset();
        updateTimerDisplay();
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
        AlertDialog boilTimePicker = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.boil_time_edit_title))
                .setView(npView)
                .setPositiveButton(R.string.dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mHopTimer.setTime(picker.getValue() * 10 * MIN_TO_MILLIS);
                                updateTimerDisplay();

                            }
                        })
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updateTimerDisplay();
                            }
                        })
                .create();
        picker.setMinValue(0);
        picker.setMaxValue(24);
        picker.setValue((int) (mHopTimer.getTime() / MIN_TO_MILLIS / 10));
        picker.setDisplayedValues(pickerValues);
        boilTimePicker.show();
    }

    private void updateTimerDisplay() {
        long millis = mHopTimer.getRemainingTime();
        long mins = millis / MIN_TO_MILLIS;
        long secs = (millis % MIN_TO_MILLIS) / SEC_TO_MILLIS;
        DecimalFormat f = new DecimalFormat("00");
        mTimerView.setText(f.format(mins) + ":" + f.format(secs));
    }

    private void startTimerDisplay() {
        long interval = mHopTimer.getRemainingTime();

        mCountDownTimer= new CountDownTimer(interval, SEC_TO_MILLIS) {
            @Override
            public void onTick(long l) {
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                mTimerView.setText(("Done!"));
                mStartButton.setVisibility(View.INVISIBLE);
                mPauseButton.setVisibility(View.INVISIBLE);
                mResetButton.setVisibility(View.VISIBLE);
                mHopTimer.stop();
            }
        }.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "Saving State...");
        super.onSaveInstanceState(outState);
        mHopTimer.saveToSharedPrefs();
        outState.putParcelableArrayList(HOPS_LIST, (ArrayList) mHopsToAdd);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "Restoring State...");
        super.onRestoreInstanceState(savedInstanceState);
        mHopTimer.loadFromSavedPrefs();
        ArrayList<Hops> hopsFromState = savedInstanceState.getParcelableArrayList(HOPS_LIST);
        mHopsToAdd.clear();
        mHopsToAdd.addAll(hopsFromState);
        ((HopsListAdapter) mHopsListView.getAdapter()).notifyDataSetChanged();
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

    @Override
    public void onAddHops(Hops hops) {
        mHopsToAdd.add(hops);
        closeEditFragment();
    }

    @Override
    public void onEditHops(Hops hops, int pos) {
        mHopsToAdd.set(pos, hops);
        closeEditFragment();
    }

    @Override
    public void onDeleteHops(Hops hops, int pos) {
        mHopsToAdd.remove(pos);
        closeEditFragment();
    }

    @Override
    public void onCancelEdit() {
        closeEditFragment();
    }
}
