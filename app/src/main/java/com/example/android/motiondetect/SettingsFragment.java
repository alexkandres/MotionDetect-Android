package com.example.android.motiondetect;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    Preference datePicker;
    CheckBoxPreference checkBoxPreference;
    private boolean isActive;
    private String[] times = new String[2];
    private boolean days[] = new boolean[7];
    int requestCode = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_seetings);

        checkBoxPreference = (CheckBoxPreference) findPreference("pref_sync");
        setNotificationTimes();

        //Set on change listener to Shared preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        datePicker = findPreference("datePicker");
        //refers to overriden onPreferenceClick below
        datePicker.setOnPreferenceClickListener(this);

    }
    private void setNotificationTimes() {

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/schedule/detail/" + LoginActivity.username + "/";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject reader;
                try {
                    reader = new JSONObject(response);
                    isActive = reader.getBoolean("is_active");

                    if(isActive){
                        checkBoxPreference.setChecked(true);
                    }
                    else {
                        checkBoxPreference.setChecked(false);
                    }
                    times[0] = reader.getString("time_from");
                    times[1] = reader.getString("time_to");

                    //get booleans from requests
                    days[0] = reader.getBoolean("monday");
                    days[1] = reader.getBoolean("tuesday");
                    days[2] = reader.getBoolean("wednesday");
                    days[3] = reader.getBoolean("thursday");
                    days[4] = reader.getBoolean("friday");
                    days[5] = reader.getBoolean("saturday");
                    days[6] = reader.getBoolean("sunday");

                    processTimes(times);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        hideDialog();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(strReq);
    }

    private void sendNotificationPost(){

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/schedule/detail/" + LoginActivity.username + "/";
        StringRequest strReq = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject reader;
                try {
                    reader = new JSONObject(response);

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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("is_active", (isActive)? "1": "0");
                params.put("time_from", times[0]);
                params.put("time_to", times[1]);
                params.put("monday", (days[0])? "1": "0");
                params.put("tuesday", (days[1])? "1": "0");
                params.put("wednesday", (days[2])? "1": "0");
                params.put("thursday", (days[3])? "1": "0");
                params.put("friday", (days[4])? "1": "0");
                params.put("saturday",(days[5])? "1": "0");
                params.put("sunday", (days[6])? "1": "0");
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(strReq);
    }
    private void processTimes(String [] times){
        //00:00:00
        String[] arrHourMin = times[0].split(":");
        String[] arrHourMin2 = times[1].split(":");

        //display format h = 12hr clk, mm = minute, a = am/pm
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mma");
        Time timefrom = new Time(Integer.parseInt(arrHourMin[0]), Integer.parseInt(arrHourMin[1]), 0);
        Time timeto = new Time(Integer.parseInt(arrHourMin2[0]), Integer.parseInt(arrHourMin2[1]), 0);

        String from = simpleDateFormat.format(timefrom);
        String to = simpleDateFormat.format(timeto);

        //update time by reference
        times[0] = from;
        times[1] = to;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch(key){
            case "pref_sync":

                //update is_active field for post request
                isActive = checkBoxPreference.isChecked();
                sendNotificationPost();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        String key = preference.getKey();
        switch(key){
            case "datePicker":

                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivityForResult(intent, requestCode);
                break;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check wich activity for result called
        if(requestCode == this.requestCode){

            if(resultCode == Activity.RESULT_OK){
                //TODO do something with data if i want
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
