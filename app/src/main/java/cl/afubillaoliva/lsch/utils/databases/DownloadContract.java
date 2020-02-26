package cl.afubillaoliva.lsch.utils.databases;

import android.provider.BaseColumns;

public class DownloadContract {
    public static final class DownloadEntry implements BaseColumns {
        public static final String TABLE_NAME = "download";
        public static final String COLUMN_WORD_ID = "wordid";
        public static final String COLUMN_WORD_TITLE = "title";
        public static final String COLUMN_WORD_DESCRIPTIONS= "descriptions";
        public static final String COLUMN_ANTONYMS = "antonyms";
        public static final String COLUMN_SYNONYMS = "synonyms";
        public static final String COLUMN_CATEGORY = "categories";
        public static final String COLUMN_WORD_IMAGES = "images";
    }
}
