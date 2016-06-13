package com.g_ara.garaapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private ProgressDialog pDialog;

    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String username = mUsernameView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();

                if(!username.isEmpty()&&!password.isEmpty()){
                    checkLogin(username, password);
                }else {
                    Toast.makeText(getApplicationContext(),"Please enter username/password",Toast.LENGTH_LONG).show();
                }
            }
        });
        Button mCreateAccountButton = (Button) findViewById(R.id.createAccountButton);
        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()&&db.getMemberDetails().size()>0) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkLogin(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APILinks.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    boolean error = jObj.length()==0?false:true;

                    // Check for error node in json
                    if (error) {
                        // member successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the member in SQLite
                        String accesstoken = jObj.getJSONObject(0).getString("value");

                        JSONObject member = jObj.getJSONObject(1);
                        HashMap<String,String> map = new Gson().fromJson(member.toString(), new TypeToken<HashMap<String, String>>(){}.getType());

                        map.put("accesstoken",accesstoken);


//                        String id = member.getString("id");
//                        String name = member.getString("name");
//                        String studentemail = member.getString("studentemail");
//                        String username = member.getString("username");
//                        String password = member.getString("password");
//                        String phoneNumber = member.getString("phonenumber");
//                        String pic = member.getString("pic");
//
//                        // Inserting row in users table
//                        db.addMember(id,name,username,studentemail,password,phoneNumber,pic,accesstoken);
                        db.addMember(map);
//                         Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        JSONObject jsonObject = new JSONObject(response);
                        String errorMsg = jsonObject.getString("error").toString();
                        Toast.makeText(getApplicationContext(),
                                "error"+errorMsg, Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Login Error: " + error.getMessage());
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    Toast.makeText(getApplicationContext(),
                            "error: "+jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

