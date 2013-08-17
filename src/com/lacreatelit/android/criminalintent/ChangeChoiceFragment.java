package com.lacreatelit.android.criminalintent;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

public class ChangeChoiceFragment extends DialogFragment {

	static final String BUNDLE_EXTRA_CHOICE
					= "com.lacreatelit.android.criminalintent.choice";
	static final int RESULT_CHOICE_DATE = 1;
	static final int RESULT_CHOICE_TIME = 2;
	
	//private Date mDate;
	
	private void sendResult(int resultCode, int choice) {
		
		if (getTargetFragment() == null) return;
		
		Intent intent = new Intent();
		intent.putExtra(BUNDLE_EXTRA_CHOICE, choice);
		
		getTargetFragment()
			.onActivityResult(getTargetRequestCode(), resultCode, intent);
		
	}
	
//	public static ChangeChoiceFragment newInstance(Date date) {
//		
//		Bundle args = new Bundle();
//		args.putSerializable(BUNDLE_EXTRA_DATETIME, date);
//		
//		ChangeChoiceFragment changeChoiceFragment = new ChangeChoiceFragment();
//		changeChoiceFragment.setArguments(args);
//		
//		return changeChoiceFragment;
//	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		//mDate = (Date)getArguments().getSerializable(BUNDLE_EXTRA_DATETIME);
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_change_choice, null);
		
		//-----------Setup the date choice button-------------------------------
		Button btn_change_date = 
						(Button)view.findViewById(R.id.btn_change_date);
		btn_change_date.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				sendResult(Activity.RESULT_OK, RESULT_CHOICE_DATE);
				
			}
		});
		
		//-----------Setup the date choice button-------------------------------
		Button btn_change_time =
				(Button)view.findViewById(R.id.btn_change_time);
		btn_change_time.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				sendResult(Activity.RESULT_OK, RESULT_CHOICE_TIME);
				
			}
		});
		
		//----------- Return the dialog associated with this Fragment----------
		return new AlertDialog.Builder(getActivity())
				.setView(view)
				.create();
	
	}
	
	

}
