package com.lacreatelit.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CrimeFragment extends Fragment {

	static final String EXTRA_CRIME_ID = 
			"com.lacreatelit.android.criminalintent.crime_id";
	private static final String TAG = "CrimeFragment";
	
	static final String DIALOG_DATE = "date";
	static final String FRAG_MGR_DIALOG_TIME = "time";
	private static final String FRAG_MGR_DIALOG_CHOICE = "choice";

	// Request codes of CrimeFragment
	static final int REQUEST_DATE = 0;
	private static final int TARGET_REQUEST_TIME = 1;
	private static final int TARGET_REQUEST_CHOICE = 2;
	private static final int REQUEST_PHOTO = 3;
	
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private Button mTimeButton;
	private ChangeChoiceFragment mChangeChoiceFragment;
	
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	
	// Variables to handle the Contextual Action Bar 
	private Object mActionMode = null;
	//private ActionMode.Callback mActionModeCallback = null;
	
	public static CrimeFragment newInstance(UUID crimeId) {
		
		// Step 1 - Create the Bundle
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		// Step 2 - Create the CrimeFragment
		CrimeFragment crimeFragment = new CrimeFragment();
		
		// Step 3 - Attach the arguments to the fragment
		crimeFragment.setArguments(args);
		
		return crimeFragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		//mCrime = new Crime();
		
		//--No longer directly accessing the intent of the Activity because
		//--of close coupling.
		
		//UUID crimeId = (UUID)getActivity().getIntent()
		//		.getSerializableExtra(EXTRA_CRIME_ID);
		
		
		//Log.d(TAG, "Passed in ID = " + crimeId.toString());
		//CrimeLab.get(getActivity()).printCrimeListIDs();
		
		//mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		
		//---Use the Fragment.getArgument() method to retrieve the argument of
		//Fragment
		UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		
		
	} // end of onCreate()

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			
			
		}
		
		//============Set the Edit Text=========================================
		mTitleField = (EditText)view.findViewById(R.id.edit_crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, 
					int count) {
				
				mCrime.setTitle(s.toString());
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//============Set the Date Button ======================================
		
		mDateButton = (Button)view.findViewById(R.id.btn_crime_date);
		updateDate();
		//mDateButton.setEnabled(false);
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				/*
				DatePickerFragment dateDialog = DatePickerFragment
						.newInstance(mCrime.getDate());
				dateDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dateDialog.show(fragmentManager, DIALOG_DATE);
				*/
				mChangeChoiceFragment = new ChangeChoiceFragment();
				mChangeChoiceFragment.setTargetFragment(
							CrimeFragment.this, TARGET_REQUEST_CHOICE);
				mChangeChoiceFragment.show(fragmentManager, FRAG_MGR_DIALOG_CHOICE);
				
			}
		});
		
		//============Set the Time Button ======================================
		
		mTimeButton = (Button)view.findViewById(R.id.btn_crime_time);
		mTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {   
				
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				TimePickerFragment timeDialogFragment = TimePickerFragment
						.newInstance(mCrime.getDate());
				timeDialogFragment.setTargetFragment(CrimeFragment.this, 
						TARGET_REQUEST_TIME);
				timeDialogFragment.show(fragmentManager, FRAG_MGR_DIALOG_TIME);
				
			}
		});
		
		
		//============Set the Solved CheckBox ==================================
		mSolvedCheckBox = (CheckBox)view.findViewById(R.id.chk_crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				mCrime.setSolved(isChecked);
				
			}
		});
		
		//==========Register for a context menu================================
		final LinearLayout linearLayout = (LinearLayout)view
				.findViewById(R.id.linearlayout_crime_detail);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			registerForContextMenu(linearLayout);
		} else {
			
			final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
				
				// Called each time the action mode is shown. Always called after
				// onCreateActionMode() but may be called multiple times if the
				// mode is invalidated
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					
					mActionMode = null;
					
				}
				
				// Called when the ActionMode is created;startActionMode is called
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.crime_list_item_context, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					
					switch(item.getItemId()) {
						
					case R.id.menu_item_delete_crime:
						deleteCrimeAction();
						return true;
						
					default:
						return false;
						
					}
				
				}
			};
			
			linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					
					if(mActionMode != null)
						return false;
					
					mActionMode = linearLayout.startActionMode(actionModeCallback);
					return true;
				}
			});
			
		}
		
		//==========Set the Photo button========================================
		
		mPhotoButton = (ImageButton)view.findViewById(R.id.image_button_crime);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Log.i(TAG, "Starting the camera activity...");
				Intent intent = new Intent(getActivity(), 
						CrimeCameraActivity.class);
				startActivityForResult(intent, REQUEST_PHOTO);
				
			}
		});
		
		PackageManager packageManager = getActivity().getPackageManager();
		if( !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
			!packageManager.
			hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mPhotoButton.setEnabled(false);
		}
		
		//==========Set the Image View========================================
		mPhotoView = (ImageView)view.findViewById(R.id.imageView_crime);
		
		
		//==========All work done, return the fully constructed view============
		return view;
		
	}	// end of onCreateView() 

	@Override
	public void onActivityResult(
			int requestCode, int resultCode, Intent dataIntent) {
	
		if( resultCode != Activity.RESULT_OK) return;
		
		if(requestCode == REQUEST_DATE) {
			
			Log.i(TAG, "Processing date request...");
			processDateResult(dataIntent,
					DatePickerFragment.EXTRA_DATE, true);
			getActivity().getSupportFragmentManager()
			.beginTransaction()
			.remove(mChangeChoiceFragment)
			.commit();

			
		} else if (requestCode == TARGET_REQUEST_TIME) {

			Log.i(TAG, "Processing time request...");
			processDateResult(dataIntent, 
					TimePickerFragment.BUNDLE_EXTRA_TIME, false);
			getActivity().getSupportFragmentManager()
			.beginTransaction()
			.remove(mChangeChoiceFragment)
			.commit();

			
		} else if (requestCode == TARGET_REQUEST_CHOICE) {
			
			Log.i(TAG, "Processing target choice...");
			processChoiceRequest(dataIntent);			
			
		} else if (requestCode == REQUEST_PHOTO) {
			
			Log.i(TAG, "Porcessing photo request...");
			processPhotoRequest(dataIntent);
		}
		
	} // End - onActivityResult()
	
	
	
	private void processDateResult(Intent dataIntent, String fragmentExtra, 
			boolean isChangeTime) {

		Date date = (Date)dataIntent.getSerializableExtra(fragmentExtra); 
		Log.d(TAG, 
				"ProcessDateResult: year = " + Integer.toString(date.getYear()) 
				);
		if(isChangeTime) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(mCrime.getDate());
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int seconds = calendar.get(Calendar.SECOND);
			
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, seconds);
			date = calendar.getTime();
			
		}
		mCrime.setDate(date);
		updateDate();
	}
	
	private void updateDate() {
		
		mDateButton.setText(mCrime.getDate().toString());
	}
	
	private void processChoiceRequest(Intent intent) {
		
		int choice = intent
					.getIntExtra(ChangeChoiceFragment.BUNDLE_EXTRA_CHOICE, 1);
		
		if (choice == ChangeChoiceFragment.RESULT_CHOICE_DATE) {

			FragmentManager fragmentManager = getActivity()
					.getSupportFragmentManager();
		
			DatePickerFragment dateDialog = DatePickerFragment
					.newInstance(mCrime.getDate());
			dateDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
			dateDialog.show(fragmentManager, DIALOG_DATE);
			
		} else if (choice == ChangeChoiceFragment.RESULT_CHOICE_TIME) {
			
			FragmentManager fragmentManager = getActivity()
					.getSupportFragmentManager();
			TimePickerFragment timeDialogFragment = TimePickerFragment
					.newInstance(mCrime.getDate());
			timeDialogFragment.setTargetFragment(CrimeFragment.this, 
					TARGET_REQUEST_TIME);
			timeDialogFragment.show(fragmentManager, FRAG_MGR_DIALOG_TIME);
			
		}
		
	}
	
	private void processPhotoRequest(Intent dataIntent) {
		
		String filename = dataIntent
				.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
		
		if(filename != null) {
			
			Photo photo = new Photo(filename);
			mCrime.setPhoto(photo);
			showPhoto();
			Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		
		case android.R.id.home:
			returnToParentActivity();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	private void returnToParentActivity() {
		
		if(NavUtils.getParentActivityName(getActivity()) != null) {
			NavUtils.navigateUpFromSameTask(getActivity());
		}
		
	}

	@Override
	public void onPause() {
		
		CrimeLab.get(getActivity()).saveCrimes();
		super.onPause();
		
		
	}

	//===============Create the context menu for pre honey comb devices=========
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		getActivity().getMenuInflater()
				.inflate(R.menu.crime_list_item_context, menu);
		
	}

	
	//=============Respond to the Context Menu selection for pre honey comb devices==	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	
		switch(item.getItemId()) {
		
		case R.id.menu_item_delete_crime:
			deleteCrimeAction();
			return true;
		
		}
		
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
		
	}

	
	private void deleteCrimeAction() {
		
		CrimeLab.get(getActivity()).deleteCrime(mCrime);
		returnToParentActivity();
		
	}
	
	private void showPhoto() {
		
		// Set the image button's image based on the photo
		Photo photo = mCrime.getPhoto();
		BitmapDrawable bitmapDrawable = null;
		
		if(photo != null) {
			String path = getActivity().getFileStreamPath(photo.getFilename())
					.getAbsolutePath();
			bitmapDrawable = PictureUtils.getScaledDrawable(getActivity(),
					path);
		}
		mPhotoView.setImageDrawable(bitmapDrawable);
	}

	@Override
	public void onStart() {
		
		super.onStart();
		showPhoto();
	}

	@Override
	public void onStop() {
		
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
	

}
