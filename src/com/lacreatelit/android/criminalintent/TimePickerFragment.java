package com.lacreatelit.android.criminalintent;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimePickerFragment extends DialogFragment {

	static final String BUNDLE_EXTRA_TIME = 
			"com.lacreatelit.android.criminalintent.time";
	
	private Date mDate;
	
	
	// Enables the caller to pass in the input data to the Fragment
	public static TimePickerFragment newInstance(Date date) {
		
		Bundle args = new Bundle();
		args.putSerializable(BUNDLE_EXTRA_TIME, date);
		
		TimePickerFragment timePickerFragment = new TimePickerFragment();
		timePickerFragment.setArguments(args);
		
		return timePickerFragment;
		
	}
	
	private void sendResult(int resultCode) {
		
		if( getTargetFragment() == null)
			return;
		
		Intent intent = new Intent();
		intent.putExtra(BUNDLE_EXTRA_TIME, mDate);
		
		getTargetFragment()
			.onActivityResult(getTargetRequestCode(), resultCode, intent);	
		
	}
	
	// Process the passed in data from the caller.
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mDate = (Date)getArguments().getSerializable(BUNDLE_EXTRA_TIME);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_time, null);
		
		TimePicker timePicker = (TimePicker)view
									.findViewById(R.id.dialog_timePicker);
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minutes);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				
				mDate.setHours(hourOfDay);
				mDate.setMinutes(minute);
				getArguments().putSerializable(BUNDLE_EXTRA_TIME, mDate);
				
			}
		});
		
		
		return new AlertDialog.Builder(getActivity())
			.setView(view)
			.setPositiveButton(
					android.R.string.ok, 
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					sendResult(Activity.RESULT_OK);
					
				}
			})
			.create();
		
	}
	
	

}
