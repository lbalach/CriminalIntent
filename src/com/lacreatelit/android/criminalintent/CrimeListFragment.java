package com.lacreatelit.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	
	private static final String TAG = "CrimeListFragment";
	private boolean mSubtitleVisible;
	private ArrayList<Crime> mCrimes;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.d(TAG, "OnCreate() called...");
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.crimes_title);
		
		// Getting access to the Model in MVC
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		
		
		CrimeAdapter crimeAdapter = new CrimeAdapter(mCrimes);
		
		setListAdapter(crimeAdapter);
		
		setRetainInstance(true);
		mSubtitleVisible = false;
		
	}
	
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
		//Log.d(TAG, c.getTitle() + " was clicked");
		
		//Intent intent = new Intent(getActivity(), CrimeActivity.class);
		
		Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP 
		//		| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		
		startActivity(intent);
		
	}
	
	private class CrimeAdapter extends ArrayAdapter<Crime> {
		
		public CrimeAdapter(ArrayList<Crime> crimes) {
		
			// List ID is passed in as 0 as we are passing a custom layout
			// and not using a predefined layout
			super(getActivity(), 0, crimes);
		
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_crime, null);
			}
			
			// Use model object to configure the View
			Crime crime = getItem(position);
			
			TextView titleTextView = (TextView)convertView
					.findViewById(R.id.list_item_crime_titleTextView);
			titleTextView.setText(crime.getTitle());
			
			TextView dateTextView = (TextView)convertView
					.findViewById(R.id.list_item_crime_dateTextView);
			dateTextView.setText(crime.getDate().toString());
			
			CheckBox solvedCheckBox = (CheckBox)convertView
					.findViewById(R.id.list_item_crime_solvedCheckBox);
			solvedCheckBox.setChecked(crime.isSolved());
			
			return convertView;
		}
		
		
		
	}

	@Override
	public void onPause() {
		
		super.onPause();
		Log.d(TAG, "OnPause() called...");

	}



	@Override
	public void onResume() {
		
		super.onResume();
		Log.d(TAG, "OnResume() called...");
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();

	}



	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if(mSubtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
		
	}



	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			
			case R.id.menu_item_new_crime:
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent i = new Intent(getActivity(), CrimePagerActivity.class);
				i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
				startActivityForResult(i, 0);
				return true;
			case R.id.menu_item_show_subtitle:
				if(getActivity().getActionBar().getSubtitle() == null) {
					getActivity().getActionBar().setSubtitle(R.string.subtitle);
					item.setTitle(R.string.hide_subtitle);
					mSubtitleVisible = true;
				} else {
					getActivity().getActionBar().setSubtitle(null);
					item.setTitle(R.string.show_subtitle);
					mSubtitleVisible = false;
				}
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		
		}
		
	} //end of onOptionsItemSelected



	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		FrameLayout view = (FrameLayout)inflater.inflate(
						R.layout.list_crime_fragment_new, parent, 
						false);
						
		if(view.findViewById(android.R.id.empty) != null) {
			
			Log.d(TAG, "Currently in empty view");
			
			
			Button btnNewCrime = (Button)view.findViewById(R.id.new_crime_btn); 
			btnNewCrime.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					Crime crime = new Crime();
					CrimeLab.get(getActivity()).addCrime(crime);
					Intent i = new Intent(getActivity(), CrimePagerActivity.class);
					i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
					startActivityForResult(i, 0);
					
				}
			});
			
		}
		
		
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		
		ListView listView = (ListView)view.findViewById(android.R.id.list);
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			
			registerForContextMenu(listView);
			
		} else {
			
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			
		}
		
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				// Not used in the implementation
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				// Not used in the implementation
				
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.crime_list_item_context, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, 
					MenuItem menuItem) {
				
				switch(menuItem.getItemId()) {
				
				case R.id.menu_item_delete_crime:
					CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
					CrimeLab crimeLab = CrimeLab.get(getActivity());
					
					for( int i = adapter.getCount() -1; i >= 0; i--) {
						
						if(getListView().isItemChecked(i)) {
							
							crimeLab.deleteCrime(adapter.getItem(i));
						}
					}
					
					mode.finish();
					adapter.notifyDataSetChanged();
					return true;
					
				default:
					return false;
				
				} // End - switch statement
				
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				
				// Not used in this implementation
				
			}
		});
		
		return view;
	}



	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, 
				menu);
		
	}



	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item
				.getMenuInfo();
		int position = info.position;
		
		CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
		Crime crime = adapter.getItem(position);
		
		switch(item.getItemId()) {
		
		case R.id.menu_item_delete_crime:
			CrimeLab.get(getActivity()).deleteCrime(crime);
			adapter.notifyDataSetChanged();
			return true;
		
		}
		
		return super.onContextItemSelected(item);
	}
	
	
	
	
}
