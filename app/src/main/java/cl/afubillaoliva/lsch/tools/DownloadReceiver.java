package cl.afubillaoliva.lsch.tools;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DownloadReceiver extends ResultReceiver {

    private Receiver receiver;

    public DownloadReceiver(Handler handler){
        super(handler);
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

    }



}