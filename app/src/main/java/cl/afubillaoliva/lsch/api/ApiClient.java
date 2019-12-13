package cl.afubillaoliva.lsch.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    /**
     * THIS APP USES LSCH-API, AN OPEN SOURCE API FOR THE CHILEAN SIGN LANGUAGE
     * MORE INFO ABOUT THE API ON https://github.com/AUbillaOliva/LSCH/blob/master/README.md
     * CREATED BY {@author}: ÁLVARO UBILLA OLIVA
     * @version: v1.0.1
     * @license GPL-3.0
     */

    private static final String BASE_URL = "https://lsch-api.herokuapp.com/api/";

    public static Retrofit getClient(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

    }
}