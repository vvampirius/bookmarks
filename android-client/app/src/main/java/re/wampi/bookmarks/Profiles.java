package re.wampi.bookmarks;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Profiles {
    public ArrayList<Profile> profiles;
    private BookmarksStoreDbHelper dbHelper;
    public int selected = -1;
    private SharedPreferences.Editor settingsEditor;

    Profiles(Context context, SharedPreferences.Editor e){
        dbHelper = new BookmarksStoreDbHelper(context);
        settingsEditor = e;
        reloadProfiles();
    }

    public void reloadProfiles(){
        profiles = new ArrayList<Profile>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+BookmarksStore.Profile.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Profile profile = new Profile(dbHelper,
                        cursor.getString(cursor.getColumnIndex(BookmarksStore.Profile.COLUMN_NAME_URL_ID)),
                        cursor.getString(cursor.getColumnIndex(BookmarksStore.Profile.COLUMN_NAME_GET)),
                        cursor.getString(cursor.getColumnIndex(BookmarksStore.Profile.COLUMN_NAME_ADD)),
                        cursor.getString(cursor.getColumnIndex(BookmarksStore.Profile.COLUMN_NAME_LIST))
                );
                profiles.add(profile);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public String[] getIDs(){
        int profilesCount = profiles.size();
        String[] IDs  = new String[profilesCount];
        for(int i=0; i<=(profilesCount-1); i++) {
            IDs[i] = profiles.get(i).url;
        }
        return IDs;
    }

    public Profile getProfileByURL(String url){
        for (Profile profile: profiles) {
            if (profile.url.equals(url)){
                return profile;
            }
        }
        return null;
    }

    public Profile getSelectedProfile(){
        if (selected >= 0 && profiles.size() < selected+1){
            System.out.print("Selected out of range!");
            selected = -1;
        }
        if (selected >= 0){
            return profiles.get(selected);
        }
        return null;
    }

    public boolean setSelectedProfile(Profile profile){
        int n = profiles.indexOf(profile);
        if (n >= 0){
            selected = n;
            Profile selectedProfile = getSelectedProfile();
            settingsEditor.putString("selectedProfile", selectedProfile.url);
            settingsEditor.apply();
            return true;
        }
        return false;
    }

    public boolean setSelectedProfileByURL(String url){
        Profile profile = getProfileByURL(url);
        if (profile != null){
            return setSelectedProfile(profile);
        }
        return false;
    }

    public boolean removeProfile(Profile profile){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(BookmarksStore.Profile.TABLE_NAME,
                BookmarksStore.Profile.COLUMN_NAME_URL_ID+" = ?", new String[]{ profile.url });
        db.close();
        if (count > 0 && profiles.remove(profile)){
            return true;
        }
        return false;
    }

    public boolean addProfile(String url, String getPassword, String addPassword, String listPassword){
        if (url.length()>3){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BookmarksStore.Profile.COLUMN_NAME_URL_ID, url);
            values.put(BookmarksStore.Profile.COLUMN_NAME_GET, getPassword);
            values.put(BookmarksStore.Profile.COLUMN_NAME_ADD, addPassword);
            values.put(BookmarksStore.Profile.COLUMN_NAME_LIST, listPassword);
            long newRowId = db.insert(BookmarksStore.Profile.TABLE_NAME, null, values);
            db.close();
            if (newRowId >= 0){
                reloadProfiles();
                return true;
            }
        }
        return false;
    }

}
