package com.teamsky.buskhojo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {

    private List<Driver> driversList;

    public DriverAdapter(List<Driver> driversList) {
        this.driversList = driversList;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View driverView = inflater.inflate(R.layout.driver_search_adapter, parent, false); // Replace 'item_driver' with your item layout XML
        return new DriverViewHolder(driverView);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        Driver driver = driversList.get(position);

        // Bind driver data to ViewHolder
        holder.driverBusNameTextView.setText("Bus Name: " + driver.getDriverBusName());
        holder.driverBusNumberTextView.setText("Bus Number: " + driver.getDriverBusNumber());
        holder.driverNameTextView.setText("Driver Name: " + driver.getDriverName());
        holder.driverContactTextView.setText("Contact: " + driver.getDriverContact());
        holder.busSeatsTextView.setText("Seats: " + driver.getBusSeats());
    }

    @Override
    public int getItemCount() {
        return driversList.size();
    }

    public static class DriverViewHolder extends RecyclerView.ViewHolder {
        public TextView driverBusNameTextView;
        public TextView driverBusNumberTextView;
        public TextView driverNameTextView;
        public TextView driverContactTextView;
        public TextView busSeatsTextView;

        public DriverViewHolder(View itemView) {
            super(itemView);

            driverBusNameTextView = itemView.findViewById(R.id.adapter_busName);
            driverBusNumberTextView = itemView.findViewById(R.id.adapter_busNumber);
            driverNameTextView = itemView.findViewById(R.id.adapter_driverName);
            driverContactTextView = itemView.findViewById(R.id.adapter_driverNumber);
            busSeatsTextView = itemView.findViewById(R.id.adapter_seats);
        }
    }
}
