package com.ihome.serv;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.tpad.ihome.inter.SVConnect;

public class LoginManager implements Callback {

	public final static int RESULT_RETRY = -1;
	public final static int RESULT_OK = 0;

	private final static int MSG_LOGIN_ACCOUNT = 0x010;

	private final static int MAX_RETRYS = 5;

	private RequestLoginThread mRequestLoginThread;
	private Handler mRequestLoginHandler;

	private LoginState state_login; // ��ǰ��¼״̬
	private LoginInfor retry_account; // �����˺�
	private boolean is_net_ok;

	private final LoginCallback mLoginCallback;

	LoginManager(LoginCallback callback) {
		mLoginCallback = callback;
		state_login = LoginState.OFFLINE;
		is_net_ok = true;
	}

	private void handleLoginSuccess(LoginInfor infor) {
		state_login = LoginState.ONLINE;
		// retry_account = new LoginInfor(infor.ip, infor.account, infor.pwd);
		retry_account = infor;

		mLoginCallback.onLoginSuccess(infor);
	}

	private void handleLoginFailed(LoginInfor infor, int ret) {
		state_login = LoginState.OFFLINE;

		if (mLoginCallback.onLoginFailed(ret))
			request_retry();
	}

	private int request_login(LoginInfor infor) {

		if (infor == null)
			return -1;

		state_login = LoginState.TRYING;

		return SVConnect.login(infor.ip, infor.account, infor.pwd);
	}

	private void request_retry() {

		if (retry_account == null)
			return;

		if (!is_net_ok)
			return;

		if (retry_account.retrys > MAX_RETRYS) {
			retry_account.retrys = 0;
			return;
		}

		retry_account.retrys++;

		final int delay_time = retry_account.getDelayRetry();

		Log.e(getClass().getSimpleName(),
				String.format("LoginRetry after %s ms!!!", delay_time));

		mRequestLoginHandler.sendMessageDelayed(mRequestLoginHandler
				.obtainMessage(MSG_LOGIN_ACCOUNT, retry_account), delay_time);
	}

	public void setNetWork(NetworkInfo infor) {
		synchronized (this) {
			is_net_ok = infor != null;

			if (retry_account == null)
				return;

			if (is_net_ok)
				request_retry(); 
			else
				retry_account.retrys = 0;
		}
	}

	public void logout() {
		state_login = LoginState.OFFLINE;
		retry_account = null;
	}

	public void shutdown() {
		state_login = LoginState.OFFLINE;
		retry_account = null;
	}

	public LoginInfor getLoginInfor() {
		return retry_account;
	}

	public int getLoginState() {
		return state_login.ordinal();
	}

	public void exec() {
		if (mRequestLoginThread == null)
			mRequestLoginThread = new RequestLoginThread("Login-Thread");

		mRequestLoginThread.start();
	}

	public void quit() {
		if (mRequestLoginThread != null)
			mRequestLoginThread.quit();
	}

	public int login(LoginInfor infor) {

		if (state_login == LoginState.TRYING)
			return RESULT_RETRY;

		if (mRequestLoginHandler.hasMessages(MSG_LOGIN_ACCOUNT))
			mRequestLoginHandler.removeMessages(MSG_LOGIN_ACCOUNT);

		mRequestLoginHandler.sendMessage(mRequestLoginHandler.obtainMessage(
				MSG_LOGIN_ACCOUNT, infor));

		return RESULT_OK;
	}

	@Override
	public boolean handleMessage(Message arg0) {
		final LoginInfor infor = (LoginInfor) arg0.obj;
		final int login_ret = request_login(infor);

		if (login_ret < 0)
			return false;

		if (login_ret >= LoginResult.values().length)
			return false;

		LoginResult event = LoginResult.values()[login_ret];

		switch (event) {
		case _RAZEM_LOGIN_RESULT_SUCCESS:
			handleLoginSuccess(infor);
			break;
		default:
			handleLoginFailed(infor, login_ret);
			break;
		}
		return true;
	}

	public enum LoginResult {
		_RAZEM_LOGIN_RESULT_SUCCESS("登录成功!"), //
		_RAZEM_LOGIN_RESULT_SOCKET_ERROR("网络连接错误"), //
		_RAZEM_LOGIN_RESULT_SERVER_NOT_ONLINE("网络连接错误"), //
		_RAZEM_LOGIN_RESULT_SOCKET_COMM_FAIL("网络连接错误"), //
		_RAZEM_LOGIN_RESULT_GIVEUP("网络连接错误"), //
		_RAZEM_LOGIN_RESULT_ACCOUNT_INVALID("账号或者密码无效"), //
		_RAZEM_LOGIN_RESULT_KICKED_OUT("账号已经登录"), //
		_RAZEM_LOGIN_RESULT_ACCOUNT_BLOCKED("账号被禁用");

		private final String desc;

		private LoginResult(String d) {
			desc = d;
		}

		public String getDesc() {
			return desc;
		}
	}

	public static interface LoginCallback {
		void onLoginSuccess(LoginInfor infor);

		boolean onLoginFailed(int login_ret);
	}

	public enum LoginState {
		OFFLINE, ONLINE, TRYING;
	}

	private final class RequestLoginThread extends HandlerThread {

		public RequestLoginThread(String name) {
			super(name);
		}

		@Override
		protected void onLooperPrepared() {
			super.onLooperPrepared();
			mRequestLoginHandler = new Handler(getLooper(), LoginManager.this);
		}
	}
}
