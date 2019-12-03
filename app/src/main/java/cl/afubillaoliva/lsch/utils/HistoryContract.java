package cl.afubillaoliva.lsch.utils;

import android.provider.BaseColumns;

public class HistoryContract {

    public static final class HistoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "history";
        public static final String COLUMN_HISTORY_ID = "historyid";
        public static final String COLUMN_HISTORY_TITLE = "title";

    }
}
