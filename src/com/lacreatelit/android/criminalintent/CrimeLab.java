package com.lacreatelit.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CrimeLab {
	
	private static final String TAG = "CimeLab";
	private static final String FILENAME = "crimes.json";
	
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	private int MAX_CRIMES = 10;
	
	private ArrayList<Crime> mCrimes;
	private CriminalIntentJSONSerializer mJsonSerializer;
	
	private boolean mIsExternalStorage = true;
	
	private CrimeLab(Context appContext) {
		
		mAppContext = appContext;
		mJsonSerializer = 
				new CriminalIntentJSONSerializer(mAppContext, FILENAME);
		
		try {
			
			mCrimes = mJsonSerializer.loadCrimes(mIsExternalStorage);
			
		} catch (Exception e) {
			
			mCrimes = new ArrayList<Crime>();
			Log.d(TAG, "Creating a new list as no existing file has been found");
		}
		
		//addSampleData();
		
	}

	private void addSampleData() {

		for( int i = 0; i < MAX_CRIMES; i++) {
			Crime crime = new Crime();
			crime.setTitle("Crime #" + i);
			crime.setSolved(i%2 == 0);
			mCrimes.add(crime);
		}
		
		
	}
	public static CrimeLab get(Context context) {
		
		if(sCrimeLab == null) {
			sCrimeLab = new CrimeLab(context.getApplicationContext());
		}
		
		
		return sCrimeLab;
		
	}
	
	public ArrayList<Crime> getCrimes() {
		
		return mCrimes;
	}
	
	public Crime getCrime(UUID id) {
		
		for(Crime crime : mCrimes) {
			if (crime.getId().equals(id))
				return crime;
		}
		return null;
	}
	
	void printCrimeListIDs() {
		
		Log.d(TAG, "Begin logging ID's");
		
		for(Crime crime : mCrimes) {
			Log.d(TAG, crime.getId().toString());
		}
		
		Log.d(TAG, "End logging ID's");
	}
	
	public void addCrime(Crime crime) {
		
		mCrimes.add(crime);
	}
	
	public void deleteCrime(Crime c) {
		
		mCrimes.remove(c);
	}
	
//	public ArrayList<Crime> loadCrimes() throws IOException, JSONException{
//		
//		
//		return loadCrimes(mIsExternalStorage);
//		
//	}
	
//	public ArrayList<Crime> loadCrimes( boolean isExternalStorage) 
//			throws IOException, JSONException {
//		
//		ArrayList<Crime> crimes = new ArrayList<Crime>();
//		BufferedReader reader = null;
//		InputStream inputStream = null;
//		
//		try {
//			// 1. Use the Context to get access to the file
//			if(!isExternalStorage) {
//				
//				inputStream = mAppContext.openFileInput(FILENAME);
//				Log.d(TAG, "Data read from internal storage");
//				
//			} else {
//				
//				inputStream = getExternalStorageStream();
//				Log.d(TAG, "Data read from External storage");
//				
//			}
//			
//			reader = new BufferedReader(new InputStreamReader(inputStream));
//			
//			// 2. Get the JSON string from the file
//			StringBuilder jsonString = new StringBuilder();
//			String line = null;
//			while((line = reader.readLine()) != null) {
//				jsonString.append(line);
//			}
//			
//			// 3. Tokenize the JSON string to get the JSON array
//			JSONArray jsonArray = (JSONArray)(new JSONTokener(
//					jsonString.toString()).nextValue());
//			
//			// 4. Process the JSON array to get the ArrayList of Crimes
//			for(int i = 0; i < jsonArray.length(); i++) {
//				crimes.add(new Crime(jsonArray.getJSONObject(i)));
//			}
//				
//		} catch (FileNotFoundException e ) {
//			// Ignore exception as it will happen the first time
//		} finally {
//			if (reader != null)
//				reader.close();
//		}
//		
//		return crimes;
//		
//	}
	
	public boolean saveCrimes() {
		
		try {
			
			mJsonSerializer.saveCrimes(mCrimes, mIsExternalStorage);
			Log.d(TAG, "Data saved to file(External/Internal)");
			return true;
		} catch(Exception e) {
			Log.e(TAG, "Error saving file :", e);
			return false;
		}
		
	}
	
//	private InputStream getExternalStorageStream() throws IOException {
//
//		File file =  null;
//		FileInputStream fileInStream = null;
//
//		if(android.os.Environment.getExternalStorageState()
//			.equals(Environment.MEDIA_MOUNTED)) {
//			Log.d(TAG, "External media is found");
//			
//			file = new File(mAppContext.getExternalFilesDir(null), 
//					FILENAME);
//			
//			fileInStream = new FileInputStream(file);
//			return fileInStream;
//			
//			//String baseDir = mContext.getExternalFilesDir(null).toString();
//			//Log.d(TAG, baseDir);
//		
//		} else {
//
//			Log.d(TAG, "External media is not found");
//				
//		}
//			
//		return null;
//		
//	}

}
