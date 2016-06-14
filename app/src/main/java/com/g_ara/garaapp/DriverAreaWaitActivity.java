package com.g_ara.garaapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverAreaWaitActivity extends AppCompatActivity {
    public static final String TAG = DriverAreaWaitActivity.class.getSimpleName();
    public static final long MAX_WAIT_TIME = 100000;
    public static final long TIME_INTERVAL = 1000;
    public static final long CANCEL_WAIT_TIME = 30000;
    public long beginTime, currentTime;
    private ProgressDialog pDialog;
    final boolean[] makeReq = {true};
    final AlertDialog[] dialogArr = new AlertDialog[1];
    List<JSONObject> ride;
    JSONObject theRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_area_wait);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(DriverAreaWaitActivity.this);
        builder.setTitle("waiting passengers");
        builder.setMessage("you may cancel any time");
        ride = new ArrayList<>();

        String carID = getIntent().getExtras().getString("carid");
        waitForPassenger(carID);
    }


    private void waitForPassenger(final Object id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverAreaWaitActivity.this);
        builder.setTitle("waiting passangers");
        builder.setMessage("you may cancel any time if you want to stop carpooling");

        builder.setCancelable(false);
        builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                pDialog.setMessage("cancelling ..");
                showDialog();
                StringRequest strReq = new StringRequest(Request.Method.PUT,
                        APILinks.CAR + "/" + id.toString(), new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "cancelling Response: " + response.toString());
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "canceled", Toast.LENGTH_LONG);
                        Intent intent = new Intent(DriverAreaWaitActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "cancelling Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "error: ", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("state", "0");
                        return params;
                    }

                };
                AppController.getInstance().addToRequestQueue(strReq, "cancelling");
                makeReq[0] = false;
            }
        });

        final AlertDialog dialog = builder.show();
        dialogArr[0] = dialog;
        busyWaitPassanger(id);
    }


    private void busyWaitPassanger(final Object id) {
        if (makeReq[0]) {
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    APILinks.RIDE + "/where/and", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "waitingPassanger Response: " + response.toString());
                    ride.clear();
                    try {
                        JSONArray jObj = new JSONArray(response);
                        boolean noError = jObj.length() == 0 ? false : true;
                        if (noError) {

                            for (int i = 0; i < jObj.length(); i++) {
                                JSONObject jsonObject = jObj.getJSONObject(i);
                                if (jsonObject.get("endtime").toString().equals("null")) {
                                    Toast.makeText(getApplicationContext(), "passange found ", Toast.LENGTH_LONG).show();
                                    ride.add(jsonObject);
                                }
                            }

                            while (ride.size() > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DriverAreaWaitActivity.this);
                                builder.setTitle("choose passanger");
                                builder.setCancelable(true);

                                builder.setMessage("ride :" + ride.get(0).get("id"));

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        StringRequest strReq = null;
                                        try {
                                            strReq = new StringRequest(Request.Method.PUT,
                                                    APILinks.RIDE + "/" + ride.get(0).get("id"), new Response.Listener<String>() {

                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d(TAG, "chooseCar Response: " + response.toString());
                                                    theRide = ride.get(0);
                                                    ride.clear();
                                                    dialogArr[0].cancel();
                                                    Toast.makeText(getApplicationContext(),
                                                            "Passanger choosen", Toast.LENGTH_LONG).show();
                                                }
                                            }, new Response.ErrorListener() {

                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                }
                                            }) {

                                                @Override
                                                protected Map<String, String> getParams() {
                                                    // Posting parameters to login url
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("accepted", "1");
                                                    return params;
                                                }

                                            };
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // Adding request to request queue
                                        AppController.getInstance().addToRequestQueue(strReq, "ride accepting");

                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StringRequest strReq = null;
                                        try {
                                            strReq = new StringRequest(Request.Method.PUT,
                                                    APILinks.RIDE + "/" + ride.get(0).get("id"), new Response.Listener<String>() {

                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d(TAG, "ride canceled Response: " + response.toString());

                                                    Toast.makeText(getApplicationContext(),
                                                            "ride canceled", Toast.LENGTH_LONG).show();
                                                    ride.remove(0);
                                                }
                                            }, new Response.ErrorListener() {

                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    ride.remove(0);
                                                }
                                            }) {

                                                @Override
                                                protected Map<String, String> getParams() {
                                                    // Posting parameters to login url
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("accepted", "0");
                                                    return params;
                                                }

                                            };
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // Adding request to request queue
                                        AppController.getInstance().addToRequestQueue(strReq, "ride cancel");
                                    }
                                });
                                builder.show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    if (System.currentTimeMillis() - currentTime >= TIME_INTERVAL && makeReq[0]) {
                        currentTime = System.currentTimeMillis();
//                        busyWaitPassanger(id);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "waitingDriver Error: " + error.getMessage());
                    if (System.currentTimeMillis() - currentTime >= TIME_INTERVAL && makeReq[0]) {
                        currentTime = System.currentTimeMillis();
//                        busyWaitPassanger(id);
                    }
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("carID", String.valueOf(id));
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(strReq, "waitingPassanger");
        }
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
