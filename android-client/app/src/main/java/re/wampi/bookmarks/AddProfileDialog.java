package re.wampi.bookmarks;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddProfileDialog extends DialogFragment implements View.OnClickListener {
    EditText url, getPassword, addPassword, listPassword;
    Button addButton;
    Profiles profiles;
    MainActivity parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_profile_dialog, null);
        url = (EditText) v.findViewById(R.id.url);
        getPassword = (EditText) v.findViewById(R.id.getPassword);
        addPassword = (EditText) v.findViewById(R.id.addPassword);
        listPassword = (EditText) v.findViewById(R.id.listPassword);
        addButton = (Button) v.findViewById(R.id.addButton);
        addButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v){
        if (url.getText().toString().length()>3){
            //BookmarksStoreDbHelper mDbHelper = new BookmarksStoreDbHelper(getContext());
            // Gets the data repository in write mode
            //SQLiteDatabase db = mDbHelper.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            //ContentValues values = new ContentValues();
            //values.put(BookmarksStore.Profile.COLUMN_NAME_URL_ID, url.getText().toString());
            //values.put(BookmarksStore.Profile.COLUMN_NAME_GET, getPassword.getText().toString());
            //values.put(BookmarksStore.Profile.COLUMN_NAME_ADD, addPassword.getText().toString());
            //values.put(BookmarksStore.Profile.COLUMN_NAME_LIST, listPassword.getText().toString());
            //long newRowId = db.insert(BookmarksStore.Profile.TABLE_NAME, null, values);
            //db.close();
            //mDbHelper.close();
            //if (newRowId>=0) {
            //    dismiss();
            //    parent.reloadProfilesList();
            //    parent.getList();
            //}
        }
        if (profiles.addProfile(url.getText().toString(), getPassword.getText().toString(),
                addPassword.getText().toString(), listPassword.getText().toString())){
            dismiss();
            if (parent != null){
                parent.reloadProfilesList();
                parent.getList();
            }
        }
    }
}
