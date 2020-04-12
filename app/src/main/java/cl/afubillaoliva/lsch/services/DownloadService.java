package cl.afubillaoliva.lsch.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.MainApplication;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.tools.NotificationReceiver;
import cl.afubillaoliva.lsch.utils.SharedPreference;

import android.app.IntentService;

import androidx.annotation.Nullable;

public class DownloadService extends IntentService implements NotificationReceiver.Receiver {

    public final static int SERVICE_ID = 371492;
    private static int fileLength = 0;
    private int i = 0, maxProgress = 1;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notification;
    private Intent service;
    private PendingIntent pendingIntent;
    private String list;
    private Context context;

    public DownloadService(){
        super(DownloadService.class.getSimpleName());
    }

    public DownloadService(String list, Context context){
        super(DownloadService.class.getSimpleName());
        this.list = list;
        this.context = context;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        context = MainApplication.getContext();

        final SharedPreference mSharedPreferences = new SharedPreference(context);
        notificationManagerCompat = NotificationManagerCompat.from(context);

        final NotificationReceiver notificationReceiver = new NotificationReceiver();
        notificationReceiver.setReceiver(this);

        service = new Intent(context, NotificationReceiver.class);
        service.putExtra("list", list);
        pendingIntent = PendingIntent.getBroadcast(context, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setContentTitle(context.getResources().getString(R.string.download))
                .setShowWhen(true)
                .setContentText(context.getResources().getString(R.string.download_in_progress))
                .addAction(R.drawable.ic_close_24dp, context.getResources().getString(R.string.negative_dialog_button), pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        if(mSharedPreferences.loadNightModeState())
            notification.setSmallIcon(R.drawable.ic_app_icon_light);
        else
            notification.setSmallIcon(R.drawable.ic_app_icon_dark);

        startForeground(SERVICE_ID, notification.build());
    }

    public static String stripAccents(String s){
        s = s.replaceAll("/", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        assert intent != null;
        final Bundle bundle = intent.getExtras();
        assert bundle != null;
        final Word word = (Word) bundle.getSerializable("data");

        list = bundle.getString("list");
        service.putExtra("list", list);
        pendingIntent = PendingIntent.getBroadcast(context, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);

        maxProgress = intent.getIntExtra("maxProgress", 1);

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        assert word != null;
        final File file = new File(getExternalFilesDir(null) + File.separator + stripAccents(word.getTitle()) + ".mp4");

        notification.setContentText(i + " " + context.getResources().getString(R.string.of) + " " + maxProgress + " (" + (i*100)/ maxProgress + "% " + context.getResources().getString(R.string.completed) +")");
        notificationManagerCompat.notify(SERVICE_ID, notification.build());

        if(!file.exists()){
            try {
                final URL url = new URL(word.getImages().get(0));
                final URLConnection connection = url.openConnection();
                connection.connect();

                fileLength += connection.getContentLength();

                final InputStream input = new BufferedInputStream(connection.getInputStream());

                final OutputStream output = new FileOutputStream(getExternalFilesDir(null) + File.separator + stripAccents(word.getTitle()) + ".mp4");

                final byte[] data = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1){
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

            } catch (IOException e){
                e.printStackTrace();
            }

            final Bundle resultData = new Bundle();
            resultData.putInt("progress" ,100);
            resultData.putSerializable("data", word);
            resultData.putInt("maxProgress", maxProgress);

            assert receiver != null;
            receiver.send(SERVICE_ID, resultData);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        i = 0;
        maxProgress = 1;
        notificationManagerCompat.cancel(SERVICE_ID);
    }

    @Override
    public void onReceive(Context context, Intent intent){}
}
