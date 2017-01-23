package com.example.android.motiondetect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraListActivityFragment extends Fragment {

    private static final int NUM_LIST_ITEMS = 100;
    public static CameraAdapter mAdapter;
    private RecyclerView mNumbersList;

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

        //instantiate adapter with data
        mAdapter = new CameraAdapter(cameraNameList);
        mNumbersList.setAdapter(mAdapter);
        return view;
    }
}
