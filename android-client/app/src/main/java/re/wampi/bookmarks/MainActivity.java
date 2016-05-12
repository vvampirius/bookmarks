package re.wampi.bookmarks;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Profiles profiles;

    ArrayAdapter<String> adapter;
    EditText editText;
    ArrayList<String> itemList;

    Spinner profilesList;
    SharedPreferences settings;
    SharedPreferences.Editor settingsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profilesList = (Spinner) findViewById(R.id.spinner);
        settings = getBaseContext().getSharedPreferences("Bookmarks", Context.MODE_PRIVATE);
        settingsEditor = settings.edit();

        profiles = new Profiles(getApplicationContext(), settingsEditor);
        System.out.println("selectedProfile: "+settings.getString("selectedProfile", ""));
        if (!profiles.setSelectedProfileByURL(settings.getString("selectedProfile", ""))){
            System.out.println("REMOVING selectedProfile: "+settings.getString("selectedProfile", ""));
            settingsEditor.remove("selectedProfile");
            settingsEditor.apply();
        }

        reloadProfilesList();

        profilesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            protected Adapter initializedAdapter=null;
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (initializedAdapter != parent.getAdapter()) {
                    initializedAdapter = parent.getAdapter();
                    System.out.println("startup stub!");
                    return;
                }
                String selectedItem = parent.getItemAtPosition(pos).toString();
                Profile selectedProfile = profiles.getSelectedProfile();
                System.out.println("OnItemSelected: "+selectedItem);
                if (selectedProfile==null || selectedItem!=selectedProfile.url){
                    if (profiles.setSelectedProfileByURL(selectedItem)){
                        settingsEditor.putString("selectedProfile", selectedItem);
                        settingsEditor.apply();
                        System.out.println("added to settings");
                        getList();
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        });

        getList();
    }

    public void addButtonOnClick(View v) {
        Profile selectedProfile = profiles.getSelectedProfile();
        if (selectedProfile != null) {
            EditText AddEditText = (EditText) findViewById(R.id.AddEditText);
            String getRequest = selectedProfile.url+"/add/?url="+AddEditText.getText().toString();
            if (selectedProfile.addPassword!=""){
                getRequest = getRequest+"&password="+selectedProfile.addPassword;
            }
            System.out.println(getRequest);

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, getRequest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, response.toString(), duration);
                    toast.show();
                    getList();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, error.networkResponse.statusCode+" "+new String(error.networkResponse.data, StandardCharsets.UTF_8), duration);
                    toast.show();
                }
            });

            queue.add(stringRequest);

            AddEditText.setText("");
        }
    }

    public void getList(){
        Profile selectedProfile = profiles.getSelectedProfile();
        if (selectedProfile != null){
            itemList=new ArrayList<String>();
            adapter=new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtview,itemList);
            ListView listV=(ListView)findViewById(R.id.listView);
            listV.setAdapter(adapter);
            adapter.clear();

            String getRequest = selectedProfile.url+"/list/";
            if (selectedProfile.listPassword!=""){
                getRequest = getRequest+"?password="+selectedProfile.listPassword;
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    getRequest, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject urlJSONObject = response.getJSONObject(i);
                            adapter.add(urlJSONObject.getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.toString());
                    //System.out.println(error.networkResponse.statusCode);
                }
            });
            queue.add(jsonArrayRequest);
        }
    }

    public void showAddProfileDialog(View v){
        FragmentManager manager=getFragmentManager();
        AddProfileDialog addProfileDialog=new AddProfileDialog();
        addProfileDialog.profiles = profiles;
        addProfileDialog.parent = this;
        addProfileDialog.show(manager, "AddProfileDialog");
    }

    public void showEditProfileDialog(View v){
        FragmentManager manager=getFragmentManager();
        EditProfileDialog editProfileDialog=new EditProfileDialog();
        editProfileDialog.setProfiles(profiles);
        editProfileDialog.parent = this;
        editProfileDialog.show(manager, "EditProfileDialog");
    }

    public void reloadProfilesList(){
        String[] IDs = profiles.getIDs();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, IDs);
        profilesList.setAdapter(adapter);
        if (IDs.length>0){
            if (profiles.selected==-1) {
                profiles.setSelectedProfile(profiles.profiles.get(0));
            }
            profilesList.setSelection(profiles.selected);
        }
    }

}




