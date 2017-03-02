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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

    //may need to change to non-static variable
    //private String[] data = {"Camera 1", "Camera 2", "Camera 3", "Camera 4", "Camera 5", "Camera 6"};
    public static ArrayList<String> cameraNameList;

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

        //TODO query cameras from this user
        cameraNameList = getCamerasRequest();
//        cameraNameList = new ArrayList<>(Arrays.asList("Camera 1", "Camera 2"));

        //instantiate adapter with data and both click listeners below
        mAdapter = new CameraAdapter(cameraNameList, this, this);
        mNumbersList.setAdapter(mAdapter);
        return view;
    }

    private ArrayList<String> getCamerasRequest(){

        ArrayList<String> stringArrayList = new ArrayList<>(Arrays.asList("Camera 1", "Camera 2"));
        Toast.makeText(getActivity(), "get cmera req", Toast.LENGTH_SHORT).show();
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/camera/";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray reader;

                try {
                    reader = new JSONArray(response);
                    JSONObject jsonObject = reader.getJSONObject(0);
                    String cameraName = jsonObject.getString("name");
                    Toast.makeText(getActivity(), cameraName, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        hideDialog();
//                        Log.i("Error.Response", error.getMessage());
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
        return stringArrayList;

/*
        RequestQueue queue = Volley.newRequestQueue(this);

        progressDialog.setMessage("Logging in ...");
        //show dialog
        showDialog();

//        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=2416aeb32b3e3d8360593abf67e88ddc";
        String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/login/";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                JSONObject reader;

                //TODO Token needs be accessible everywhere
                try {
                    reader = new JSONObject(response);
                    token = reader.getString("token");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(LoginActivity.this, "Login Success!! Tok = " + token, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, CameraListActivity.class);
                startActivity(intent);
            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        Log.i("Error.Response", error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        queue.add(strReq);*/
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
}
