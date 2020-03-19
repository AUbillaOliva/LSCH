package cl.afubillaoliva.lsch.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context, "Descarga abortada", Toast.LENGTH_SHORT).show();
        context.stopService(intent);
    }
}
