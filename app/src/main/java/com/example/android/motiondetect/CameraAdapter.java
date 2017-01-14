package com.example.android.motiondetect;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by alex on 1/14/2017.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.MyViewHolder>{

    public CameraAdapter(){

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView listItemNumber;

        public MyViewHolder(View itemView){
            super(itemView);
            listItemNumber = (TextView) itemView.findViewById(R.id.camera_item_number);
        }
    }
}
