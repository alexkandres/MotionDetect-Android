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

public class SavedVideosFragment extends Fragment implements SavedVideosCameraAdapter.ListItemClickListener, SavedVideosCameraAdapter.OnLongClickListener{

    private SavedVideosCameraAdapter savedVideosCameraAdapter;
    private ArrayList<String> savedVideoList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();
    private ArrayList<String> urlDeleteList = new ArrayList<>();

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
        savedVideoList = new ArrayList<>(Arrays.asList("Saved Video 1", "Saved Video 2"));
        urlList = new ArrayList<>(Arrays.asList("example.com", "exampletest.com"));

        //instantiate adapter with data and both click listeners below
        savedVideosCameraAdapter = new SavedVideosCameraAdapter(savedVideoList,urlList , this, this);
        mNumbersList.setAdapter(savedVideosCameraAdapter);
        return view;
    }

    @Override
    public void onListItemClicked(int indexClicked) {
        //TODO create intent
        //TODO pass url to intent
        //TODO start intnent
    }

    @Override
    public void onItemLongClicked(int pos) {
        //TODO dialog popup
        //TODO dialog presets edit
        //TODO dialog presets delete
    }
}
