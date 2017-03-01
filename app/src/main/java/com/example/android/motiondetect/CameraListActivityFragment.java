package com.example.android.motiondetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraListActivityFragment extends Fragment implements CameraAdapter.ListItemClickListener, CameraAdapter.OnLongClickListener{

    private static final int NUM_LIST_ITEMS = 100;
    public static CameraAdapter mAdapter;
    private RecyclerView mNumbersList;
    Toast mToast;

    //may need to change to non-static variable
    //private String[] data = {"Camera 1", "Camera 2", "Camera 3", "Camera 4", "Camera 5", "Camera 6"};
    public static ArrayList<String> cameraNameList =  new ArrayList<>(Arrays.asList("Camera 1", "Camera 2"));

    //constructor
    public CameraListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate view
        View view = inflater.inflate(R.layout.fragment_camera_list, container, false);

        //find recycler view
        mNumbersList = (RecyclerView) view.findViewById(R.id.camera_numbers);

        //get layout managers
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //set manager to recyclerview
        mNumbersList.setLayoutManager(layoutManager);

        //instantiate adapter with data and both click listeners below
        mAdapter = new CameraAdapter(cameraNameList, this, this);
        mNumbersList.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onListItemClicked(int indexClicked) {
        if(mToast != null){
            mToast.cancel();
        }
        Intent intent = new Intent(getActivity(), LiveActivityMainActivity.class);
        intent.putExtra("Camera Name", indexClicked);
        startActivity(intent);
    }

    private int requestCode = 1;
    @Override
    public void onItemLongClicked(int pos) {
        Intent intent = new Intent(getActivity(), NotificationActivity.class);
        intent.putExtra("Camera Name", pos);
        startActivityForResult(intent, requestCode);
    }


    //will be called after NotificationActivity finishes
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check wich activity for result called
        if(requestCode == this.requestCode){

            if(resultCode == Activity.RESULT_OK){
                //TODO Reset adapter to RecyclerView can have correct data
                //check status ok/cancel
                String time = data.getStringExtra("notification_key");
                Toast.makeText(getActivity(), time, Toast.LENGTH_LONG).show();
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getActivity(), "No notification change", Toast.LENGTH_LONG).show();
            }
        }
    }
}
