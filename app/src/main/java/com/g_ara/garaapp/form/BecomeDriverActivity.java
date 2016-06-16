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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class BecomeDriverActivity extends Activity {

    private static final String TAG = BecomeDriverActivity.class.getSimpleName();

    // UI references.
    private EditText licenseNumber;
    private View mBecomeDriverFormView;
    private ProgressDialog pDialog;
    private SessionManager session;

    private SQLiteHandler db;
    HashMap<String, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_driver);
        // Set up the login form.
        mBecomeDriverFormView = findViewById(R.id.become_driver_form);

        licenseNumber = (EditText) mBecomeDriverFormView.findViewById(R.id.license_number);


        Button mBecomeDriverButton = (Button) mBecomeDriverFormView.findViewById(R.id.become_driver_button);
        mBecomeDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String licenseNumber = BecomeDriverActivity.this.licenseNumber.getText().toString().trim();
                if (!licenseNumber.isEmpty()) {
                    becomingDriver(licenseNumber);
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the input", Toast.LENGTH_LONG).show();
                }
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        data = db.getMemberDetails();
        session = new SessionManager(getApplicationContext());

    }

    private void becomingDriver(final String licenseNumber) {
        // Tag used to cancel the request
        String tag_string_req = "req_becomeDriver";

        pDialog.setMessage("becomeDriver ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APILinks.DRIVER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "becomeDriver Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    boolean noError = jObj.length() == 0 ? false : true;

                    // Check for error node in json
                    if (noError) {
                        // Launch main activity
                        session.setLogin(false);

                        db.deleteAll();

                        // Launching the login activity
                        Intent intent = new Intent(BecomeDriverActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        String errorMsg = jsonObject.getString("error").toString();
                        Toast.makeText(getApplicationContext(),
                                "error" + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "becomeDriver Error: " + error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("licenseNumber", licenseNumber);
                params.put("memberID", data.get("memberID"));
                params.put("licensePic", "");
                params.put("identyCardPic", "");

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

