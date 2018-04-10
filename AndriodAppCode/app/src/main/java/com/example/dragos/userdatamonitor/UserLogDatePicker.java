package com.example.dragos.userdatamonitor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * A Class for allowing Vini to choose the date he wants to view user data on
 * Created by Dragos on 9/25/17.
 */

public class UserLogDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private AsyncResponse delegate = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.delegate.datePicked((month + 1) + "/" + dayOfMonth + "/"  + year);
    }

    // SETTER:
    public void setDelegate (AsyncResponse delegate) {
        this.delegate = delegate;
    }


}
