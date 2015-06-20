package seth_k.app.brewday;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class HopAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = HopAlarmReceiver.class.getSimpleName();

    protected NotificationCompat.Builder notice;
    protected NotificationManager manager;

    public HopAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received notice for: " + intent.getStringExtra(HopTimerActivity.HOPS_TO_ADD_EXTRA));
        notice = new NotificationCompat.Builder(context);
        notice.setSmallIcon(R.drawable.ic_notify)
                .setContentTitle("Brewing alert")
                .setContentText(intent.getStringExtra(HopTimerActivity.HOPS_TO_ADD_EXTRA))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);
        Intent openTimer = new Intent(context, HopTimerActivity.class);
        openTimer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder ts = TaskStackBuilder.create(context);
        ts.addParentStack(HopTimerActivity.class);
        ts.addNextIntent(openTimer);

        PendingIntent pendingOpenTimer = ts.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notice.setContentIntent(pendingOpenTimer);

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notice.build());
    }
}
