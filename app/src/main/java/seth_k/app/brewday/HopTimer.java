package seth_k.app.brewday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

public class HopTimer implements Parcelable {
    public static final long MIN_TO_MILLIS = 60000;
    public static final long SEC_TO_MILLIS = 1000;
    public static final String BOIL_TIME_KEY = "hop_timer_boil_time";
    public static final String BOIL_START_KEY = "hop_timer_boil_start";
    public static final String BOIL_END_KEY = "hop_timer_boil_end";
    public static final String IS_RUNNING_KEY = "hop_timer_is_running";
    public static final String NUMBER_OF_ALARMS_KEY = "hop_timer_num_alarms";

    public static final String HOPS_TO_ADD_EXTRA = "HOPS_TO_ADD";
    public static final String ALARM_ID_EXTRA = "ALARM_ID";

    private List<Hops> mHopsList;
    private Context mContext;
    private long mBoilTime; // Time to end of boil in millisec from start or last pause
    private long mBoilStartTime;
    private long mBoilStopTime; // Time of end of boil in system clock time
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

    public static Parcelable.Creator<HopTimer> CREATOR = new Parcelable.Creator<HopTimer>() {
        @Override
        public HopTimer createFromParcel(Parcel parcel) {
            return new HopTimer(parcel);
        }

        @Override
        public HopTimer[] newArray(int i) {
            return new HopTimer[i];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mBoilTime);
        parcel.writeLong(mBoilStartTime);
        parcel.writeLong(mBoilStopTime);
        parcel.writeInt(isRunning ? 1 : 0);
    }

    public HopTimer(Parcel p) {
        mBoilTime = p.readLong();
        mBoilStartTime = p.readLong();
        mBoilStopTime = p.readLong();
        isRunning = p.readInt() != 0;
    }

    public long getBoilTime() {
        return mBoilTime;
    }

    public void setBoilTime(long boilTime) {
        mBoilTime = boilTime;
    }

    public long getBoilStopTime() {
        return mBoilStopTime;
    }

    public void setBoilStopTime(long boilStopTime) {
        mBoilStopTime = boilStopTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public AlarmManager getAlarmManager() {
        return mAlarmManager;
    }

    public void setAlarmManager(AlarmManager alarmManager) {
        mAlarmManager = alarmManager;
    }

    public void start() {
        mBoilStartTime = SystemClock.elapsedRealtime(); //get current time
        if (mBoilStopTime == 0) {  // Check to see if it is already set .ie the timer is only
            // Starting if activity is recreated.
            mBoilStopTime = mBoilStartTime + mBoilTime;

            // add notifications for each hops addition and boil end
            addAlarmNotifications();
        }
        isRunning = true;
    }

    public void pause() {
        mBoilTime = mBoilStopTime - SystemClock.elapsedRealtime(); //find the remaining time
        mBoilStopTime = 0; // reset the ending time to undefined
        isRunning = false;

        // Cancel all notifications for each hops addition and boil end
        removeAlarmNotifications();
    }

    public void addAlarmNotifications() {
        int alarmId = 0;
        String message = "";
        long atMillis;
        long atMillisPrev = 0;
        long now = SystemClock.elapsedRealtime() - 1000;
        for (Hops hop : mHopsList) {
            atMillis = mBoilStopTime - hop.getBoilTime() * MIN_TO_MILLIS;
            if (atMillis < now ) {
                // skip alarms that should already be past (if paused/reset)
            } else if (atMillis == atMillisPrev) {  // if at same time as previous hop addition collapse into single alarm.
                message += "\n" + hop.toString();
                setAlarm(alarmId-1, atMillis, message);
            } else {
                message = hop.toString();
                setAlarm(alarmId++, atMillis, message);
            }
            atMillisPrev = atMillis;
        }
        setAlarm(alarmId, mBoilStopTime, "End of Boil. Please turn burner off.");
        mNumberOfAlarms = alarmId + 1;
    }

    private void setAlarm(int requestCode, long atMillis, String message) {
        Log.d(HopTimerActivity.TAG, "Adding alarm " + requestCode + " for: " + message);
        Intent alarmIntent = new android.content.Intent(mContext, seth_k.app.brewday.HopAlarmReceiver.class);
        alarmIntent.putExtra(HOPS_TO_ADD_EXTRA, message);
        alarmIntent.putExtra(ALARM_ID_EXTRA, requestCode);
        PendingIntent pending = PendingIntent.getBroadcast(mContext, requestCode,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
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
        Log.d(HopTimerActivity.TAG, "Canceling alarm " + requestCode );
        Intent alarmIntent = new Intent(mContext, HopAlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(mContext, requestCode,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(pending);
    }

    public void saveToSharedPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(BOIL_TIME_KEY, mBoilTime);
        editor.putLong(BOIL_START_KEY, mBoilStartTime);
        editor.putLong(BOIL_END_KEY, mBoilStopTime);
        editor.putBoolean(IS_RUNNING_KEY, isRunning);
        editor.putInt(NUMBER_OF_ALARMS_KEY, mNumberOfAlarms);
        editor.apply();
    }

    public void loadFromSavedPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        long now = SystemClock.elapsedRealtime();
        mBoilStartTime = prefs.getLong(BOIL_START_KEY, 0);
        if ( now - mBoilStartTime > 6 * 60 * MIN_TO_MILLIS ) { // if timer is more than 6 hr old
            deleteFromSavedPrefs();                            // assume we're starting over
            mBoilStartTime = 0;
        }
        mBoilStopTime = prefs.getLong(BOIL_END_KEY, 0);
        mBoilTime = prefs.getLong(BOIL_TIME_KEY, HopTimerActivity.DEFAULT_BOIL_TIME * MIN_TO_MILLIS);
        isRunning = prefs.getBoolean(IS_RUNNING_KEY, false);
        if (isRunning) {
            mBoilTime = mBoilStopTime - now;
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
        editor.commit();
    }
}