package cl.afubillaoliva.lsch.api;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.models.Abecedary;
import cl.afubillaoliva.lsch.models.Word;
import retrofit2.Call;
import retrofit2.http.GET;

public class ApiService {

    public interface LettersService{
        @GET("letters")
        Call<ArrayList<Abecedary>> getAbecedary();
    }

    public interface WordsService {
        @GET("words")
        Call<ArrayList<Word>> getWords();
    }

}
