package com.ihome.act;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ihome.R;
import com.ihome.app.IHomeApp;

/**
 * Created by sk on 14-6-27.
 */
public class BaseActivity extends Activity {
	private IHomeApp ihomeApp;

	private boolean _dbg_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		_dbg_ = getResources().getBoolean(R.bool.act_debug);
		printf("%s onCreate!!!", getClass().getSimpleName()); 
		
		super.onCreate(savedInstanceState);
		ihomeApp = (IHomeApp) getApplication();
		ihomeApp.join(this.getClass().getSimpleName(), this);
	}

	@Override
	protected void onDestroy() {
		printf("%s onDestroy!!!", getClass().getSimpleName());
		super.onDestroy();
		ihomeApp.exit(this.getClass().getSimpleName());
	}

	protected void printf(String msg, Object... args) {
		if (_dbg_)
			Log.e(getClass().getSimpleName(), String.format(msg, args));
	}

	public void forceClose(boolean force) {
		printf("force close ");

		finish();

		if (force)
			System.exit(0);
	}
}
