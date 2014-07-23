package com.ihome.serv;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ihome.R;
import com.tpad.ihome.inter.SVConnect;

/**
 * Created by sk on 14-7-2.
 */
public abstract class IService extends Service {

	public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

	private boolean _dbg_;

	private NetStateChangedListener net_changed_listener;
	private ConnectivityManager mConnectivityManager;
 
	protected abstract void onNetChanged(NetworkInfo netWorkInfor);

	@Override
	public void onCreate() {
		super.onCreate();

		_dbg_ = getResources().getBoolean(R.bool.ser_debug);
		
		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    
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

				onNetChanged(network_infor);
			}
		}
	}

	protected class ServManager extends IServManager.Stub {

		@Override
		public void rzLogin(String addr, int account, String passwd)
				throws RemoteException {
		}

		@Override
		public int rzGetLoginState() throws RemoteException {
			return 0;
		}

		@Override
		public LoginInfor rzGetLoginInfor() throws RemoteException {
			return null;
		}

		@Override
		public void rzLogout() throws RemoteException {
			SVConnect.logout();
		}

		@Override
		public void rzSetInfor(int set_bits, String id, String title,
				int call_status, byte[] icon_buf) throws RemoteException {
			SVConnect.setInfomation(set_bits, id, title, call_status, icon_buf);
		}

		@Override
		public void rzGetMemberList() throws RemoteException {
			SVConnect.getMemberList();
		}

		@Override
		public void rzGetMemberInfo(int account, int update_bits)
				throws RemoteException {
			SVConnect.getMemberInfo(account, update_bits);
		}

		@Override
		public void rzCall(int targetid) throws RemoteException {
			SVConnect.call(targetid);
		}

		@Override
		public void rzAnswer() throws RemoteException {
			SVConnect.answer();
		}

		@Override
		public void rzReject() throws RemoteException {
			SVConnect.reject();
		}
	}
}
