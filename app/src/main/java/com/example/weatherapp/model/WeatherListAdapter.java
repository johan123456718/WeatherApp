package com.example.weatherapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;

import java.util.List;

/**
 * An Adapter for placing the data to a Recycler View
 * Made by Johan Challita, Tidaa
 */

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<WeatherVolley> weatherList;

    public WeatherListAdapter(Context context,
                              List<WeatherVolley> weatherList) {
        this.inflater = LayoutInflater.from(context);
        this.weatherList = weatherList;
    }

    /**
     * Initalized the viewholder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.weather_list,
                parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Binds the viewholder to the adapter
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position < weatherList.size()) {
            WeatherVolley weatherVolley = weatherList.get(position);
            holder.validTime.setText(weatherVolley.getValidTime());
            holder.cloud_coverage.setText(weatherVolley.getCloudCoverage());
            holder.temperature.setText(weatherVolley.getDegrees());
        }
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    /**
     * A ViewHolder class that holds the data from the item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView validTime;
        private TextView cloud_coverage;
        private TextView temperature;

        public ViewHolder(View itemView) {
            super(itemView);
            validTime = itemView.findViewById(R.id.validTime);
            cloud_coverage = itemView.findViewById(R.id.cloud_coverage);
            temperature = itemView.findViewById(R.id.temperature);
        }
    }
}
