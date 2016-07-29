package com.transility.tim.android.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transility.tim.android.R;
import com.transility.tim.android.bean.DeviceReport;

import java.util.ArrayList;

/**
 * Adapter class to attach data into inflated views.
 * Created by ambesh.kukreja on 7/27/2016.
 */
public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsRecyclerViewHolder>{


    private ArrayList<DeviceReport> deviceReports;
    public ReportsAdapter(ArrayList<DeviceReport> deviceReports){

        this.deviceReports=deviceReports;

    }

    @Override
    public ReportsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reports_item_screen,null);

        return new ReportsRecyclerViewHolder(layout_view);
    }

    @Override
    public void onBindViewHolder(ReportsRecyclerViewHolder holder, int position) {


        DeviceReport deviceReport=deviceReports.get(position);
        holder.userNameTv.setText(deviceReport.getUserName());
        holder.intTimeTv.setText(deviceReport.getInTime());
        holder.outTimeTv.setText(deviceReport.getOutTime());

    }

    @Override
    public int getItemCount() {
        return deviceReports.size();
    }


    protected class ReportsRecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView userNameTv,intTimeTv,outTimeTv;
     public ReportsRecyclerViewHolder(View itemView) {
         super(itemView);
         userNameTv= (TextView) itemView.findViewById(R.id.userNameTv);
         intTimeTv= (TextView) itemView.findViewById(R.id.intTimeTv);
         outTimeTv= (TextView) itemView.findViewById(R.id.outTimeTv);
     }

 }
}
