package com.g_ara.garaapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.g_ara.garaapp.helper.APILinks;
import com.g_ara.garaapp.helper.AppController;
import com.g_ara.garaapp.helper.DispatchAdapter;
import com.g_ara.garaapp.helper.DividerItemDecoration;
import com.g_ara.garaapp.model.DispatchResult;
import com.g_ara.garaapp.model.SQLiteHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DispatchActivity extends AppCompatActivity {
    public static final String TAG = DispatchActivity.class.getSimpleName();
    public static final long MAX_WAIT_TIME = 60000;
    public static final long TIME_INTERVAL = 1000;
    public static final long CANCEL_WAIT_TIME = 30000;
    public long beginTime, currentTime;
    private List<DispatchResult> dispatchResults = new ArrayList<>();
    private RecyclerView recyclerView;
    private DispatchAdapter mAdapter;
    private ProgressDialog pDialog;
    HashMap<String, String> member;

    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveres);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new SQLiteHandler(getApplicationContext());

        member = db.getMemberDetails();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new DispatchAdapter(dispatchResults);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final DispatchResult dispatchResult = dispatchResults.get(position);
                Toast.makeText(getApplicationContext(), dispatchResult.getName() + " is selected!", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(DispatchActivity.this);
                builder.setTitle("choose driver");
                builder.setCancelable(true);

                final Location loc1 = new Location("");
                loc1.setLatitude(MainActivity.currentLatitude);
                loc1.setLongitude(MainActivity.currentLongitude);

                Location loc2 = new Location("");
                loc2.setLatitude(Double.parseDouble(dispatchResult.getLatitude()));
                loc2.setLongitude(Double.parseDouble(dispatchResult.getLongitude()));
                float distance = loc1.distanceTo(loc2);
                builder.setMessage(dispatchResult.getName() + "\n" + distance + " Meter away from you\n" + "seats available" + dispatchResult.getAvailableSeats()+"\n phone number:"+dispatchResult.getPhoneNumber());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rideOrder(dispatchResult.getCarid(), MainActivity.currentLatitude, MainActivity.currentLongitude, MainActivity.distLongitude, MainActivity.distLatitude, MainActivity.distLongitude);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        Bundle b = getIntent().getExtras();

        prepareDriverData(b.get("drivers"));
    }

    private void rideOrder(final String carid, final double currentLatitude, final double currentLongitud, final double distLongitude, final double distLatitude, double distLongitude1) {
        pDialog.setMessage("Ordering your ride..");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APILinks.RIDE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "dispatch Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    boolean noError = jObj.length() == 0 ? false : true;

                    // Check for error node in json
                    if (noError) {
                        JSONObject jsonObject = jObj.getJSONObject(0);
                        driverResponse(jsonObject.get("id"));

                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        String errorMsg = jsonObject.getString("error").toString();
                        Toast.makeText(getApplicationContext(),
                                "error" + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Ordering Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "error: ", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("carid", carid);
                params.put("sourcelatitude", String.valueOf(currentLatitude));
                params.put("sourcelongitude", String.valueOf(currentLongitud));
                params.put("destinationlatitude", String.valueOf(distLatitude));
                params.put("destinationlongitude", String.valueOf(distLongitude));
//                params.put("dist", "1000");
                params.put("memberid", String.valueOf(distLongitude));

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, "ordering");
    }

    private void driverResponse(final Object id) {
        final boolean[] makeReq = {true};
        AlertDialog.Builder builder = new AlertDialog.Builder(DispatchActivity.this);
        builder.setTitle("waiting driver response");
        builder.setMessage("this dialog will remain for 30 seconds you can cancel any time");

        builder.setCancelable(false);
        builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                pDialog.setMessage("cancelling ..");
                showDialog();
                StringRequest strReq = new StringRequest(Request.Method.DELETE,
                        APILinks.RIDE + "/" + id.toString(), new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "cancelling Response: " + response.toString());
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "ride canceled", Toast.LENGTH_LONG);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "waitingDriver Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "error: ", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                });
                AppController.getInstance().addToRequestQueue(strReq, "cancelling");
                makeReq[0] = false;
            }
        });

        final AlertDialog dialog = builder.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (makeReq[0]) {
                    beginTime = System.currentTimeMillis();
                    dialog.cancel();
                    busyWaitDriverResponse(id);
                }
            }
        }, MAX_WAIT_TIME);

    }

    private void busyWaitDriverResponse(final Object id) {
        pDialog.setMessage("waiting driver response... max 1 minute");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                APILinks.RIDE + "/" + id.toString(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "waitingDriver Response: " + response.toString());

                try {
                    JSONArray jObj = new JSONArray(response);
                    boolean noError = jObj.length() == 0 ? false : true;


                    if (noError) {
                        JSONObject jsonObject = jObj.getJSONObject(0);

                        switch (jsonObject.get("accepted").toString()) {
                            case "null":
                                Toast.makeText(getApplicationContext(), "no response from driver", Toast.LENGTH_LONG).show();
                                if (System.currentTimeMillis() - beginTime <= MAX_WAIT_TIME && System.currentTimeMillis() - currentTime >= TIME_INTERVAL) {
                                    currentTime = System.currentTimeMillis();
//                                    busyWaitDriverResponse(id);
                                } else {
                                    hideDialog();
                                }
                                break;
                            case "0":
                                Toast.makeText(getApplicationContext(), "driver rejected", Toast.LENGTH_LONG).show();
                                hideDialog();

                                break;

                            case "1":
                                Toast.makeText(getApplicationContext(), "driver accepted", Toast.LENGTH_LONG).show();
                                hideDialog();

                                AlertDialog.Builder builder = new AlertDialog.Builder(DispatchActivity.this);
                                builder.setTitle("driver accepted");
                                builder.setCancelable(false);
                                builder.setPositiveButton("go to check in screen", null);
                                builder.show();

                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (System.currentTimeMillis() - beginTime <= MAX_WAIT_TIME && System.currentTimeMillis() - currentTime >= TIME_INTERVAL) {
                    currentTime = System.currentTimeMillis();
//                    busyWaitDriverResponse(id);
                } else {
                    hideDialog();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "waitingDriver Error: " + error.getMessage());
                if (System.currentTimeMillis() - beginTime <= MAX_WAIT_TIME && System.currentTimeMillis() - currentTime >= TIME_INTERVAL) {
                    currentTime = System.currentTimeMillis();
//                    busyWaitDriverResponse(id);
                } else {
                    hideDialog();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, "waitingDriver");
    }

    private void prepareDriverData(Object drivers) {
        try {
            JSONArray json = new JSONArray((String) drivers);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                if (!jsonObject.has("carid"))
                    jsonObject.put("carid", 1);
                DispatchResult result = new DispatchResult(
                        jsonObject.getString("ID"),
                        jsonObject.getString("name"),
                        jsonObject.getString("username"),
                        jsonObject.getString("pic"),
                        jsonObject.getString("longitude"),
                        jsonObject.getString("latitude"),
                        jsonObject.getString("DistLatitude"),
                        jsonObject.getString("DistLongitude"),
                        jsonObject.getString("carModelID"),
                        jsonObject.getString("availableSeats"),
                        jsonObject.getString("frontPic"), jsonObject.getString("carid"),jsonObject.getString("phoneNumber")
                );
                dispatchResults.add(result);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        mAdapter.notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private DispatchActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final DispatchActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

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
