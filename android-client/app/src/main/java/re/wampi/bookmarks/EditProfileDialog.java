package re.wampi.bookmarks;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EditProfileDialog extends DialogFragment implements View.OnClickListener {
    EditText url, getPassword, addPassword, listPassword;
    Button updateButton, removeButton;
    Profiles profiles;
    Profile profile;
    MainActivity parent; // maybe very bad way

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_dialog, null);
        url = (EditText) v.findViewById(R.id.url);
        getPassword = (EditText) v.findViewById(R.id.getPassword);
        addPassword = (EditText) v.findViewById(R.id.addPassword);
        listPassword = (EditText) v.findViewById(R.id.listPassword);
        updateButton = (Button) v.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);
        removeButton = (Button) v.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(this);
        if (profile != null){
            url.setText(profile.url);
            getPassword.setText(profile.getPassword);
            addPassword.setText(profile.addPassword);
            listPassword.setText(profile.listPassword);
        }
        return v;
    }

    public void setProfiles(Profiles ps){
        profiles = ps;
        profile = ps.getSelectedProfile();
    }

    @Override
    public void onClick(View v){
        if (v == updateButton){
            if (url.getText().toString().length()>3){
                String id = profile.url;
                profile.url = url.getText().toString();
                profile.getPassword = getPassword.getText().toString();
                profile.addPassword = addPassword.getText().toString();
                profile.listPassword = listPassword.getText().toString();
                if (profile.save()){
                    dismiss();
                    parent.reloadProfilesList();
                    parent.getList();
                }
            }
        }
        if (v == removeButton){
            if (profiles.removeProfile(profile)){
                dismiss();
                parent.reloadProfilesList();
                parent.getList();
            }
        }
    }
}
