package org.opendataspace.android.app.sync;

import org.alfresco.mobile.android.application.commons.utils.ConnectivityUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.format.DateUtils;

public class OdsSyncReceiver extends BroadcastReceiver
{
    private static final String SYNC_START = "org.opendataspace.android.app.sync.SYNC_START";

    public static final String[] SYNC_SOURCES =
        {Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_MOVIES, Environment.DIRECTORY_DCIM};

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();

        if (action == Intent.ACTION_BOOT_COMPLETED)
            startWatcher(context);
        else if (action == SYNC_START)
        {
            if (!ConnectivityUtils.isWifiAvailable(context))
                reschedule(context, getPendingInetent(context));
            else
                startWorker(context);
        }
    }

    public static void startWatcher(Context context)
    {
        final Intent i = new Intent(OdsSyncWatcherService.class.getCanonicalName());
        i.setClass(context, OdsSyncWatcherService.class);
        context.startService(i);
    }

    public static void startWorker(Context context)
    {
        final Intent i = new Intent(OdsSyncWorkerService.class.getCanonicalName());
        i.setClass(context, OdsSyncWorkerService.class);
        context.startService(i);
    }

    public static void reschedule(Context context, PendingIntent intent)
    {
        final AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15 * DateUtils.MINUTE_IN_MILLIS, intent);
    }

    public static PendingIntent getPendingInetent(Context context)
    {
        final Intent in = new Intent(context, OdsSyncReceiver.class);
        in.setAction(OdsSyncReceiver.SYNC_START);
        return PendingIntent.getBroadcast(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}