package com.lacreatelit.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class CrimePagerActivity extends FragmentActivity {

	private static String TAG = "CrimePagerActivity";
	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;
	

	@Override
	protected void onCreate(Bundle onSavedBundleState) {
		
		// Initialize the Activity
		super.onCreate(onSavedBundleState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);
		
		// Setup access to the Model data object
		mCrimes = CrimeLab.get(this).getCrimes();
		
		// Configure the View hierarchy
		
		// Step 1 - set up the Adapter that will supply data to the 
		// view
		FragmentManager fragmentManager = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
			
			@Override
			public int getCount() {
				
				return mCrimes.size();
			}
			
			@Override
			public Fragment getItem(int pos) {
				
				Log.d(TAG, "Position clicked = " + Integer.toString(pos));
				Crime crime = mCrimes.get(pos);
				return CrimeFragment.newInstance(crime.getId());
			}
		});
		
		// Step 2 - set up the Page Listener
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				
				Crime crime = mCrimes.get(pos);
				if (crime.getTitle() != null) {
					setTitle(crime.getTitle());
				}				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Decode the data sent out in the Intent() and configure the View 
		// hierarchy of the Activity
		UUID crimeId = (UUID)getIntent()
				.getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		
		for(int i = 0; i < mCrimes.size(); i++ ) {
			
			if( mCrimes.get(i).getId().equals(crimeId)) {
	
				mViewPager.setCurrentItem(i);
				break;
			}
			
		} // end - for loop
		
	} // end - onCreate() 	

} // end - class definition
