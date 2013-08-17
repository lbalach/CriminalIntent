package com.lacreatelit.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CriminalIntentJSONSerializer {
	
	
	private static final String TAG = "CriminalIntentJSONSerializer";
	
	private Context mContext;
	private String mFileName;
	
	public CriminalIntentJSONSerializer(Context context, String fileName) {
		
		mContext = context;
		mFileName = fileName;
		
	}
	
	
//	public void saveCrimes(ArrayList<Crime> crimes) 
//			throws JSONException, IOException {
//		
//		saveCrimes(crimes, false);
//	}
	
	public void saveCrimes(ArrayList<Crime> crimes, 
			boolean isWriteExternalStorage) 
			throws JSONException, IOException {
		
		//Put the passed in list into a JSON Array
		JSONArray jsonArray = new JSONArray();
		for (Crime c : crimes) {
			jsonArray.put(c.toJSON());
		}
		
		if (!isWriteExternalStorage) {
			
			writeToInternalStorage(jsonArray);
			Log.d(TAG, "Writing to internal storage");
		
		} else {
			
			writeToExternalStorage(jsonArray);
			Log.d(TAG, "Writing to external storage");
		}
		
	} // End of saveCrimes()
	
	private void writeToInternalStorage(JSONArray jsonArray) 
			throws IOException {
		
		Writer writer = null;
		try {
			
			OutputStream out = mContext.openFileOutput(mFileName, 
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(jsonArray.toString());
			
		} finally {
			
			if (writer != null)
				writer.close();
			
		}
		
	}
	
	
	private void writeToExternalStorage(JSONArray jsonArray) 
			throws IOException {
		
		OutputStreamWriter writer = null;
		FileOutputStream fileOutStream = null;
		File file = null;
		try {
			if(android.os.Environment.getExternalStorageState()
					.equals(Environment.MEDIA_MOUNTED)) {
				Log.d(TAG, "External media is found");
				
				file = new File(mContext.getExternalFilesDir(null), 
						mFileName);
				
				fileOutStream = new FileOutputStream(file);
				writer = new OutputStreamWriter(fileOutStream);
				writer.write(jsonArray.toString());
				Log.d(TAG, "Data written to external file");
				
				//String baseDir = mContext.getExternalFilesDir(null).toString();
				//Log.d(TAG, baseDir);
				
			} else {
				
				Log.d(TAG, "External media is not found");
			}
			
		} finally {
			
			if(writer != null)
				writer.close();
			
			if(fileOutStream != null) {
			
				fileOutStream.flush();
				fileOutStream.close();
				
			}
		}
	}
	
	
	public ArrayList<Crime> loadCrimes( boolean isExternalStorage) 
			throws IOException, JSONException {
		
		ArrayList<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		InputStream inputStream = null;
		
		try {
			
			
			if (!isExternalStorage) {
				
				inputStream = mContext.openFileInput(mFileName);
				
			}
			else {
				
				inputStream = getExternalStorageStream();
				
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));
			
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			
			while((line = reader.readLine()) != null) {
			
				jsonString.append(line);
				
			}
			
			JSONArray array = (JSONArray)new JSONTokener(jsonString.toString())
									.nextValue();
			
			for( int i = 0; i < array.length(); i++) {
				
				crimes.add(new Crime(array.getJSONObject(i)));
				
			}
			
		} catch (FileNotFoundException e) {
			// This exception is ignored because the first time this will always
			// be encountered
			
		} catch (JSONException e) {
			
			Log.d(TAG, "JSONException caught",e);
			throw e;
			
		}
		finally {
			
			if(reader != null)
				reader.close();
			
		}
		
	
		return crimes;
		
	}
	
	private InputStream getExternalStorageStream() throws IOException {

		File file =  null;
		FileInputStream fileInStream = null;

		if(android.os.Environment.getExternalStorageState()
			.equals(Environment.MEDIA_MOUNTED)) {
			
			file = new File(mContext.getExternalFilesDir(null), 
					mFileName);
			
			fileInStream = new FileInputStream(file);
			return fileInStream;
			
			//String baseDir = mContext.getExternalFilesDir(null).toString();
			//Log.d(TAG, baseDir);
		
		} else {

			Log.d(TAG, "External media is not found");
				
		}
			
		return null;
		
	}
	
	

}
