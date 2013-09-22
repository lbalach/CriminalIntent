package com.lacreatelit.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	
	// JSON keys used by the model class
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_PHOTO = "photo";
	
	// Data used in the model class
	private UUID mId;
	private String mTitle;
	private Date mDate = new Date();
	private boolean mSolved;
	private Photo mPhoto;
	
	
	public Crime() {
		
		mId = UUID.randomUUID();
		//mDate = new Date();
	}
	
	public Crime(JSONObject jsonObject) throws JSONException {
		
		mId = UUID.fromString(jsonObject.getString(JSON_ID));
		mTitle = jsonObject.getString(JSON_TITLE);
		mSolved = jsonObject.getBoolean(JSON_SOLVED);
		mDate = new Date(jsonObject.getLong(JSON_DATE));
		
		if(jsonObject.has(JSON_PHOTO)) {
			mPhoto = new Photo(jsonObject.getJSONObject(JSON_PHOTO));
		}
		
	}

	public UUID getId() {
		return mId;
	}


	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	@Override
	public String toString() {
		
		return mTitle;
	}
	
	
	public JSONObject toJSON() throws JSONException {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(JSON_ID, mId.toString());
		jsonObject.put(JSON_TITLE, mTitle);
		jsonObject.put(JSON_SOLVED, mSolved);
		jsonObject.put(JSON_DATE, mDate.getTime());
		
		if(mPhoto != null) {
			jsonObject.put(JSON_PHOTO, mPhoto.toJSON());
		}
		
		return jsonObject;
		
	}

	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}
	
	
	
}
