package cl.afubillaoliva.lsch.tools;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.services.DownloadService;
import cl.afubillaoliva.lsch.utils.databases.DownloadDatabaseHelper;

public class DownloadReceiver extends ResultReceiver {

    private static DownloadDatabaseHelper mDownloadDatabaseHelper;
    private Receiver receiver;

    public DownloadReceiver(Handler handler, Context context){
        super(handler);
        mDownloadDatabaseHelper = new DownloadDatabaseHelper(context);
    }

    public interface Receiver{
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);

        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }

        final Word word = (Word) resultData.getSerializable("data");

        if (resultCode == DownloadService.SERVICE_ID) {
            int progress = resultData.getInt("progress");

            if (progress == 100) {
                if(word != null) {
                    mDownloadDatabaseHelper.addDownload(word);
                }
            }
        }
    }

}
