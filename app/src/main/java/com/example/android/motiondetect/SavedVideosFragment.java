package com.example.android.motiondetect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 4/5/2017.
 */

public class SavedVideosFragment extends Fragment implements SavedVideosCameraAdapter.ListItemClickListener, SavedVideosCameraAdapter.OnLongClickListener{

    String TAG = SavedVideosFragment.class.getSimpleName();
    private SavedVideosCameraAdapter savedVideosCameraAdapter;
    private ArrayList<String> savedVideoList = new ArrayList<>();
    private ArrayList<String> thumbnailurls = new ArrayList<>();
    private ArrayList<String> urlDeleteList = new ArrayList<>();
    private ArrayList<String> videoNameList = new ArrayList<>();

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

        getVideoRequest();
//        savedVideoList = new ArrayList<>(Arrays.asList("Saved Video 1", "Saved Video 2"));
//        thumbnailurls = new ArrayList<>(Arrays.asList("example.com", "exampletest.com"));

        //instantiate adapter with data and both click listeners below
        savedVideosCameraAdapter = new SavedVideosCameraAdapter(savedVideoList, thumbnailurls, videoNameList, this, this);
        mNumbersList.setAdapter(savedVideosCameraAdapter);
        return view;
    }

    private void getVideoRequest(){

        //TODO process request
//        http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/video/list/hello/
        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/video/list/" + LoginActivity.username + "/";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray reader;
                try {
                    reader = new JSONArray(response);
//                    {
//                        "id": 71,
//                            "path": "https://s3.amazonaws.com/sketchflow/hello/04052017191527.mp4",
//                            "thumbnail": "https://s3.amazonaws.com/sketchflow/hello/04052017191527.jpg",
//                            "size": 142.6,
//                            "delete": "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/video/delete/71/"
//                    },

                    //TODO add time, day, address to camera name list
                    //put name of each camera name in an Arraylist
                    for(int i = 0; i < reader.length(); i++){
                        JSONObject jsonObject = reader.getJSONObject(i);
                        String videoUrl = jsonObject.getString("path");
                        String thumbnailUrl = jsonObject.getString("thumbnail");
                        String deleteVideoUrl = jsonObject.getString("delete");
                        String videoName = jsonObject.getString("video_name");
                        savedVideoList.add(videoUrl);
                        thumbnailurls.add(thumbnailUrl);
                        urlDeleteList.add(deleteVideoUrl);
                        videoNameList.add(videoName);
                    }
                    savedVideosCameraAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Error with saved video request");
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", LoginActivity.token);
                return map;
            }
        };
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(strReq);
    }
    @Override
    public void onListItemClicked(int indexClicked) {

        //TODO send url
        Intent intent = new Intent(getActivity(), StreamSavedVideoActivity.class);
        intent.putExtra("video_url", savedVideoList.get(indexClicked));
        startActivity(intent);
    }

    @Override
    public void onItemLongClicked(int pos) {
        //TODO dialog popup
        //TODO dialog presets edit
        //TODO dialog presets delete
    }
}
