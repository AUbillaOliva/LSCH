package cl.afubillaoliva.lsch.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.MainApplication;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class DownloadService extends JobIntentService {


    public final static int SERVICE_ID = 371492;
    private static int fileLength = 0, maxProgress = 0;
    private static int i = 0;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notification;
    private SharedPreference mSharedPreferences;
    private Context context;

    public static void enqueueWork(Context context, Intent intent){
        enqueueWork(context, DownloadService.class, SERVICE_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(MainActivity.DOWN, "onCreate");

        context = MainApplication.getContext();

        mSharedPreferences = new SharedPreference(context);
        notificationManagerCompat = NotificationManagerCompat.from(context);

        notification = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setContentTitle("Descarga")
                .setShowWhen(true)
                .setContentText("Descarga en progreso...")
                .setPriority(NotificationCompat.PRIORITY_LOW);
        if(mSharedPreferences.loadNightModeState())
            notification.setSmallIcon(R.drawable.ic_app_icon_light);
        else
            notification.setSmallIcon(R.drawable.ic_app_icon_dark);

        notificationManagerCompat.notify(SERVICE_ID, notification.build());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        final Bundle bundle = intent.getExtras();
        assert bundle != null;
        final Word word = (Word) bundle.getSerializable("data");

        maxProgress = intent.getIntExtra("maxProgress", 0);

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        try {
            assert word != null;
            final URL url = new URL(word.getImages().get(0));
            final URLConnection connection = url.openConnection();
            connection.connect();

            fileLength += connection.getContentLength();

            final InputStream input = new BufferedInputStream(connection.getInputStream());

            final OutputStream output = new FileOutputStream(getExternalFilesDir(null) + File.separator + stripAccents(word.getTitle()) + ".mp4");

            final byte[] data = new byte[1024];
            long total = 0;
            int count;
            notification.setContentText(i + " de " + maxProgress + " (" + (i*100)/maxProgress + "% Completado)");
            notificationManagerCompat.notify(SERVICE_ID, notification.build());
            while ((count = input.read(data)) != -1) {
                total += count;
                final Bundle resultData = new Bundle();
                resultData.putInt("progress" ,(int) (total * 100 / fileLength));
                assert receiver != null;
                receiver.send(SERVICE_ID, resultData);
                output.write(data, 0, count);
            }
            i++;
            output.flush();
            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        final Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        resultData.putSerializable("data", word);
        resultData.putInt("maxProgress", maxProgress);

        assert receiver != null;
        receiver.send(SERVICE_ID, resultData);
    }

    public static String stripAccents(String s){
        s = s.replaceAll("/", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(MainActivity.DOWN, "onDestroy");
        maxProgress = 0;
        i = 0;
        notificationManagerCompat.cancel(SERVICE_ID);
        notification = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setShowWhen(false)
                .setContentTitle("Descarga completada")
                .setAutoCancel(true)
                .setShowWhen(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        if(mSharedPreferences.loadNightModeState())
            notification.setSmallIcon(R.drawable.ic_app_icon_light);
        else
            notification.setSmallIcon(R.drawable.ic_app_icon_dark);

        notificationManagerCompat.notify(SERVICE_ID, notification.build());

    }

    @Override
    public boolean onStopCurrentWork() {
        Log.e(MainActivity.DOWN, "onStopCurrentWork");
        return super.onStopCurrentWork();
    }
}
