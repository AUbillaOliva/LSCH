package cl.afubillaoliva.lsch.utils.databases;

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

    private SQLiteOpenHelper dbhandler = null;
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

    public boolean exists(String key){

        final String[] projection = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_ID,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS,
                FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS,
                FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS,
                FavoriteContract.FavoriteEntry.COLUMN_CATEGORY,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_IMAGES

        };
        final String selection = FavoriteContract.FavoriteEntry.COLUMN_WORD_ID + " =?";
        final String[] selectionArgs = { key };
        final String limit = "1";

        db = this.getWritableDatabase();

        final Cursor cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        final boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private static String strSeparator = "__,__";

    private static String convertArrayToString(ArrayList<String> array){
        final StringBuilder str = new StringBuilder();
        for (int i = 0;i< array.size(); i++) {
            str.append(array.get(i));
            // Do not append comma at the end of last element
            if(i<array.size()-1)
                str.append(strSeparator);
        }
        return str.toString();
    }

    private static ArrayList<String> convertStringToArray(String str){
        final ArrayList<String> list = new ArrayList<>();
        if(str != null){
            final String[] arr = str.split(strSeparator);
            Collections.addAll(list, arr);
            return  list;
        } else
            return list;

    }

    public void addFavorite(Word word){
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE, word.getTitle());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_WORD_ID, word.getTitle());

        final String descriptions;
        if(word.getDescription() != null)
            descriptions = convertArrayToString(word.getDescription());
        else
            descriptions = null;

        if(descriptions != null)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS, descriptions);

        final String synonyms;
        if(word.getSin() != null)
            synonyms = convertArrayToString(word.getSin());
        else
            synonyms = null;
        if(synonyms != null)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS, synonyms);

        final String antonyms;
        if(word.getAnt() != null)
            antonyms = convertArrayToString(word.getAnt());
        else
            antonyms = null;
        if(antonyms != null)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS, antonyms);

        final String categories;
        if(word.getCategory() != null)
            categories = convertArrayToString(word.getCategory());
        else
            categories = null;
        if(categories != null)
            values.put(FavoriteContract.FavoriteEntry.COLUMN_CATEGORY, categories);

        final String images;
        if(word.getImages() != null)
            images = convertArrayToString(word.getImages());
        else
            images = null;
        if(images != null)
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