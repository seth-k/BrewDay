package seth_k.app.brewday;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class HopAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = HopAlarmReceiver.class.getSimpleName();

    protected NotificationCompat.Builder notice;
    protected NotificationManagerCompat manager;
    protected long[] vibrator = { 200, 300, 200, 300 };

    public HopAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(HopTimer.HOPS_TO_ADD_EXTRA);
        int alarmId = intent.getIntExtra(HopTimer.ALARM_ID_EXTRA, 0);
        Log.d(TAG, "Received notice for: " + message);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        if (preference.getBoolean("notifications_brew_event", true)) {
            boolean vibrate = preference.getBoolean("notifications_event_vibrate", true);
            // Get preferred ringtone
            String strRingtonePreference = preference.getString("notifications_event_ringtone", "DEFAULT_SOUND");
            Uri ringtone = Uri.parse(strRingtonePreference);

            notice = new NotificationCompat.Builder(context);
            notice.setSmallIcon(R.drawable.ic_notify)
                    .setContentTitle("Brewing alert")
                    .setContentText(message)
                    .setTicker(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSound(ringtone)
                    .setAutoCancel(true);
            if (vibrate) {
                notice.setVibrate(vibrator);
            }
            Intent openTimer = new Intent(context, HopTimerActivity.class);
            openTimer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder ts = TaskStackBuilder.create(context);
            ts.addParentStack(HopTimerActivity.class);
            ts.addNextIntent(openTimer);

            PendingIntent pendingOpenTimer = ts.getPendingIntent(alarmId, PendingIntent.FLAG_UPDATE_CURRENT);
            notice.setContentIntent(pendingOpenTimer);

            manager = NotificationManagerCompat.from(context);
            manager.notify(alarmId, notice.build());
        }
    }
}
