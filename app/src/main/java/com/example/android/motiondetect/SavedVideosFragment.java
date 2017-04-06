package com.example.android.motiondetect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alex on 4/5/2017.
 */

public class SavedVideosFragment extends Fragment {

    public static ArrayList<String> cameraNameList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_saved_videos, container, false);

        //find recycler view
        RecyclerView mNumbersList = (RecyclerView) view.findViewById(R.id.recycler_view_saved_videos);

        //get layout managers
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //set manager to recyclerview
        mNumbersList.setLayoutManager(layoutManager);

////        getCamerasRequest();
        cameraNameList = new ArrayList<>(Arrays.asList("Camera 1", "Camera 2"));

        //instantiate adapter with data and both click listeners below
////        mAdapter = new CameraAdapter(cameraNameList,urlList , this, this);
//        mNumbersList.setAdapter(mAdapter);
        return view;
    }
}
