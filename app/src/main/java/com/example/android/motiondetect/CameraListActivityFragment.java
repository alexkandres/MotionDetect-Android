package com.example.android.motiondetect;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraListActivityFragment extends Fragment implements CameraAdapter.ListItemClickListener, CameraAdapter.OnLongClickListener{

    public static CameraAdapter mAdapter;
    private RecyclerView mNumbersList;
    Toast mToast;
    String androidToken;

    //may need to change to non-static variable
    public static ArrayList<String> cameraNameList = new ArrayList<>();
    public static ArrayList<String> urlList = new ArrayList<>();

    //constructor
    public CameraListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //token for this android device
        //TODO save token to api with correct headers
        androidToken = FirebaseInstanceId.getInstance().getToken();
        Log.i("CameraListActivittttt", androidToken);
//        sendAndroidToken();

        //inflate view
        View view = inflater.inflate(R.layout.fragment_camera_list, container, false);

        //load aws
        new LoadAwsTask().execute();

        //find recycler view
        mNumbersList = (RecyclerView) view.findViewById(R.id.camera_numbers);

        //get layout managers
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //set manager to recyclerview
        mNumbersList.setLayoutManager(layoutManager);

        getCamerasRequest();
//        cameraNameList = new ArrayList<>(Arrays.asList("Camera 1", "Camera 2"));

        //instantiate adapter with data and both click listeners below
        mAdapter = new CameraAdapter(cameraNameList,urlList , this, this);
        mNumbersList.setAdapter(mAdapter);
        return view;
    }

    private void sendAndroidToken() {

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/androidtoken/" + LoginActivity.id + "/";
        StringRequest strReq = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", "Ahhhh");
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", LoginActivity.token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("android_token", androidToken);
                return params;
            }
        };
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(strReq);
    }

    private void getCamerasRequest(){

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/camera/";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray reader;
                try {
                    reader = new JSONArray(response);
//                    {
//                        "cid": 4,
//                            "name": "hello",
//                            "address": "hello",
//                            "created_at": "2017-02-23T21:36:28Z",
//                            "is_active": true
//                    },

                    //TODO add time, day, address to camera name list
                    //put name of each camera name in an Arraylist
                    for(int i = 0; i < reader.length(); i++){
                        JSONObject jsonObject = reader.getJSONObject(i);
                        String cameraName = jsonObject.getString("name");
                        String urlName = jsonObject.getString("address");
                        cameraNameList.add(cameraName);
                        urlList.add(urlName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", "Ahhhh");
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
                //TODO Reset adapter so RecyclerView can have correct data
                //TODO empty adapter
                //TODO call getCamera request
                //TODO notify on changed adapter
                //get values from keys
                String time = data.getStringExtra("time_key");
                boolean days[] = data.getBooleanArrayExtra("days_key");
                Toast.makeText(getActivity(), Arrays.toString(days), Toast.LENGTH_LONG).show();
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getActivity(), "No notification change", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LoadAwsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getActivity(),
                    "us-east-1:844f80c7-2fbf-40b1-b7d4-472e608e3197", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );

            //send credentials to registration constructor
            Registration registration = new Registration(credentialsProvider);
            registration.registerWithSNS();

            return null;
        }
    }
}
