package com.ihome.act.module;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

import com.ihome.R;
import com.ihome.act.RemoteActivity;
import com.ihome.serv.IServManager;
import com.ihome.serv.LoginInfor;
import com.ihome.serv.RazemIntent;
import com.ihome.serv.LoginManager.LoginResult;

public class LoginAct extends RemoteActivity {

	public final static String LOGIN_SERVER_ADDR = "115.28.246.43";
	public final static int LOGIN_SERVER_ACCOUNT = 1000000;
	public final static String LOGIN_SERVER_PASSWD = "1000";

	private IServManager mServManager;

	private TextView txt_show;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);
		txt_show = (TextView) findViewById(R.id.txt_show);
		register(RazemIntent.ACTION_LOGIN_STATE_CHANGED);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregister();
	}

	@Override
	protected void onLoginRet(final LoginInfor infor, final LoginResult ret) {
		super.onLoginRet(infor, ret);
		printf("%s -  %s", infor, ret);

		runOnUiThread(new Runnable() { 

			@Override
			public void run() {
				txt_show.setText(String.format("%s - %s", infor, ret));
			}
		});
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		mServManager = IServManager.Stub.asInterface(arg1);

		try {
			mServManager.rzLogin(LOGIN_SERVER_ADDR, LOGIN_SERVER_ACCOUNT,
					LOGIN_SERVER_PASSWD);

			txt_show.setText(String.format(
					"Start login - %s - %s - %s .....\n", LOGIN_SERVER_ADDR,
					LOGIN_SERVER_ACCOUNT, LOGIN_SERVER_PASSWD));

		} catch (RemoteException e) {
			handleRemoteException(e);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		mServManager = null;
	}
}
