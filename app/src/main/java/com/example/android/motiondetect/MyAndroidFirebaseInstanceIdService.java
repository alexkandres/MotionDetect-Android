package com.example.android.motiondetect;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 3/6/2017.
 */

public class MyAndroidFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyAndroidFCMIIDService";
    String androidToken;

    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        androidToken = FirebaseInstanceId.getInstance().getToken();
        //Log the token
        Log.d(TAG, "Refreshed token: " + androidToken);
        sendRegistrationToServer();
    }
    private void sendRegistrationToServer() {
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
        MySingleton.getInstance(getApplication().getApplicationContext()).addToRequestQueue(strReq);
    }
}
