package cl.afubillaoliva.lsch.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

import cl.afubillaoliva.lsch.models.Word;


public class FavoriteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteOpenHelper dbhandler;
    private SQLiteDatabase db;

    public FavoriteDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        db = dbhandler.getWritableDatabase();
    }

    public void close(){
        dbhandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteContract.FavoriteEntry.COLUMN_WORD_ID + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_CATEGORY + " TEXT, " +
                FavoriteContract.FavoriteEntry.COLUMN_WORD_IMAGES + " TEXT" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    private static String strSeparator = "__,__";

    private static String convertArrayToString(ArrayList<String> array){
        StringBuilder str = new StringBuilder();
        for (int i = 0;i< array.size(); i++) {
            str.append(array.get(i));
            // Do not append comma at the end of last element
            if(i<array.size()-1){
                str.append(strSeparator);
            }
        }
        return str.toString();
    }

    private static ArrayList<String> convertStringToArray(String str){
        ArrayList<String> list = new ArrayList<>();
        if(str != null){
            String[] arr = str.split(strSeparator);
            Collections.addAll(list, arr);
            return  list;
        } else {
            return list;
        }

    }

    public void addFavorite(Word word){
        SQLiteDatabase db = this.getWritableDatabase();

        /*COLUMN_WORD_TITLE,
                        COLUMN_WORD_DESCRIPTIONS,
                        COLUMN_ANTONYMS,
                        COLUMN_SYNONYMS,
                        COLUMN_CATEGORY,
                        COLUMN_WORD_IMAGES*/


        ContentValues values = new ContentValues();

        values.put(FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE, word.getTitle());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_WORD_ID, word.getTitle());

        String descriptions = convertArrayToString(word.getDescription());
        if(descriptions.length() != 0)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS, descriptions);

        String synonyms = convertArrayToString(word.getSin());
        if(synonyms.length() != 0)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS, synonyms);

        String antonyms = convertArrayToString(word.getAnt());
        if(antonyms.length() != 0)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS, antonyms);


        String categories = convertArrayToString(word.getCategory());
        if(categories.length() != 0)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_CATEGORY, categories);

        String images = convertArrayToString(word.getImages());
        if(images.length() != 0)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_WORD_IMAGES, images);

        db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteFavorite(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE+ "='" + title + "'", null);
    }

    public ArrayList<Word> getAllFavorite(){
        String[] columns = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_ID,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS,
                FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS,
                FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS,
                FavoriteContract.FavoriteEntry.COLUMN_CATEGORY,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_IMAGES

        };
        String sortOrder =
                FavoriteContract.FavoriteEntry._ID + " ASC";
        ArrayList<Word> favoriteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                Word word = new Word();
                word.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE)));
                word.setDescription(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS))));
                word.setSin(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS))));
                word.setAnt(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS))));
                word.setCategory(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_CATEGORY))));
                word.setImages(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_WORD_IMAGES))));

                favoriteList.add(word);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favoriteList;
    }

}