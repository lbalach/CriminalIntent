package com.lacreatelit.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
	
	private static final String TAG = "CrimeCameraFragment";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private View mCameraProgressContainer;
	
	private Camera.ShutterCallback mShutterCallback = 
			
			new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			
			mCameraProgressContainer.setVisibility(View.VISIBLE);
			
		}
	};
	
	private Camera.PictureCallback mPictureCallback = 
			
			new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			// Create a filename
			String fileName = UUID.randomUUID().toString() + ".jpg";
			
			// Save the jpeg data to disk
			if(writeToFile(fileName, data)) {
				Log.i(TAG, "JPEG saved at " + fileName);
			}
			
			// Destroy the activity - So no need to set the visibility of the 
			// spinner to false again.
			getActivity().finish();
		}
	};

	@Override
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_crime_camera, parent,
				false);
		
		//==========Setup the Click button======================================
		
		Button btnTakePicture = (Button)view.findViewById(
				R.id.btn_take_picture);
		btnTakePicture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				if(mCamera != null) {
					
					mCamera.takePicture(mShutterCallback, null, mPictureCallback);
					
				}
				
			}
		});
		
		//==========Setup Surface view for the camera===========================
		
		mSurfaceView = (SurfaceView)view
				.findViewById(R.id.surfaceView_crime_camera);
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		// setType and SURFACE_TYPE_PUSH_BUFFERS are deprecated methods
		// but are required for Camera preview to work on pre-3.0 devices
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				
				// Tell the camera to use this surface to set up its preview area 
				try {
					if(mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException exception) {
					Log.e(TAG, "Error setting up preview display", exception);
				}
				
			}
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				
				// Surface no longer available hence stop the preview 
				if(mCamera != null) {
					mCamera.stopPreview();
				}
				
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				
				if(mCamera == null) return;
				
				// The surface has changed size; update the camera preview parameters
				Camera.Parameters parameters = mCamera.getParameters();
				
				// Get best supported size for preview
				Size s = getBestSupportedSize(
						parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(s.width, s.height);
				
				// Get best supported size for picture
				s = getBestSupportedSize(
						parameters.getSupportedPictureSizes(), width, height);
				parameters.setPictureSize(s.width, s.height);
				
				mCamera.setParameters(parameters);
				
				try{
					
					mCamera.startPreview();
					
				} catch (Exception exception) {
					
					Log.e(TAG, "Could not start preview", exception);
					mCamera.release();
					mCamera = null;
					
				}
				
			}
		});
		
		//==========Setup the Progress bar======================================
		mCameraProgressContainer = view
				.findViewById(R.id.crime_camera_progressContainer);
		mCameraProgressContainer.setVisibility(View.INVISIBLE);
		
		return view;
	}

	@TargetApi(9)
	@Override
	public void onResume() {
		
		super.onResume();
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
		
	}

	@Override
	public void onPause() {
		
		super.onPause();
		if(mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	/** An algorithm to get the largest size available. CameraPreview.java
	 * in the sample app has more details 
	 */
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		
		for(Size size: sizes) {
			
			int area = size.width * size.height;
			if(area > largestArea) {
				bestSize = size;
				largestArea = area;
			}
			
		}
		
		return bestSize;
		
	}
	
	private boolean writeToFile(String fileName, byte[] dataToWrite) {
		
		FileOutputStream os = null;
		boolean success = true;
		
		try {
			
			os = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
			os.write(dataToWrite);			
		} catch (Exception e) {
			
			Log.e(TAG, "Error writing to file " + fileName, e);
			success = false;
			
		} finally {
			
			try {
				if(os != null)
					os.close();
			} catch (Exception e) {
				Log.e(TAG, "Error closing file " + fileName, e);
				success = false;
			}
			
		}
		
		return success;
	}

}
