package com.example.android.motiondetect;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.util.Arrays;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    Preference datePicker;
    Preference timePicker;
    int requestCode = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_seetings);

        addPreferencesFromResource(R.xml.activity_seetings);
        datePicker = findPreference("datePicker");
        timePicker = findPreference("timePicker");

        //refers to overriden onPreferenceClick below
        datePicker.setOnPreferenceClickListener(this);
        timePicker.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        String key = preference.getKey();
        switch(key){
            case "datePicker":

                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivityForResult(intent, requestCode);
                break;
            case "timePicker":
                //TODO Remove time picker, this will go into activity instead of dialog
                DialogFragment timeDialogFragment = new TimePickerFragment();
                timeDialogFragment.show(getFragmentManager(), "Time Picker");
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
