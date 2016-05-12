package re.wampi.bookmarks;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Profile {
    public String url, getPassword, addPassword, listPassword;
    private BookmarksStoreDbHelper dbHelper;
    private String id;

    Profile(BookmarksStoreDbHelper _dbHelper, String _url, String _getPassword,
            String _addPassword, String _listPassword){
        dbHelper = _dbHelper;
        id = _url;
        url = _url;
        getPassword = _getPassword;
        addPassword = _addPassword;
        listPassword = _listPassword;
    }

    public boolean save(){
        if (url.length()>3){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BookmarksStore.Profile.COLUMN_NAME_URL_ID, url);
            values.put(BookmarksStore.Profile.COLUMN_NAME_GET, getPassword);
            values.put(BookmarksStore.Profile.COLUMN_NAME_ADD, addPassword);
            values.put(BookmarksStore.Profile.COLUMN_NAME_LIST, listPassword);
            int count = db.update(BookmarksStore.Profile.TABLE_NAME, values,
                    BookmarksStore.Profile.COLUMN_NAME_URL_ID+" = ?", new String[]{ id });
            db.close();
            if (count > 0){
                id = url;
                return true;
            }
        }
        return false;
    }
}
