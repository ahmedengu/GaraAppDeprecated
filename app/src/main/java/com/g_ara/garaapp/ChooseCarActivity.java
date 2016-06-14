package com.g_ara.garaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ahmedengu on 6/13/2016.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChooseCarActivity extends AppCompatActivity {
    public static final String TAG = ChooseCarActivity.class.getSimpleName();
    private List<Car> cars = new ArrayList<>();
    private RecyclerView recyclerView;
    private CarAdapter mAdapter;
    private ProgressDialog pDialog;
    List<HashMap<String, String>> carList;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_area);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new CarAdapter(cars);

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
                String tag_string_req = "req_chooseCar";

                final Car car = cars.get(position);
                Toast.makeText(getApplicationContext(), car.getID() + " is selected!", Toast.LENGTH_SHORT).show();

                pDialog.setMessage("Choosing Car ...");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.PUT,
                        APILinks.CAR+"/"+car.getID(), new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "chooseCar Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONArray jObj = new JSONArray(response);
                            boolean noError = jObj.length() == 0 ? false : true;

                            // Check for error node in json
                            if (noError) {
                                // Launch main activity

                                Toast.makeText(getApplicationContext(), "car choosen", Toast.LENGTH_SHORT).show();


                                Intent intent = new Intent(ChooseCarActivity.this,
                                        DriverAreaWaitActivity.class);
                                intent.putExtra("carid", car.getID());
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
                        Log.e(TAG, "chooseCar Error: " + error.getMessage());
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
                        params.put("state", "1");




                        return params;
                    }

                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        db = new SQLiteHandler(getApplicationContext());
        carList = db.selectAll("car");
        prepareCarData(carList);
    }


    private void prepareCarData(List<HashMap<String, String>> carList) {

        for (int i = 0; i < carList.size(); i++) {
            HashMap<String, String> mCar = carList.get(i);
            Car car = new Car(mCar.get("ID"), mCar.get("driverID"), mCar.get("plateNumber"), mCar.get("platePic"), mCar.get("carModelID"), mCar.get("frontPic"), mCar.get("backPic"), mCar.get("sidePic"), mCar.get("insidePic"), mCar.get("licenseNumber"), mCar.get("licensePic"), mCar.get("licenseExpireDate"), mCar.get("DistLongitude"), mCar.get("DistLatitude"), mCar.get("availableSeats"), mCar.get("state"));

            cars.add(car);
        }


        mAdapter.notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ChooseCarActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ChooseCarActivity.ClickListener clickListener) {
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