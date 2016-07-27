package com.transility.tim.android.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.transility.tim.android.bean.DeviceReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class to attach data into inflated views.
 * Created by ambesh.kukreja on 7/27/2016.
 */
public class ReportsArrayAdapter extends RecyclerView.Adapter<ReportsArrayAdapter.ReportsRecyclerViewHolder>{


    @Override
    public ReportsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ReportsRecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected class ReportsRecyclerViewHolder extends RecyclerView.ViewHolder{

     public ReportsRecyclerViewHolder(View itemView) {
         super(itemView);
     }

 }
}
