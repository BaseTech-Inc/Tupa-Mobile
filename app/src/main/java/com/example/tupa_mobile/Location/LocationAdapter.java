package com.example.tupa_mobile.Location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tupa_mobile.R;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationHolder>{

    private Context context;
    private ArrayList<Location> locations;

    public LocationAdapter(Context context, ArrayList<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_location_item, parent, false);
        return new LocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        Location location = locations.get(position);
        holder.setDetails(location);
    }

    @Override
    public int getItemCount() {
        return this.locations.size();
    }


    public class LocationHolder extends RecyclerView.ViewHolder {

        TextView txtLocation, txtCurrentDesc, txtCurrentTemp;
        private ImageView imgRideIcon;
        public LocationHolder(View view) {
            super(view);

            txtLocation = view.findViewById(R.id.txtLocation);
            txtCurrentDesc = view.findViewById(R.id.txtDesc);
            txtCurrentTemp = view.findViewById(R.id.txtCurrentTemp);
            imgRideIcon = itemView.findViewById(R.id.imgRideIcon);
        }

        public void setDetails(Location location) {

            txtLocation.setText(location.getName().toString());
            txtCurrentDesc.setText(location.getCurrentDesc());
            txtCurrentTemp.setText(location.getTemp() + "°");
            if (location.getCurrentDesc() == "Ensolarado") {
                imgRideIcon.setImageResource(R.drawable.day_sunny);
            }
            if (location.getCurrentDesc() == "Nublado") {
                imgRideIcon.setImageResource(R.drawable.day_cloudy);
            }
            if (location.getCurrentDesc() == "Ventando") {
                imgRideIcon.setImageResource(R.drawable.day_windy);
            }
        }
        private String truncate(String str, int n){
            return (str.length() > n) ? str.substring(0, n-1) + "..." : str;
        };
    }
}
