package re.wampi.bookmarks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;
import android.content.Context;

import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addButtonOnClick(View v) {
        EditText AddEditText = (EditText) findViewById(R.id.AddEditText);
        String url = "http://bookmarks.wampi.re/add/?url="+AddEditText.getText().toString();
        System.out.println(url);

        RequestQueue queue = Volley.newRequestQueue(this);

        //JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, response.toString(), duration);
                toast.show();
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

