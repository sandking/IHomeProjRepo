package com.ihome.serv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sk on 14-6-28.
 */
public class IHomeServ extends Service
{
	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		return true;
	}

	@Override
	public void onRebind(Intent intent)
	{
		super.onRebind(intent);
	}
}
