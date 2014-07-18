package com.ihome.serv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.ihome.act.module.InCommingAct;
import com.ihome.serv.LoginManager.LoginCallback;
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

	static {
		System.loadLibrary("razem");
	}

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

	@SuppressLint("NewApi")
	@Override
	public IBinder onBind(Intent intent) {

		final String act_name = intent.getExtras().getString("act-name",
				"default");

		printf("onBind - %s", act_name);

		return mServManager;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onUnbind(Intent intent) {
		final String act_name = intent.getExtras().getString("act-name",
				"default");
		printf("onUnbind - %s", act_name);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		final String act_name = intent.getExtras().getString("act-name",
				"default");

		printf("onRebind - %s", act_name);
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
	public void onLoginSuccess(LoginInfor infor) {

		// notify
		Bundle bundle = new Bundle();

		bundle.putParcelable(RazemIntent.BUNDLE_LOGIN_TAG_INFOR, infor);

		sendBroadcast(RazemIntent.ACTION_LOGIN_STATE_CHANGED, bundle);
	}

	@Override
	public boolean onLoginFailed(int login_ret) {
		// notify

		Bundle bundle = new Bundle();

		sendBroadcast(RazemIntent.ACTION_LOGIN_STATE_CHANGED, bundle);

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
	public void onMemberAcquired(int account, String nick, String title,
			int call_status, byte[] icon) {
		// printf("onMemberAcquired { %d , %s , %s , %d , %d } ", account, nick,
		// title, call_status, icon.length);

		Bundle bundle = new Bundle();

		MemberInfor infor = new MemberInfor(account, nick, title, call_status,
				icon);

		bundle.putParcelable(RazemIntent.BUNDLE_MEMBER_INFOR, infor);

		sendBroadcast(RazemIntent.ACTION_MEMBER_ACQUIRE, bundle);
	}

	@Override
	public void onMemberOnlineStateChanged(int account_id, int online_offline) {
		// printf("onMemberOnlineStateChanged { %d , %d }", account_id,
		// online_offline);

		Bundle bundle = new Bundle();

		bundle.putInt(RazemIntent.BUNDLE_MEMBER_ONLINE_ACCOUNT, account_id);
		bundle.putInt(RazemIntent.BUNDLE_MEMBER_ONLINE_STATE, online_offline);

		sendBroadcast(RazemIntent.ACTION_MEMBER_STATE_CHANGED, bundle);

	}

	@Override
	public void onMemberChanged(int account_id, int changed_bits) {
		// printf("onMemberChanged { %d , %d }", account_id, changed_bits);
		SVConnect.getMemberInfo(account_id, changed_bits);
	}

	@Override
	public void onCallIncomming(int targetid) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt(RazemIntent.BUNDLE_CALL_INCOMMING_TAG_TARGET, targetid);
		intent.putExtras(bundle);
		intent.setClass(getApplicationContext(), InCommingAct.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);
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
			return mLoginManager.getLoginState();
		}

		@Override
		public LoginInfor rzGetLoginInfor() throws RemoteException {
			return mLoginManager.getLoginInfor();
		}

		@Override
		public void rzLogout() throws RemoteException {
			super.rzLogout();
			mLoginManager.logout();
		}
	}
}
