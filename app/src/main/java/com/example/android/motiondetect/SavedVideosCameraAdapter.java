package com.example.android.motiondetect;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alex on 4/6/2017.
 */

public class SavedVideosCameraAdapter {

    //inner class start
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        CardView cardView;
        ImageView imageView;
        TextView listItemNumberView;
        TextView fromTextView;
        TextView toTextView;
        TextView daysTextView;
        TextView urlTextView;

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
            urlTextView = (TextView) itemView.findViewById(R.id.url_textview);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
//            mOnClickListener.onListItemClicked(position);
        }

        @Override
        public boolean onLongClick(View view) {
            int pos = getAdapterPosition();
//            onLongClickListener.onItemLongClicked(pos);
            return true;
        }
    }//end of inner class
}
