package com.g_ara.garaapp.form;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.g_ara.garaapp.helper.APILinks;
import com.g_ara.garaapp.helper.AppController;
import com.g_ara.garaapp.R;
import com.g_ara.garaapp.helper.SessionManager;
import com.g_ara.garaapp.model.SQLiteHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    // UI references.
    private EditText name;
    private EditText password;

    private View mSettingsFormView;
    private ProgressDialog pDialog;
    private SessionManager session;

    private SQLiteHandler db;
    HashMap<String, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Set up the login form.
        mSettingsFormView = findViewById(R.id.settings_form);
        db = new SQLiteHandler(getApplicationContext());
        data = db.selectOne("Member");
        session = new SessionManager(getApplicationContext());

        name = (EditText) mSettingsFormView.findViewById(R.id.name);
        password = (EditText) mSettingsFormView.findViewById(R.id.password);
        name.setText(data.get("name"));

        Button mSettingsSaveButton = (Button) mSettingsFormView.findViewById(R.id.settings_save_button);
        mSettingsSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = SettingsActivity.this.name.getText().toString().trim();
                String password = SettingsActivity.this.password.getText().toString().trim();

                if (!name.isEmpty()&&!password.isEmpty()) {
                    addingCar(name,password);
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the input", Toast.LENGTH_LONG).show();
                }
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SQLite database handler

    }

    private void addingCar(final String name, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_Settings";

        pDialog.setMessage("Settings ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                APILinks.MEMBER+"/"+data.get("ID").toString(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Settings Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    boolean noError = jObj.length() == 0 ? false : true;

                    // Check for error node in json
                    if (noError) {
                        // Launch main activity
                        session.setLogin(false);

                        db.deleteAll();
                        Toast.makeText(getApplicationContext(), "logging out for data update", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        String errorMsg = jsonObject.getString("error").toString();
                        Toast.makeText(getApplicationContext(),
                                "error" + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Settings Error: " + error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("password", password);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

