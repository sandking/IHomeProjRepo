package com.ihome.act;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.ihome.serv.IServManager;
import com.ihome.serv.LoginInfor;
import com.ihome.serv.MemberInfor;
import com.ihome.serv.RazemIntent;
import com.ihome.serv.RemoteServ;

public abstract class RemoteActivity extends BaseActivity implements
		ServiceConnection {

	public final static String BUNDLE_LOGIN_INFOR_ACCOUNT = "bundle_account";
	public final static String BUNDLE_TYPE_GOTO_COMMUNITY = "bundle_type";

	public final static int TYPE_INCOMMING = 0;
	public final static int TYPE_CALLOUT = 1;

	private BroadcastReceiver mReceiver;
	
	public final static boolean TEST_CALL_OUT = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bind();
	}

	@Override
	protected void onPause() {
		super.onPause();
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

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		try {
			// printf("onServiceConnected!!!!!!!!!");
			Log.e("RemoteServ", getClass().getSimpleName()
					+ "  : onServiceConnected!!!");
			onBindPrepared(IServManager.Stub.asInterface(service));
		} catch (RemoteException e) {
			handleRemoteException(e);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	}

	protected abstract void onBindPrepared(IServManager serv)
			throws RemoteException;

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

	protected void onLoginRet(LoginInfor infor) {

	}

	protected void onCallSuccess(String ip, int port, String key, int link_dir,
			int udp_socket) {

	}

	protected void onCallFailed(int state) {

	}

	protected void onMemberAcquired(MemberInfor infor) {
	}

	protected void onMemberOnlineStateChanged(int account, int online_state) {
	}

	private final class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			final String action = arg1.getAction();
			final Bundle extra = arg1.getExtras();

			if (action.equals(RazemIntent.ACTION_LOGIN_STATE_CHANGED)) {

				LoginInfor infor = extra
						.getParcelable(RazemIntent.BUNDLE_LOGIN_TAG_INFOR);
				onLoginRet(infor);

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
			} else if (action.equals(RazemIntent.ACTION_MEMBER_ACQUIRE)) {

				final MemberInfor infor = extra
						.getParcelable(RazemIntent.BUNDLE_MEMBER_INFOR);

				onMemberAcquired(infor);
			} else if (action.equals(RazemIntent.ACTION_MEMBER_STATE_CHANGED)) {
				final int account = extra
						.getInt(RazemIntent.BUNDLE_MEMBER_ONLINE_ACCOUNT);
				final int online_state = extra
						.getInt(RazemIntent.BUNDLE_MEMBER_ONLINE_STATE);

				onMemberOnlineStateChanged(account, online_state);
			}
		}
	}
}
