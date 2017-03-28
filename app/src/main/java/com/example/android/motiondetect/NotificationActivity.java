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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

                //TODO send request to update time and days for notification, then call adapter.onchanged
                Intent intent = new Intent();

                //send from and to time
                //set key so calling activity may retreive the data
                intent.putExtra("time_key", fromTime.getText().toString() + toTime.getText().toString());

                //save the days to be notified
                checkDays();
                intent.putExtra("days_key", days);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private String[] setNotificationTimes() {

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/schedule/detail/" + LoginActivity.username + "/";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject reader;
                try {
                    reader = new JSONObject(response);
                    times[0] = reader.getString("time_from");
                    times[1] = reader.getString("time_to");
                    Log.i("CameraListActivityyyy", "Post request works");

                    //set from and to time
                    fromTime.setText(times[0]);
                    toTime.setText(times[1]);

                    //set days to be notified
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

        return times;
    }

    private void checkDays(){

        checkBox1 = (CheckBox) findViewById(R.id.mon_checkbox);
        checkBox2 = (CheckBox) findViewById(R.id.tue_checkbox);
        checkBox3 = (CheckBox) findViewById(R.id.wed_checkbox);
        checkBox4 = (CheckBox) findViewById(R.id.thu_checkbox);
        checkBox5 = (CheckBox) findViewById(R.id.fri_checkbox);
        checkBox6 = (CheckBox) findViewById(R.id.sat_checkbox);
        checkBox7 = (CheckBox) findViewById(R.id.sun_checkbox);

        days[0] = checkBox1.isChecked();
        days[1] = checkBox2.isChecked();
        days[2] = checkBox3.isChecked();
        days[3] = checkBox4.isChecked();
        days[4] = checkBox5.isChecked();
        days[5] = checkBox6.isChecked();
        days[6] = checkBox7.isChecked();
    }
}
