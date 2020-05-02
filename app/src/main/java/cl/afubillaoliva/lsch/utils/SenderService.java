package cl.afubillaoliva.lsch.utils;

import android.content.Context;
import android.util.Log;

import org.acra.data.CrashReportData;
import org.acra.sender.ReportSender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    private Context context;

    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData report){

        this.context = context;

        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = null;

        try {
            try {
                final JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromAsset()));
                final JSONArray acraSettings = obj.getJSONArray("acra-settings");

                for (int i = 0; i < acraSettings.length(); i++) {
                    final JSONObject acraProperties = acraSettings.getJSONObject(i);
                    final String user = acraProperties.getString("acra-user");
                    final String pass = acraProperties.getString("acra-password");

                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", "ACRA-report-stacktrace.json", RequestBody.create(MultipartBody.FORM, report.toJSON()))
                            .addFormDataPart("user", user)
                            .addFormDataPart("pass", pass)
                            .build();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
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

    private String loadJSONFromAsset() {
        String json;
        try {
            final InputStream is = context.getAssets().open("ACRA-SETTINGS.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}

