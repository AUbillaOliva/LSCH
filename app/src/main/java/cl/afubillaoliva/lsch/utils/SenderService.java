package cl.afubillaoliva.lsch.utils;

import android.content.Context;
import android.util.Log;

import org.acra.data.CrashReportData;
import org.acra.sender.ReportSender;
import org.json.JSONException;

import java.io.IOException;

import androidx.annotation.NonNull;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.api.ApiClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SenderService implements ReportSender {

    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData report){
        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = null;

        try {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "ACRA-report-stacktrace.json", RequestBody.create(MultipartBody.FORM, report.toJSON()))
                    .addFormDataPart("user", "ecdd7446cd389d")
                    .addFormDataPart("pass","fce0b8515205f4")
                    .build();
        } catch (JSONException e){
            e.printStackTrace();
        }
        final Request request = new Request.Builder()
                .url(ApiClient.REPORT_URL)
                .post( requestBody )
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e){
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                if(!response.isSuccessful())
                    Log.e(MainActivity.REP, response.message());
                Log.d(MainActivity.REP, String.valueOf(call.isExecuted()));
            }
        });


    }


}

