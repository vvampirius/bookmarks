package re.wampi.bookmarks;

import android.provider.BaseColumns;

public final class BookmarksStore {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public BookmarksStore() {}

    /* Inner class that defines the table contents */
    public static abstract class Profile implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_URL_ID = "url";
        public static final String COLUMN_NAME_GET = "getPassword";
        public static final String COLUMN_NAME_ADD = "addPassword";
        public static final String COLUMN_NAME_LIST = "listPassword";

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Profile.TABLE_NAME + " (" +
                        Profile.COLUMN_NAME_URL_ID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                        Profile.COLUMN_NAME_GET + TEXT_TYPE + COMMA_SEP +
                        Profile.COLUMN_NAME_ADD + TEXT_TYPE + COMMA_SEP +
                        Profile.COLUMN_NAME_LIST + TEXT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Profile.TABLE_NAME;

    }

}
