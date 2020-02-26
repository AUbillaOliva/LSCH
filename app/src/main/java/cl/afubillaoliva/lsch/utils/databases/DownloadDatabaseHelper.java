package cl.afubillaoliva.lsch.utils.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

import cl.afubillaoliva.lsch.models.Word;

public class DownloadDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "download.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteOpenHelper dbhandler = null;
    private SQLiteDatabase db;

    public DownloadDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() { db = dbhandler.getWritableDatabase(); }

    public void close() { dbhandler.close(); }

    public boolean exists(String key){

        final String[] projection = {
                DownloadContract.DownloadEntry._ID,
                DownloadContract.DownloadEntry.COLUMN_WORD_ID,
                DownloadContract.DownloadEntry.COLUMN_WORD_TITLE,
                DownloadContract.DownloadEntry.COLUMN_WORD_DESCRIPTIONS,
                DownloadContract.DownloadEntry.COLUMN_ANTONYMS,
                DownloadContract.DownloadEntry.COLUMN_SYNONYMS,
                DownloadContract.DownloadEntry.COLUMN_CATEGORY,
                DownloadContract.DownloadEntry.COLUMN_WORD_IMAGES

        };
        final String selection = DownloadContract.DownloadEntry.COLUMN_WORD_ID + " =?";
        final String[] selectionArgs = { key };
        final String limit = "1";

        db = this.getWritableDatabase();

        final Cursor cursor = db.query(DownloadContract.DownloadEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        final boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        final String SQL_CREATE_DOWNLOAD_TABLE = "CREATE TABLE " + DownloadContract.DownloadEntry.TABLE_NAME + " (" +
                DownloadContract.DownloadEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DownloadContract.DownloadEntry.COLUMN_WORD_ID + " TEXT, " +
                DownloadContract.DownloadEntry.COLUMN_WORD_TITLE + " TEXT NOT NULL, " +
                DownloadContract.DownloadEntry.COLUMN_WORD_DESCRIPTIONS + " TEXT, " +
                DownloadContract.DownloadEntry.COLUMN_SYNONYMS + " TEXT, " +
                DownloadContract.DownloadEntry.COLUMN_ANTONYMS + " TEXT, " +
                DownloadContract.DownloadEntry.COLUMN_CATEGORY + " TEXT, " +
                DownloadContract.DownloadEntry.COLUMN_WORD_IMAGES + " TEXT" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_DOWNLOAD_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il){

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DownloadContract.DownloadEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    private static String strSeparator = "__,__";

    private static String convertArrayToString(ArrayList<String> array){
        final StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.size(); i++){
            str.append(array.get(i));
            if(i < array.size() - 1){
                str.append(strSeparator);
            }
        }
        return str.toString();
    }

    private static ArrayList<String> convertStringToArray(String str){
        final ArrayList<String> list = new ArrayList<>();
        if(str != null){
            final String[] arr = str.split(strSeparator);
            Collections.addAll(list, arr);
            return  list;
        } else {
            return list;
        }

    }

    public void addDownload(Word word){

        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(DownloadContract.DownloadEntry.COLUMN_WORD_TITLE, word.getTitle());
        values.put(DownloadContract.DownloadEntry.COLUMN_WORD_ID, word.getTitle());

        final String descriptions = convertArrayToString(word.getDescription());
        if(descriptions.length() != 0)
            values.put(DownloadContract.DownloadEntry.COLUMN_WORD_DESCRIPTIONS, descriptions);

        final String synonyms = convertArrayToString(word.getSin());
        if(synonyms.length() != 0)
            values.put(DownloadContract.DownloadEntry.COLUMN_SYNONYMS, synonyms);

        final String antonyms = convertArrayToString(word.getAnt());
        if(antonyms.length() != 0)
            values.put(DownloadContract.DownloadEntry.COLUMN_ANTONYMS, antonyms);


        final String categories = convertArrayToString(word.getCategory());
        if(categories.length() != 0)
            values.put(DownloadContract.DownloadEntry.COLUMN_CATEGORY, categories);

        final String images = convertArrayToString(word.getImages());
        if(images.length() != 0)
            values.put(DownloadContract.DownloadEntry.COLUMN_WORD_IMAGES, images);

        db.insert(DownloadContract.DownloadEntry.TABLE_NAME, null, values);
        db.close();

    }

    public void deleteDownload(String title){

        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DownloadContract.DownloadEntry.TABLE_NAME, DownloadContract.DownloadEntry.COLUMN_WORD_TITLE + "='" + title + "'", null);

    }

    public ArrayList<Word> getAllDownload(){

        final String[] columns = {
                DownloadContract.DownloadEntry._ID,
                DownloadContract.DownloadEntry.COLUMN_WORD_ID,
                DownloadContract.DownloadEntry.COLUMN_WORD_TITLE,
                DownloadContract.DownloadEntry.COLUMN_WORD_DESCRIPTIONS,
                DownloadContract.DownloadEntry.COLUMN_ANTONYMS,
                DownloadContract.DownloadEntry.COLUMN_SYNONYMS,
                DownloadContract.DownloadEntry.COLUMN_CATEGORY,
                DownloadContract.DownloadEntry.COLUMN_WORD_IMAGES

        };
        final String sortOrder =
                DownloadContract.DownloadEntry._ID + " ASC";
        final ArrayList<Word> downloadList = new ArrayList<>();

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(DownloadContract.DownloadEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                final Word word = new Word();
                word.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE)));
                word.setDescription(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS))));
                word.setSin(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS))));
                word.setAnt(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS))));
                word.setCategory(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_CATEGORY))));
                word.setImages(convertStringToArray(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_WORD_IMAGES))));

                downloadList.add(word);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return downloadList;

    }

}
