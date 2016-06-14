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
public class AddCarActivity extends Activity {

    private static final String TAG = AddCarActivity.class.getSimpleName();

    // UI references.
    private EditText licenseNumber;
    private EditText PlateNumber;

    private View mAddCarFormView;
    private ProgressDialog pDialog;
    private SessionManager session;

    private SQLiteHandler db;
    HashMap<String, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        // Set up the login form.
        mAddCarFormView = findViewById(R.id.add_car_form);

        licenseNumber = (EditText) mAddCarFormView.findViewById(R.id.license_number);
        PlateNumber = (EditText) mAddCarFormView.findViewById(R.id.plate_number);


        Button mAddCarButton = (Button) mAddCarFormView.findViewById(R.id.add_car_button);
        mAddCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String licenseNumber = AddCarActivity.this.licenseNumber.getText().toString().trim();
                String PlateNumber = AddCarActivity.this.PlateNumber.getText().toString().trim();

                if (!licenseNumber.isEmpty()&&!PlateNumber.isEmpty()) {
                    addingCar(licenseNumber,PlateNumber);
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the input", Toast.LENGTH_LONG).show();
                }
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        data = db.selectOne("Driver");
        session = new SessionManager(getApplicationContext());

    }

    private void addingCar(final String licenseNumber, final String PlateNumber) {
        // Tag used to cancel the request
        String tag_string_req = "req_AddCar";

        pDialog.setMessage("AddCar ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APILinks.CAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "AddCar Response: " + response.toString());
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

                        Intent intent = new Intent(AddCarActivity.this, LoginActivity.class);
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
                Log.e(TAG, "AddCar Error: " + error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("licenseNumber", licenseNumber);
                params.put("plateNumber", PlateNumber);
                params.put("carModelID", "1");
                params.put("driverID", data.get("driverID"));

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

