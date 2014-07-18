package com.ihome.act.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.TextView;

import com.ihome.R;
import com.ihome.act.RemoteActivity;
import com.ihome.serv.IServManager;
import com.ihome.serv.LoginInfor;
import com.ihome.serv.RazemIntent;

public class LoginAct extends RemoteActivity {

	public final static String LOGIN_SERVER_ADDR = "115.28.246.43";
	public final static int LOGIN_SERVER_ACCOUNT = 1000000;
	public final static String LOGIN_SERVER_PASSWD = "1000";

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
	protected void onLoginRet(final LoginInfor infor) {
		super.onLoginRet(infor);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txt_show.append(String.format("Result : %s - ",
						infor != null ? "success" : "failed"));
			}
		});

		if (infor != null) {
			Bundle bundle = new Bundle();
			bundle.putInt(BUNDLE_LOGIN_INFOR_ACCOUNT, infor.account);
			Intent intent = new Intent();
			intent.setClass(this, MembersAct.class);
			intent.putExtras(bundle);
			startActivity(intent);

			finish();
		}
	}

	@Override
	protected void onBindPrepared(IServManager serv) throws RemoteException {
		serv.rzLogin(LOGIN_SERVER_ADDR, LOGIN_SERVER_ACCOUNT,
				LOGIN_SERVER_PASSWD);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txt_show.setText(String.format(
						"Start login - %s - %s - %s .....\n",
						LOGIN_SERVER_ADDR, LOGIN_SERVER_ACCOUNT,
						LOGIN_SERVER_PASSWD));
			}
		});
	}
}
