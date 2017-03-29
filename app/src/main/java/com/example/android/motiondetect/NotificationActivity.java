package com.example.android.motiondetect;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private TextView fromTime;
    private TextView toTime;
    private Button cancelButton;
    private Button saveButton;
    private boolean days[] = new boolean[7];
    private String[] times = new String[2];
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private CheckBox checkBox7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_notification_dialog);

        //From time text view and click listener
        fromTime = (TextView) findViewById(R.id.from_time);
        toTime = (TextView) findViewById(R.id.to_time);

        //find checkview checkboxes
        checkBox1 = (CheckBox) findViewById(R.id.mon_checkbox);
        checkBox2 = (CheckBox) findViewById(R.id.tue_checkbox);
        checkBox3 = (CheckBox) findViewById(R.id.wed_checkbox);
        checkBox4 = (CheckBox) findViewById(R.id.thu_checkbox);
        checkBox5 = (CheckBox) findViewById(R.id.fri_checkbox);
        checkBox6 = (CheckBox) findViewById(R.id.sat_checkbox);
        checkBox7 = (CheckBox) findViewById(R.id.sun_checkbox);

        //get from and to time from API
        setNotificationTimes();

        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                TimePickerDialog timePickerDialog=  new TimePickerDialog(NotificationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            //set textview to what the user selected (onTimeSet called after user selection)
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                Time time = new Time(hour, minute, 0);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mma");
                                String s = simpleDateFormat.format(time);
                                fromTime.setText(s);
                            }
                        }, hour, minute,
                        DateFormat.is24HourFormat(NotificationActivity.this));

                //display dialog
                timePickerDialog.show();

            }
        });

        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                TimePickerDialog timePickerDialog=  new TimePickerDialog(NotificationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            //set textview to what the user selected (onTimeSet called after user selection)
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                //ex 12:00[AM][PM]
                                Time time = new Time(hour, minute, 0);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mma");
                                String s = simpleDateFormat.format(time);
                                toTime.setText(s);
                            }
                        }, hour, minute,
                        false);

                //display dialog
                timePickerDialog.show();
            }
        });
        //cancel button
        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        //save button listener
        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get time from textview
                String fromtime = fromTime.getText().toString();
                String totime = toTime.getText().toString();

                //save the days to be notified
                checkDays();

//                SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
//                SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
//                Date date = parseFormat.parse("10:30 PM");
//                System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));

                //change format to 00:00:00 format
                SimpleDateFormat parsTo = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat parseFrom = new SimpleDateFormat("h:mma");
                try {
                    Date date = parseFrom.parse(fromtime);
                    Date dateto = parseFrom.parse(totime);
                    times[0] = parsTo.format(date);
                    times[1] = parsTo.format(dateto);

                    sendNotificationPost();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //set key so calling activity may retreive the data
                Intent intent = new Intent();
                intent.putExtra("time_key", fromtime + totime);
                intent.putExtra("days_key", days);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq);
    }
    private void setNotificationTimes() {

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/schedule/detail/" + LoginActivity.username + "/";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject reader;
                try {
                    reader = new JSONObject(response);
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

                    //set from and to time
                    fromTime.setText(times[0]);
                    toTime.setText(times[1]);

                    //set check boxes
                    checkBox1.setChecked(days[0]);
                    checkBox2.setChecked(days[1]);
                    checkBox3.setChecked(days[2]);
                    checkBox4.setChecked(days[3]);
                    checkBox5.setChecked(days[4]);
                    checkBox6.setChecked(days[5]);
                    checkBox7.setChecked(days[6]);

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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq);
    }

    private void checkDays(){

        days[0] = checkBox1.isChecked();
        days[1] = checkBox2.isChecked();
        days[2] = checkBox3.isChecked();
        days[3] = checkBox4.isChecked();
        days[4] = checkBox5.isChecked();
        days[5] = checkBox6.isChecked();
        days[6] = checkBox7.isChecked();
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
}
