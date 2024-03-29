package com.g_ara.garaapp.helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.g_ara.garaapp.R;
import com.g_ara.garaapp.model.Car;

import java.util.List;

/**
 * Created by ahmedengu on 6/13/2016.
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder> {

    private List<Car> dispatchResults;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView plateNumber;
        public ImageView frontPIC;

        public MyViewHolder(View view) {
            super(view);
            plateNumber = (TextView) view.findViewById(R.id.plate_number);
            frontPIC = (ImageView) view.findViewById(R.id.front_pic);
        }
    }


    public CarAdapter(List<Car> dispatchResults) {
        this.dispatchResults = dispatchResults;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Car car = dispatchResults.get(position);
        holder.plateNumber.setText(car.getPlateNumber());
        if(car.getFrontPic()==null)
            new ImageLoadTask("https://cdn3.iconfinder.com/data/icons/car-icons-front-views/480/Sports_Car_Front_View-512.png", holder.frontPIC).execute();
        else
        new ImageLoadTask(car.getFrontPic(), holder.frontPIC).execute();

    }

    @Override
    public int getItemCount() {
        return dispatchResults.size();
    }
}