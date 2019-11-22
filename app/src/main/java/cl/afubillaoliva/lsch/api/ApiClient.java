package cl.afubillaoliva.lsch.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    /**
     * THIS APP USES LSCH-API, AN OPEN SOURCE API FOR THE CHILEAN SIGN LANGUAGE
     * MORE INFO ABOUT THE API ON https://github.com/AUbillaOliva/LSCH-Api/blob/master/README.md
     * LICENCE GPL-3.0
     */

    private static final String BASE_URL = "https://lsch-api.herokuapp.com/api/";

    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
