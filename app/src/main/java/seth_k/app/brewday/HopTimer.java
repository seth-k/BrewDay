package seth_k.app.brewday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

public class HopTimer {
    public static final String TAG = HopTimer.class.getSimpleName();

    public static final long MIN_TO_MILLIS = 60000;
    public static final long SEC_TO_MILLIS = 1000;
    public static final String BOIL_TIME_KEY = "hop_timer_time";
    public static final String BOIL_START_KEY = "hop_timer_start";
    public static final String BOIL_END_KEY = "hop_timer_end";
    public static final String IS_RUNNING_KEY = "hop_timer_is_running";
    public static final String NUMBER_OF_ALARMS_KEY = "hop_timer_num_alarms";

    public static final String HOPS_TO_ADD_EXTRA = "HOPS_TO_ADD";
    public static final String ALARM_ID_EXTRA = "ALARM_ID";

    private List<Hops> mHopsList;
    private Context mContext;
    private long mTime; // Time to end of boil in millisec from start or last pause
    private long mStartTime; // Time the timer was last started in system clock time
    private long mStopTime; // Time of end of boil in system clock time
    private boolean isRunning;
    private int mNumberOfAlarms;
    private AlarmManager mAlarmManager;

    public HopTimer(Context context, List<Hops> hopsList) {
        mContext = context;
        mHopsList = hopsList;
        isRunning = false;
        mNumberOfAlarms = 0;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public long getRemainingTime() {
        long now = System.currentTimeMillis();

        if (isRunning) {
            if (now > mStopTime) { return 0; } // End time past
            else { return mStopTime - now; }   // during boil, timer running
        } else {
            if (mStopTime == 0) {
                return mTime; // timer stopped at end
            }
            else {
                return 0;
            }
        }
    }

    public long getStopTime() {
        return mStopTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void start() {
        mStartTime = System.currentTimeMillis(); //get current time
        if (mStopTime == 0 || mStopTime < mStartTime) {  // Check to see if it is already set .ie the timer is only
            // Starting if activity is recreated.
            mStopTime = mStartTime + mTime;

            // add notifications for each hops addition and boil end
            addAlarmNotifications();
        }
        isRunning = true;
    }

    public void pause() {
        mTime = mStopTime - System.currentTimeMillis(); //find the remaining time
        mStopTime = 0; // reset the ending time to undefined
        isRunning = false;

        // Cancel all notifications for each hops addition and boil end
        removeAlarmNotifications();
    }

    public void stop() {
        isRunning = false;
    }

    public void reset() {
        mStartTime = 0;
        mStopTime = 0;
        mTime = HopTimerActivity.DEFAULT_BOIL_TIME * MIN_TO_MILLIS;

    }

    public void addAlarmNotifications() {
        int alarmId = 0;
        String message = "";
        long atMillis;
        long atMillisPrev = 0;
        long now = System.currentTimeMillis() - 1000;
        for (Hops hop : mHopsList) {
            atMillis = mStopTime - hop.getBoilTime() * MIN_TO_MILLIS;
            if (atMillis >= now) { // skip alarms that should already be past (if paused/reset)
                if (atMillis == atMillisPrev) {  // if at same time as previous hop addition collapse into single alarm.
                    message += "\n" + hop.toString();
                    setAlarm(alarmId-1, atMillis, message);
                } else {
                    message = hop.toString();
                    setAlarm(alarmId++, atMillis, message);
                }
            }
            atMillisPrev = atMillis;
        }
        setAlarm(alarmId, mStopTime, "End of Boil. Please turn burner off.");
        mNumberOfAlarms = alarmId + 1;
    }

    private void setAlarm(int requestCode, long atMillis, String message) {
        Log.d(TAG, "Adding alarm " + requestCode + " for: " + message);
        Intent alarmIntent = new Intent(mContext, HopAlarmReceiver.class);
        alarmIntent.putExtra(HOPS_TO_ADD_EXTRA, message);
        alarmIntent.putExtra(ALARM_ID_EXTRA, requestCode);
        PendingIntent pending = PendingIntent.getBroadcast(mContext, requestCode,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                atMillis, pending);
    }

    public void removeAlarmNotifications() {
        for (int alarmId = 0; alarmId < mNumberOfAlarms; alarmId++) {
            cancelAlarm(alarmId);
        }
        mNumberOfAlarms = 0;
    }

    public void resetAlarmNotifications() {
        if (isRunning) {
            removeAlarmNotifications();
            addAlarmNotifications();
        }
    }

    private void cancelAlarm(int requestCode) {
        Log.d(TAG, "Canceling alarm " + requestCode );
        Intent alarmIntent = new Intent(mContext, HopAlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(mContext, requestCode,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(pending);
    }

    public void saveToSharedPrefs() {
        Log.d(TAG, "Saving Timer...");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(BOIL_TIME_KEY, mTime);
        editor.putLong(BOIL_START_KEY, mStartTime);
        editor.putLong(BOIL_END_KEY, mStopTime);
        editor.putBoolean(IS_RUNNING_KEY, isRunning);
        editor.putInt(NUMBER_OF_ALARMS_KEY, mNumberOfAlarms);
        editor.apply();
    }

    public void loadFromSavedPrefs() {
        Log.d(TAG, "Loading Timer...");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        long now = System.currentTimeMillis();
        mStartTime = prefs.getLong(BOIL_START_KEY, 0);
        mStopTime = prefs.getLong(BOIL_END_KEY, 0);
        if ( now - mStartTime > 6 * 60 * MIN_TO_MILLIS || now > mStopTime) { // if timer is more than 6 hr old
            deleteFromSavedPrefs();                            // or done assume we're starting over
            mStartTime = 0;
            mStopTime = 0;
        }
        isRunning = prefs.getBoolean(IS_RUNNING_KEY, false);
        if (isRunning) {
            mTime = mStopTime - now;
            if (mTime < 0 ) {
                mTime = 0;
                isRunning = false;
            }
        } else {
            mTime = prefs.getLong(BOIL_TIME_KEY, HopTimerActivity.DEFAULT_BOIL_TIME * MIN_TO_MILLIS);
        }
        mNumberOfAlarms = prefs.getInt(NUMBER_OF_ALARMS_KEY, 0);
    }

    public void deleteFromSavedPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(BOIL_TIME_KEY);
        editor.remove(BOIL_START_KEY);
        editor.remove(BOIL_END_KEY);
        editor.remove(IS_RUNNING_KEY);
        editor.remove(NUMBER_OF_ALARMS_KEY);
        editor.apply();
    }
}