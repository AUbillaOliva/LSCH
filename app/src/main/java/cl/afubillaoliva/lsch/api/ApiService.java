package cl.afubillaoliva.lsch.api;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.models.Abecedary;
import cl.afubillaoliva.lsch.models.Word;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ApiService {

    public interface AbecedaryService {
        @GET("letters")
        Call<ArrayList<Abecedary>> getAbecedary();
    }

    public interface WordsService {
        @GET("words")
        Call<ArrayList<Word>> getWords(@Query("letter") String letter, @Query("category") String category);
    }

    public interface ExpressionsServiceCategories {
        @GET("expressions")
        Call<ArrayList<Word>> getExpressionsOfCategories(@Query("category") String category);
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
