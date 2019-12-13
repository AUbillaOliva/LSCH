package cl.afubillaoliva.lsch.api;

import android.util.Log;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.models.Abecedary;
import cl.afubillaoliva.lsch.models.Expressions;
import cl.afubillaoliva.lsch.models.Word;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class ApiService {

    public interface AbecedaryService{
        @GET("letters")
        Call<ArrayList<Abecedary>> getAbecedary();
    }

    public interface WordsService {
        @GET("words")
        Call<ArrayList<Word>> getWords(@Query("letter") String letter, @Query("category") String category);
    }

    public interface WordService {
        @Streaming
        @GET
        Call<ResponseBody> getVideo(@Url String fileUrl);
    }

    public interface ExpressionsServiceCategories {
        @GET("expressions")
        Call<ArrayList<Expressions>> getExpressionsOfCategories(@Query("category") String category);
    }

    public interface ExpressionsCategoryService {
        @GET("expressions/categories")
        Call<ArrayList<String>> getExpressionsCategories();
    }

    public interface ThemesCategoryService {
        @GET("words/categories")
        Call<ArrayList<String>> getThemesCategories();
    }

}
