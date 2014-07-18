package com.ihome.act.view;

import android.content.Context;
import android.graphics.ImageFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CaptureView extends SurfaceView implements Callback {

	private VideoCapture video_capture;
	private final static int PRE_BMP_WIDTH = 640;
	private final static int PRE_BMP_HEIGHT = 480;

	private byte[] video_capture_buf;

	@SuppressWarnings("deprecation")
	public CaptureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final SurfaceHolder mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(getClass().getSimpleName(), "surfaceCreated!!!");
		capture_create();
		capture_start(holder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(getClass().getSimpleName(), "surfaceDestroyed!!!");
		capture_stop();
		capture_release();
	}

	public void setCaptureBuf(byte[] buf) {
		this.video_capture_buf = buf;
	}

	public void capture() {
		this.video_capture.capture(this.video_capture_buf);
	}

	private void capture_create() {
		if (video_capture == null) {
			video_capture = VideoCapture.create();
			video_capture.setCaptureConfig(PRE_BMP_WIDTH, PRE_BMP_HEIGHT,
					ImageFormat.NV21);
		}
	}

	private void capture_start(SurfaceHolder holder) {
		if (video_capture != null) {
			video_capture.startCapture(holder, video_capture_buf);
		}
	}

	private void capture_stop() {
		if (video_capture != null) {
			video_capture.stopCapture();
		}
	}

	private void capture_release() {
		if (video_capture != null) {
			video_capture.release();
			video_capture = null;
		}
	}

}
