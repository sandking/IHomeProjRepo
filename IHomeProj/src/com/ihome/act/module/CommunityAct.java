package com.ihome.act.module;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ihome.act.BaseActivity;

/**
 * Created by sk on 14-7-2.
 */
public class CommunityAct extends BaseActivity {

	final static int FLAG_KEEP_NO_KEYGUARD = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

	final static int FLAG_KEEP_SCREEN_ON = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
			| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Window win = getWindow();
		win.addFlags(FLAG_KEEP_NO_KEYGUARD);
		win.addFlags(FLAG_KEEP_SCREEN_ON);

		
		
	}

}
