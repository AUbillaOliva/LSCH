package cl.afubillaoliva.lsch.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import cl.afubillaoliva.lsch.services.DownloadService;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class NotificationReceiver extends BroadcastReceiver {

    private NotificationReceiver.Receiver receiver;

    public interface Receiver {
        void onReceive(Context context, Intent intent);
    }

    public void setReceiver(NotificationReceiver.Receiver receiver){
        this.receiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        final SharedPreference mSharedPreferences = new SharedPreference(context);
        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        final String list = intent.getStringExtra("list");
        if (receiver != null)
            receiver.onReceive(context, intent);
        context.stopService(new Intent(context, DownloadService.class));
        mSharedPreferences.deleteDownloads(list);
        notificationManagerCompat.cancel(DownloadService.SERVICE_ID);
    }
}
