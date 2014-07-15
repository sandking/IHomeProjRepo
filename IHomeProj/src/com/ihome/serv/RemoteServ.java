package com.ihome.serv;

import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.ihome.serv.LoginManager.LoginCallback;
import com.ihome.serv.LoginManager.LoginInfor;
import com.ihome.serv.LoginManager.LoginResult;
import com.tpad.ihome.inter.RZCallStateListener;
import com.tpad.ihome.inter.RZEventCallback;
import com.tpad.ihome.inter.RZInitCompleteListener;
import com.tpad.ihome.inter.RZMemberChangedListener;
import com.tpad.ihome.inter.RZ_EVENT;
import com.tpad.ihome.inter.SVConnect;

/**
 * Created by sk on 14-6-28.
 */
public class RemoteServ extends IService implements RZCallStateListener,
		RZInitCompleteListener, RZMemberChangedListener, RZEventCallback,
		LoginCallback {

	private RemoteServManager mServManager;
	private LoginManager mLoginManager;

	private final Runnable init_runn = new Runnable() {
		@Override
		public void run() {
			SVConnect.init(RemoteServ.this);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		mServManager = new RemoteServManager();

		mLoginManager = new LoginManager(this);
		mLoginManager.exec();

		new Thread(init_runn, "SVConnect-Thread").start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mServManager;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLoginManager.quit();
		mServManager = null;
	}

	@Override
	protected void onNetChanged(NetworkInfo netWorkInfor) {
		mLoginManager.setNetWork(netWorkInfor);
	}

	@Override
	public void onInitCompleted() {
		SVConnect.setCacheRootPath(getCacheDir().getAbsolutePath());
		SVConnect.setCallStateListener(this);
		SVConnect.setMemberChangedListener(this);
		SVConnect.setEventCallback(this);
	}

	@Override
	public void onLoginSuccess() {

		// notify
		sendBroadcast(RazemIntent.ACTION_LOGIN_SUCCESS, new Bundle());
	}

	@Override
	public boolean onLoginFailed(int login_ret) {
		// notify

		Bundle bundle = new Bundle();

		bundle.putInt(RazemIntent.BUNDLE_LOGIN_TAG_RESULT, login_ret);

		sendBroadcast(RazemIntent.ACTION_LOGIN_SUCCESS, bundle);

		switch (LoginResult.values()[login_ret]) {
		case _RAZEM_LOGIN_RESULT_ACCOUNT_BLOCKED:
			return false;
		case _RAZEM_LOGIN_RESULT_KICKED_OUT:
			return false;
		case _RAZEM_LOGIN_RESULT_ACCOUNT_INVALID:
			return false;
		default:
			return true;
		}
	}

	@Override
	public void onReceiveEvent(int event) {

		RZ_EVENT rz_event = RZ_EVENT.values()[event];

		switch (rz_event) {
		case SERVER_SHUTDOWN:
			mLoginManager.shutdown();
			break;
		default:
			printf("onReceiveEvent - %d", event);
			break;
		}
	}

	@Override
	public void onMemberAcquired(int accountid, String id, String title,
			int callstatus, byte[] icon) {

	}

	@Override
	public void onMemberOnlineStateChanged(int account_id, int online_offline) {

	}

	@Override
	public void onMemberChanged(int account_id, int changed_bits) {
		SVConnect.getMemberInfo(account_id, changed_bits);
	}

	@Override
	public void onCallIncomming(int targetid) {

		Bundle bundle = new Bundle();

		bundle.putInt(RazemIntent.BUNDLE_CALL_INCOMMING_TAG_TARGET, targetid);

		sendBroadcast(RazemIntent.ACTION_CALL_INCOMMING, bundle);
	}

	@Override
	public void onCallSuccess(String ip, int port, String key, int link_dir,
			int udp_socket) {
		Bundle bundle = new Bundle();

		bundle.putString(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_IP, ip);
		bundle.putInt(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_PORT, port);
		bundle.putString(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_KEY, key);
		bundle.putInt(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_LINK_DIR, link_dir);
		bundle.putInt(RazemIntent.BUNDLE_CALL_SUCCESS_TAG_UDP_SOCKET,
				udp_socket);

		sendBroadcast(RazemIntent.ACTION_CALL_SUCCESS, bundle);
	}

	@Override
	public void onCallFailed(int state) {
		Bundle bundle = new Bundle();

		bundle.putInt(RazemIntent.BUNDLE_CALL_FAILED_TAG_STATE, state);

		sendBroadcast(RazemIntent.ACTION_CALL_FAILED, bundle);
	}

	void sendBroadcast(final String action, final Bundle bundle) {

		final Intent intent = new Intent(action);
		intent.putExtras(bundle);

		sendOrderedBroadcast(intent, null);
	}

	protected final class RemoteServManager extends ServManager {

		@Override
		public void rzLogin(String addr, int account, String passwd)
				throws RemoteException {
			super.rzLogin(addr, account, passwd);

			mLoginManager.login(new LoginInfor(addr, account, passwd));
		}

		@Override
		public int rzGetLoginState() throws RemoteException {

			return super.rzGetLoginState();
		}

	}
}
