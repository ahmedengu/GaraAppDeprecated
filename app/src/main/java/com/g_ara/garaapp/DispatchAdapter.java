package com.g_ara.garaapp;

/**
 * Created by ahmedengu on 6/13/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DispatchAdapter extends RecyclerView.Adapter<DispatchAdapter.MyViewHolder> {

    private List<DispatchResult> dispatchResults;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView driverName;
        public ImageView driverPIC;

        public MyViewHolder(View view) {
            super(view);
            driverName = (TextView) view.findViewById(R.id.driverName);
            driverPIC = (ImageView) view.findViewById(R.id.driverPIC);
        }
    }


    public DispatchAdapter(List<DispatchResult> dispatchResults) {
        this.dispatchResults = dispatchResults;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.driver_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DispatchResult driver = dispatchResults.get(position);
        holder.driverName.setText(driver.getName());
        new ImageLoadTask(driver.getPic(), holder.driverPIC).execute();

    }

    @Override
    public int getItemCount() {
        return dispatchResults.size();
    }
}