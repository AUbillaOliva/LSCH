package cl.afubillaoliva.lsch.utils.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 2;

    private SQLiteOpenHelper dbHandler = null;

    public HistoryDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        final SQLiteDatabase db = dbHandler.getReadableDatabase();
    }

    public void close(){
        dbHandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryContract.HistoryEntry.TABLE_NAME + " (" +
                HistoryContract.HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HistoryContract.HistoryEntry.COLUMN_HISTORY_ID + " TEXT, " +
                HistoryContract.HistoryEntry.COLUMN_HISTORY_TITLE + " TEXT" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HistoryContract.HistoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addHistory(String string){
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(HistoryContract.HistoryEntry.COLUMN_HISTORY_TITLE, string);
        values.put(HistoryContract.HistoryEntry.COLUMN_HISTORY_ID, string);

        db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteHistory(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HistoryContract.HistoryEntry.TABLE_NAME, HistoryContract.HistoryEntry.COLUMN_HISTORY_ID + "='" + title + "'", null);
    }

    public ArrayList<String> getHistory(){
        final String[] columns = {
                HistoryContract.HistoryEntry._ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_TITLE
        };
        final String sortOrder = HistoryContract.HistoryEntry._ID + " ASC";
        final ArrayList<String> historyList = new ArrayList<>();

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(HistoryContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);
        if(cursor.moveToLast()){
            do {
                String title;
                title = cursor.getString(cursor.getColumnIndex(HistoryContract.HistoryEntry.COLUMN_HISTORY_TITLE));
                historyList.add(title);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        db.close();

        return historyList;
    }
}
