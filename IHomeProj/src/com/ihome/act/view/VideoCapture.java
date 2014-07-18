package com.ihome.act.view;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoCapture implements PreviewCallback {
	final static String TAG = "CaptureFrame";

	final static int FLAG_CAMERA_FACING_BACK = 0;
	final static int FLAG_CAMERA_FACING_FRONT = 1;

	private int capture_width;
	private int capture_height;
	private int capture_format;

	private Camera mCamera;
	private Parameters mParameters;

	private ReentrantLock lock_capture = new ReentrantLock();

	private Condition condition_capture = lock_capture.newCondition();

	/**
	 * Open the specified camera
	 * 
	 * @param flag
	 *            0 - back , 1 - front.
	 * @return the camera.
	 */
	@SuppressLint("NewApi")
	private Camera openCamera(int flag) {
		int numberOfCameras = Camera.getNumberOfCameras();
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == flag)
				return Camera.open(i);
		}
		return null;
	}

	/**
	 * Initialize the camera.
	 * 
	 * @return if 0 - initialize successfully.
	 */
	private int initCamera() {
		if (mCamera != null)
			return -2;

		do {
			mCamera = openCamera(FLAG_CAMERA_FACING_FRONT);

			if (mCamera != null)
				break;

			mCamera = openCamera(FLAG_CAMERA_FACING_BACK);

			if (mCamera != null)
				break;

			return -1;

		} while (false);

		return 0;
	}

	private VideoCapture() {
		if (initCamera() == 0) {
			mParameters = mCamera.getParameters();
		}
	}

	public static VideoCapture create() {
		VideoCapture captureFrame = new VideoCapture();

		return captureFrame.mCamera == null ? null : captureFrame;
	}

	/**
	 * Set the configuration for capture-frame.
	 * 
	 * @param width
	 *            width of frame.
	 * @param height
	 *            height of frame.
	 * @param pixelFMT
	 *            format of frame.
	 * @return buf for capturing.
	 */
	public void setCaptureConfig(int width, int height, int pixelFMT) {
		capture_width = width;
		capture_height = height;
		capture_format = pixelFMT;

		Size capture_size = mCamera.new Size(width, height);

		List<Integer> supportedFormats = mParameters
				.getSupportedPreviewFormats();

		if (!supportedFormats.contains(pixelFMT)) {
			capture_format = ImageFormat.NV21;
			Log.w(TAG,
					String.format("setCaptureConfig -> { pixelFormat is not supported!!!  Changed to ImageFormat.NV21 }"));
		}

		List<Size> supportedSize = mParameters.getSupportedPreviewSizes();

		if (!supportedSize.contains(capture_size)) {
			capture_width = supportedSize.get(0).width;
			capture_width = supportedSize.get(0).height;

			Log.w(TAG,
					String.format(
							"setCaptureConfig -> { size is not supported!!!  Changed to size[%d,%d]}",
							capture_width, capture_height));
		}

		int size = (width * height * (capture_format == ImageFormat.RGB_565 ? 4
				: 3)) >> 1;

		Log.e(TAG, String.format(
				"Capture VARS [width[%d] height[%d] format[%d] size[%d]]",
				capture_width, capture_height, capture_format, size));
	}

	public void startCapture(SurfaceHolder holder, byte[] buf) {
		mParameters.setPreviewFormat(capture_format);
		mParameters.setPreviewSize(capture_width, capture_height);
		// mParameters.setRotation(90);
		mCamera.setParameters(mParameters);
		mCamera.setDisplayOrientation(90);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.setPreviewCallbackWithBuffer(this);
		mCamera.startPreview();

		mCamera.addCallbackBuffer(buf);
	}

	public void stopCapture() {
		if (mCamera == null)
			return;

		inotify();

		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();
	}

	public void release() {
		mCamera.release();
		mCamera = null;
	}

	public boolean capture(byte[] buff) {
		try {
			if (mCamera != null) {
				lock_capture.lock();
				mCamera.addCallbackBuffer(buff);
				condition_capture.await();
				lock_capture.unlock();
			}
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		inotify();
	}

	public void inotify() {
		lock_capture.lock();
		condition_capture.signal();
		lock_capture.unlock();
	}
}