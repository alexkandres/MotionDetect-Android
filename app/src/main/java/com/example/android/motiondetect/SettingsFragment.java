package com.example.android.motiondetect;

import android.app.DialogFragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    Preference datePicker;
    Preference timePicker;
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

/*      char grade = 'C';

        switch(grade) {
            case 'A' :
                System.out.println("Excellent!");
                break;
            case 'B' :
            case 'C' :
                System.out.println("Well done");
                break;
            case 'D' :
                System.out.println("You passed");
            case 'F' :
                System.out.println("Better try again");
                break;
            default :
                System.out.println("Invalid grade");
        }
        */
        String key = preference.getKey();
        switch(key){
            case "datePicker":
                DialogFragment newDialogFragment = new DatePickerFragment();
                newDialogFragment.show(getFragmentManager(), "DATE PICKER");
                break;
            case "timePicker":
                Toast.makeText(getActivity(), "Time Picker", Toast.LENGTH_SHORT).show();
        }
//        if(key.equalsIgnoreCase("datePicker")){
//            DialogFragment newDialogFragment = new DatePickerFragment();
//            newDialogFragment.show(getFragmentManager(), "DATE PICKER");
//        }
        return false;
    }
}
