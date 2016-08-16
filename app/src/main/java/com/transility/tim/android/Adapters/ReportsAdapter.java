package com.transility.tim.android.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transility.tim.android.R;
import com.transility.tim.android.bean.DeviceReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter class to attach data into inflated views.
 * Created by ambesh.kukreja on 7/27/2016.
 */
public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsRecyclerViewHolder>{


    private final ArrayList<DeviceReport> deviceReports;

    private final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a", Locale.US);
    public ReportsAdapter(ArrayList<DeviceReport> deviceReports){


        this.deviceReports=deviceReports;



    }

    @Override
    public ReportsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout_view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reports_item_screen,parent,false);

        return new ReportsRecyclerViewHolder(layout_view);
    }

    @Override
    public void onBindViewHolder(ReportsRecyclerViewHolder holder, int position) {


        DeviceReport deviceReport=deviceReports.get(position);
        holder.userNameTv.setText(deviceReport.getUserName());

        if (!TextUtils.isEmpty(deviceReport.getInTime())){
            holder.intTimeTv.setText(simpleDateFormat.format(new Date(Long.parseLong(deviceReport.getInTime()))));
        }
        else {
            holder.intTimeTv.setText("");

        }

        if (!TextUtils.isEmpty(deviceReport.getOutTime())){
            holder.outTimeTv.setText(simpleDateFormat.format(new Date(Long.parseLong(deviceReport.getOutTime()))));
        }
        else{
            holder.outTimeTv.setText("");
        }
        }

    @Override
    public int getItemCount() {
        return deviceReports.size();
    }


    protected class ReportsRecyclerViewHolder extends RecyclerView.ViewHolder{
    public final TextView userNameTv,intTimeTv,outTimeTv;

     public ReportsRecyclerViewHolder(View itemView) {
         super(itemView);
         userNameTv= (TextView) itemView.findViewById(R.id.userNameTv);
         intTimeTv= (TextView) itemView.findViewById(R.id.intTimeTv);
         outTimeTv= (TextView) itemView.findViewById(R.id.outTimeTv);
     }

 }
}
