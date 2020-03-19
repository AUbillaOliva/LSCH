package cl.afubillaoliva.lsch.tools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import cl.afubillaoliva.lsch.MainActivity;

public class DownloadService extends JobIntentService {

    private final static int SERVICE_ID = 371492;
    private static boolean isCompleted = false;
    private static int position = 0;

    public static void enqueueWork(Context context, Intent intent){
        enqueueWork(context, DownloadService.class, SERVICE_ID, intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(MainActivity.DOWN, "onCreate");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e(MainActivity.DOWN, "onHandleWork of " + intent.getStringExtra("fileName"));
        startDownload(intent.getStringExtra("url"), intent.getStringExtra("fileName"));
        position++;
        isCompleted = false;
    }

    public static int getPosition(){ return  position; }

    public static String stripAccents(String s){
        s = s.replaceAll("/", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private static void downloadFile(String url, File outputFile){
        try {
            final URL u = new URL(url);
            final URLConnection conn = u.openConnection();
            final int contentLength = conn.getContentLength();

            final DataInputStream stream = new DataInputStream(u.openStream());

            final byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            final DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
            isCompleted = true;
        } catch(IOException ignored){
        }
    }

    public static boolean isCompleted() { return isCompleted; }

    public void startDownload(String url, String fileName) {
        final File file = new File(getExternalFilesDir(null) + File.separator + stripAccents(fileName) + ".mp4");
        if(!file.exists())
            downloadFile(url, new File(getExternalFilesDir(null) + File.separator + stripAccents(fileName) + ".mp4"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(MainActivity.DOWN, "onDestroy");
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.e(MainActivity.DOWN, "onStopCurrentWork");
        return super.onStopCurrentWork();
    }
}
