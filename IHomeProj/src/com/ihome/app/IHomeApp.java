package com.ihome.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.ihome.R;
import com.ihome.act.BaseActivity;

import java.util.*;

/**
 * Created by sk on 14-6-27.
 */
public class IHomeApp extends Application implements CrashListener {

	private final HashMap<String, BaseActivity> ihome_acts = new HashMap<String, BaseActivity>();

	private CrashHandler mCrashHandler;

	// FLAG : debug .
	private boolean _dbg_;

	// FLAG : catch debug info .
	private boolean _catch_;

	@Override
	public void onCreate() {

		printf(">>>>>>>>>>>> Application onCreate <<<<<<<<<<<<<<");

		super.onCreate();

		_dbg_ = getResources().getBoolean(R.bool.app_debug);
		_catch_ = getResources().getBoolean(R.bool.app_catch_info);

		if (_catch_) {
			mCrashHandler = CrashHandler.getInstance();
			mCrashHandler.init(this);
			mCrashHandler.setCrashListener(this);
		}
	}

	protected void printf(String msg, Object... args) {
		if (_dbg_)
			Log.e(IHomeApp.class.getSimpleName(), String.format(msg, args));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCatch(Throwable ex) {
		printf(">>>>> onCatch : %s { ex : %s }", Thread.currentThread()
				.getName(), ex.getMessage());

		new Thread() {
			@Override
			public void run() {
				super.run();
				Looper.prepare();
				Toast.makeText(IHomeApp.this,
						String.format("Unknow Exception - Will auto exit !!"),
						Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();

		return true;
	}

	@Override
	public void onCommit(boolean delete) {
		if (delete)
			mCrashHandler.deleteCrashFiles();

		forceClose();
	}

	public void join(String name, BaseActivity activity) {
		ihome_acts.put(name, activity);
		printf(">>>>>>>>>>>> %s has join !!! <<<<<<<<<<<<<<", name);
	}

	public void exit(String name) {
		ihome_acts.remove(name);
		printf(">>>>>>>>>>>> %s has exit !!! <<<<<<<<<<<<<<", name);
	}

	protected void forceClose() {
		printf("force close !!!");

		Iterator<BaseActivity> acts_iterator = ihome_acts.values().iterator();

		final int acts_count = ihome_acts.size();

		int act_index = 0;

		while (acts_iterator.hasNext()) {
			acts_iterator.next().forceClose(act_index == (acts_count - 1));

			act_index++;
		}

		android.os.Process.killProcess(android.os.Process.myPid());
		ActivityManager activityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		activityMgr.killBackgroundProcesses(getPackageName());
		System.exit(0);
	}
}
