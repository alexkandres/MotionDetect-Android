package com.example.android.motiondetect;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alex on 4/6/2017.
 */

public class SavedVideosCameraAdapter extends RecyclerView.Adapter<SavedVideosCameraAdapter.MyViewHolder>{

    private static final String TAG = SavedVideosCameraAdapter.class.getSimpleName();
    private ArrayList<String> nameList;
    private ArrayList<String> urlList;
    private ArrayList<String> videoNameList;
    //TODO may not need this in Adapter. Does not have to display in CardView. Just save
    private ArrayList<String> deleteUrlList;
    private SavedVideosCameraAdapter.ListItemClickListener mOnClickListener;
    private SavedVideosCameraAdapter.OnLongClickListener onLongClickListener; //Reference CameraAdapter.OnLongClickListener

    //data to be sent in
    public SavedVideosCameraAdapter(ArrayList<String> numberOfItems, ArrayList<String> urlList, ArrayList<String> videoNameList,SavedVideosCameraAdapter.ListItemClickListener listItemClickListener, SavedVideosCameraAdapter.OnLongClickListener onLongClickListener){
        mOnClickListener = listItemClickListener;
        this.onLongClickListener = onLongClickListener;
        nameList = numberOfItems;
        this.urlList = urlList;
        this.videoNameList = videoNameList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //get context from viewgroup
        Context context = viewGroup.getContext();
        //pass in context into layoutinflater to retreive inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        //pass in layout, viewgroup, and boolean into inflate for view
        View view = inflater.inflate(R.layout.saved_videos_layout, viewGroup,false);
        //instantiate a new myviewholder with the view that we just inflated
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //display Name in listItemNumberView textview
        //display array item text in textview
        holder.listItemNumberView.setText(videoNameList.get(position));
        Context context = holder.imageView.getContext();
        //use picasso to display urlimg in imageview
        Picasso.with(context).load(urlList.get(position)).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public void clear(){
        nameList.clear();
        urlList.clear();
        videoNameList.clear();
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

        public MyViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardViewItem_saved_video);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_saved_video);
            imageView.setImageResource(R.mipmap.camera);
            listItemNumberView = (TextView) itemView.findViewById(R.id.saved_video_name);

            //TODO set notification time and days from get request
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
