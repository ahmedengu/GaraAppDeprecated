package com.g_ara.garaapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mNameView;
    private EditText mStudentEmailView;
    private EditText mPhoneNumberView;
    private View mRegisterFormView;
    private ProgressDialog pDialog;

    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mRegisterFormView = findViewById(R.id.Register_form);

        mUsernameView = (EditText) mRegisterFormView.findViewById(R.id.username);

        mPasswordView = (EditText) mRegisterFormView.findViewById(R.id.password);
        mNameView = (EditText) mRegisterFormView.findViewById(R.id.name);

        mStudentEmailView = (EditText) mRegisterFormView.findViewById(R.id.studentEmail);
        mPhoneNumberView = (EditText) mRegisterFormView.findViewById(R.id.phoneNumber);
        Button mRegisterButton = (Button) mRegisterFormView.findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();
                String name = mNameView.getText().toString().trim();
                String phoneNumber = mPhoneNumberView.getText().toString().trim();
                String studentEmail = mStudentEmailView.getText().toString().trim();
                if (!username.isEmpty() && !password.isEmpty() && !name.isEmpty() && !phoneNumber.isEmpty() && !studentEmail.isEmpty()) {
                    registerMember(username, password, studentEmail, phoneNumber, name);
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the input", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button mAlreadyMemberButton = (Button) findViewById(R.id.already_Member_button);
        mAlreadyMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());


    }

    private void registerMember(final String username, final String password, final String studentEmail, final String phoneNumber, final String name) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APILinks.REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "register Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    boolean noError = jObj.length() == 0 ? false : true;

                    // Check for error node in json
                    if (noError) {
                        JSONObject member = jObj.getJSONObject(0);
                        String id = member.getString("id");
                        String name = member.getString("name");
                        String studentemail = member.getString("studentemail");
                        String username = member.getString("username");
                        String password = member.getString("password");
                        String phoneNumber = member.getString("phonenumber");

                        // Inserting row in users table
                        db.addMember(id,name, username, studentemail, password, phoneNumber,"");

                        // Launch main activity
                        Intent intent = new Intent(RegisterActivity.this,
                                LoginActivity.class);
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
                Log.e(TAG, "register Error: " + error.getMessage());
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    Toast.makeText(getApplicationContext(),
                            "error: " + jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "error: ", Toast.LENGTH_LONG).show();
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
                params.put("studentemail", studentEmail);
                params.put("name", name);
                params.put("phonenumber", phoneNumber);


                return params;
            }

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

