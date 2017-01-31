package com.example.android.motiondetect;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.app.DialogFragment;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    Preference datePicker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_seetings);

        addPreferencesFromResource(R.xml.activity_seetings);
        datePicker = findPreference("datePicker");

        //refers to overriden onPreferenceClick below
        datePicker.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        String key = preference.getKey();
        if(key.equalsIgnoreCase("datePicker")){
            Toast.makeText(getActivity(), "Date Picker", Toast.LENGTH_SHORT).show();
            DialogFragment newDialogFragment = new DatePickerFragment();
            newDialogFragment.show(getFragmentManager(), "DATE PICKER");
        }
        return false;
    }
}
