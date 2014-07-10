package com.tpadsz.ihome.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class CustomDialog extends Dialog {
	protected Context mContext;
	private Window window;
	private WindowManager.LayoutParams windowsParams;
	protected int mX, mY;

	public CustomDialog(Context context, int layout, int theme) {
		super(context, theme);
		mContext = context;
		window = getWindow();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		windowsParams = window.getAttributes();
		setContentView(layout);
	}

	public CustomDialog(Context context, View mView, int theme) {
		super(context, theme);
		mContext = context;
		window = getWindow();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		windowsParams = window.getAttributes();
		setContentView(mView);
	}

	public void setPosition(int x, int y) {
		windowsParams.gravity = Gravity.LEFT | Gravity.TOP;
		// windowsParams.verticalMargin = 50;
		windowsParams.x = x;
		windowsParams.y = y;
		mX = x;
		mY = y;
	}

	public void setWidth(int width) {
		windowsParams.width = width;
	}

	public void setHeight(int height) {
		windowsParams.height = height;
	}

	@Override
	public void show() {
		window.setAttributes(windowsParams);
		super.show();
	}

	public void showDialog() {
		window.setAttributes(windowsParams);
		show();
	}

	public void setWindowAnimation(int styleid) {
		window.setWindowAnimations(styleid);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
