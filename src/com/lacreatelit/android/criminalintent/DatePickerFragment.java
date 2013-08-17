package com.lacreatelit.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {

	public static final String EXTRA_DATE = 
			"com.lacreatelit.android.criminalintent.date";
	private static final String TAG = "DatePickerFragment";
	
	private Date mDate;
	
	private int mHour;
	private int mMinute;
	
	public static DatePickerFragment newInstance(Date date) {
		
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setArguments(args);
		
		return datePickerFragment;
	}
	
	private void sendResult(int resultCode) {
		
		if(getTargetFragment() == null)
			return;
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment()
			.onActivityResult(getTargetRequestCode(), resultCode, intent);
			
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
		
		// Parse the passed in date into the individual components 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_date, null);
		
		DatePicker datePicker = (DatePicker)view
				.findViewById(R.id.dialog_datePicker);
		datePicker.init(year, month, day, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int month,
					int day) {
				
				Log.d(TAG, "Year = " + Integer.toString(year));
				mDate.setYear(year);
				mDate.setMonth(month);
				mDate.setDate(day);
				
				mDate = new GregorianCalendar(year, month, day).getTime();
				
				getArguments().putSerializable(EXTRA_DATE, mDate);
				
			}
			
		});
		
		return new AlertDialog.Builder(getActivity())
				.setView(view)
				.setTitle(R.string.date_picker_title)
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
