package com.example.android.motiondetect;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    private TextView fromTime;
    private TextView toTime;
    private Button cancelButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_notification_dialog);

        //From time text view and click listener
        fromTime = (TextView) findViewById(R.id.from_time);
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
                                fromTime.setText(s + " - ");
                            }
                        }, hour, minute,
                        DateFormat.is24HourFormat(NotificationActivity.this));

                //display dialog
                timePickerDialog.show();

            }
        });

        toTime = (TextView) findViewById(R.id.to_time);
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

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                //send from and to time
                //set key so calling activity may retreive the data
                intent.putExtra("notification_key", fromTime.getText().toString() + toTime.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
