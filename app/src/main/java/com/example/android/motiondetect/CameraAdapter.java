package com.example.android.motiondetect;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alex on 1/14/2017.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.MyViewHolder>{
    private static final String TAG = CameraAdapter.class.getSimpleName();
    private ArrayList<String> mNumberItems;
    private ListItemClickListener mOnClickListener;
    private OnLongClickListener onLongClickListener; //Reference CameraAdapter.OnLongClickListener

    //data to be sent in
    public CameraAdapter(ArrayList<String> numberOfItems, ListItemClickListener listItemClickListener, OnLongClickListener onLongClickListener){
        this.onLongClickListener = onLongClickListener;
        mOnClickListener = listItemClickListener;
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

    //set text in textview to new MyViewHolder created
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.listItemNumberView.setText(mNumberItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mNumberItems.size();
    }

    public interface ListItemClickListener{
        void onListItemClicked(int indexClicked);
    }

    public interface OnLongClickListener{
        void onItemLongClicked(int pos);
    }
    //inner class start
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        CardView cardView;
        ImageView imageView;
        TextView listItemNumberView;
        TextView fromTextView;
        TextView toTextView;
        TextView daysTextView;

        public MyViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardViewItem);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewItem);
            imageView.setImageResource(R.mipmap.camera);
            listItemNumberView = (TextView) itemView.findViewById(R.id.camera_item_number);

            //TODO set notification time and days from get request
            fromTextView = (TextView) itemView.findViewById(R.id.from_textview);
            toTextView = (TextView) itemView.findViewById(R.id.to_textview);
            daysTextView = (TextView) itemView.findViewById(R.id.days_textview);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClicked(position);
        }

        @Override
        public boolean onLongClick(View view) {
            int pos = getAdapterPosition();
            onLongClickListener.onItemLongClicked(pos);
            return true;
        }
    }//end of inner class
}
