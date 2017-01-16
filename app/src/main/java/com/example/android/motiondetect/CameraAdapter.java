package com.example.android.motiondetect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by alex on 1/14/2017.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.MyViewHolder>{
    private static final String TAG = CameraAdapter.class.getSimpleName();
    private int mNumberItems;

    //data to be sent in
    public CameraAdapter(int numberOfItems){
        mNumberItems = numberOfItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //get context from viewgroup
        Context context = viewGroup.getContext();
        //pass in context into layoutinflater to retreive inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        //pass in layout, viewgroup, and boolean into inflate for view
        View view = inflater.inflate(R.layout.number_list_item, viewGroup,false);
        //instantiate a new myviewholder with the view that we just inflated
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    //inner class start
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView listItemNumberView;

        public MyViewHolder(View itemView){
            super(itemView);
            listItemNumberView = (TextView) itemView.findViewById(R.id.camera_item_number);
        }

        public void bind(int listIndex) {
            listItemNumberView.setText(String.valueOf(listIndex));
        }

    }//end of inner class
}
