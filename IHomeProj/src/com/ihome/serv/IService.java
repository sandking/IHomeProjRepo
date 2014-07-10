package com.ihome.serv;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.ihome.R;

/**
 * Created by sk on 14-7-2.
 */
public abstract class IService extends Service {

	public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

	private boolean _dbg_;

	private NetStateChangedListener net_changed_listener;
	private ConnectivityManager mConnectivityManager;

	@Override
	public void onCreate() {
		super.onCreate();
		_dbg_ = getResources().getBoolean(R.bool.ser_debug);

		foreground();
		register_netlistener();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregister_netlistener();
	}

	@SuppressWarnings("deprecation")
	private void foreground() {
		Notification note = new Notification(0, null,
				System.currentTimeMillis());
		note.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(42, note);
	}

	private void register_netlistener() {
		if (net_changed_listener == null) {
			net_changed_listener = new NetStateChangedListener();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACTION_CONNECTIVITY_CHANGE);
			filter.setPriority(Integer.MAX_VALUE);
			registerReceiver(net_changed_listener, filter);
		}
	}

	private void unregister_netlistener() {
		if (net_changed_listener != null) {
			unregisterReceiver(net_changed_listener);
			net_changed_listener = null;
		}
	}

	protected abstract boolean onNetConnected(NetworkInfo infor);

	protected abstract boolean onNetDisconnected();

	protected void printf(String msg, Object... args) {
		if (_dbg_)
			Log.e(getClass().getSimpleName(), String.format(msg, args));
	}

	private final class NetStateChangedListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (TextUtils.equals(action, ACTION_CONNECTIVITY_CHANGE)) {
				NetworkInfo network_infor = mConnectivityManager
						.getActiveNetworkInfo();

				if (network_infor == null ? onNetDisconnected()
						: onNetConnected(network_infor))
					return;
			}
		}
	}

}
