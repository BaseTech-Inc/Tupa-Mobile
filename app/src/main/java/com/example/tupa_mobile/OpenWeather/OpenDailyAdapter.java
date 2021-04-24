package com.example.tupa_mobile.OpenWeather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tupa_mobile.R;

import java.util.ArrayList;

public class OpenDailyAdapter extends RecyclerView.Adapter<OpenDailyAdapter.OpenDailyHolder> {

    private Context context;
    private ArrayList<OpenDaily> dailies;

    public OpenDailyAdapter(Context context, ArrayList<OpenDaily> dailies) {
        this.context = context;
        this.dailies = dailies;
    }

    @NonNull
    @Override
    public OpenDailyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.open_forecast_item,parent, false);
        return new OpenDailyAdapter.OpenDailyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenDailyHolder holder, int position) {
        OpenDaily openDaily = dailies.get(position);
        holder.setDetails(openDaily);

        boolean isExpandable = dailies.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return this.dailies.size();
    }

    public class OpenDailyHolder extends RecyclerView.ViewHolder {

        TextView txtUpdateTime, txtTempDay, txtTempNight, txtFeelsLikeDay, txtFeelsLikeNight, txtHumidity, txtRain, txtPop, txtCondition, txtDescCondition;
        LinearLayout clickableLayout;
        RelativeLayout expandableLayout;

        public OpenDailyHolder(@NonNull View itemView) {
            super(itemView);

            //initialize the textViews
            txtUpdateTime = itemView.findViewById(R.id.txtUpdateTime);
            txtTempDay = itemView.findViewById(R.id.txtTempDay);
            txtRain = itemView.findViewById(R.id.txtRain);
            txtPop = itemView.findViewById(R.id.txtPop);

            clickableLayout = itemView.findViewById(R.id.clickableLayout);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            clickableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OpenDaily daily =  dailies.get(getAdapterPosition());
                    daily.setExpandable(!daily.isExpandable());
                    notifyItemChanged(getAdapterPosition());

                }
            });

        }

        void setDetails(OpenDaily openDaily){

            //assign textViews' values

            txtUpdateTime.setText("Update: " + openDaily.getDt());
            txtTempDay.setText("Day UVI: " + openDaily.getUvi());
            txtRain.setText("Rain (mm): " + openDaily.getRain());
            txtPop.setText("Rain Probability:" + openDaily.getPop());

        }
    }
}
