package com.example.android.motiondetect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraListActivityFragment extends Fragment {

    private static final int NUM_LIST_ITEMS = 100;
    private CameraAdapter mAdapter;
    private RecyclerView mNumbersList;
    private String[] data = {"Camera 1", "Camera 2", "Camera 3", "Camera 4", "Camera 5", "Camera 6"};
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //set manager to recyclerview
        mNumbersList.setLayoutManager(layoutManager);
        //instantiate adapter with data
        mAdapter = new CameraAdapter(data);
        mNumbersList.setAdapter(mAdapter);
        return view;
    }
}
