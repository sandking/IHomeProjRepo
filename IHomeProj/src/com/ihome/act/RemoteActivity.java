package com.ihome.act;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Toast;

import com.ihome.serv.LoginInfor;
import com.ihome.serv.RazemIntent;
import com.ihome.serv.RemoteServ;
import com.ihome.serv.LoginManager.LoginResult;

public abstract class RemoteActivity extends BaseActivity implements
		ServiceConnection {

	public final static String BUNDLE_LOGIN_INFOR_ACCOUNT = "bundle_account";

	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bind();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbind();
	}

	public void bind() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), RemoteServ.class);
		Bundle bundle = new Bundle();
		final String smpName = getClass().getSimpleName();
		bundle.putString("act-name", smpName);
		intent.putExtras(bundle);
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	public void unbind() {
		unbindService(this);
	}

	public void register(String... actions) {

		if (mReceiver != null)
			return;

		mReceiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		for (String action : actions)
			filter.addAction(action);
		registerReceiver(mReceiver, filter);
	}

	public void unregister() {
		if (mReceiver == null)
			return;

		unregisterReceiver(mReceiver);
		mReceiver = null;
	}

	protected void handleRemoteException(RemoteException e) {
		e.printStackTrace();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(RemoteActivity.this,
						"Remote Communication Exception !!", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	protected void onLoginRet(LoginInfor infor, LoginResult ret) {

	}

	protected void onCallSuccess(String ip, int port, String key, int link_dir,
			int udp_socket) {

	}

	protected void onCallFailed(int state) {

	}

	private final class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			final String action = arg1.getAction();
			final Bundle extra = arg1.getExtras();

			if (action.equals(RazemIntent.ACTION_LOGIN_STATE_CHANGED)) {
				final int login_ret = extra
						.getInt(RazemIntent.BUNDLE_LOGIN_TAG_RESULT);

				final LoginResult result = LoginResult.values()[login_ret];
				LoginInfor infor = null;
				if (result == LoginResult._RAZEM_LOGIN_RESULT_SUCCESS)
					infor = extra
							.getParcelable(RazemIntent.BUNDLE_LOGIN_TAG_INFOR);
				onLoginRet(infor, result);

			} else if (action.equals(RazemIntent.ACTION_CALL_INCOMMING)) {

			} else if (action.equals(RazemIntent.ACTION_CALL_SUCCESS)) {

				final String ip = extra
						.getString(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_IP);
				final int port = extra
						.getInt(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_PORT);
				final String key = extra
						.getString(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_KEY);
				final int link_dir = extra
						.getInt(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_LINK_DIR);
				final int udp_socket = extra
						.getInt(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_UDP_SOCKET);

				onCallSuccess(ip, port, key, link_dir, udp_socket);
			} else if (action.equals(RazemIntent.ACTION_CALL_FAILED)) {

				final int state = extra
						.getInt(RazemIntent.BUNDLE_CALL_FAILED_TAG_STATE);

				onCallFailed(state);
			}
		}
	}
}
