package com.ihome.act.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import com.ihome.R;
import com.ihome.act.RemoteActivity;
import com.ihome.serv.IServManager;
import com.ihome.serv.LoginInfor;
import com.ihome.serv.LoginManager.LoginState;
import com.ihome.serv.RazemIntent;
import com.ihome.serv.RemoteServ;

/**
 * Created by sk on 14-6-27.
 */
public class WelcomeAct extends RemoteActivity {

	private IServManager mServManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = new Intent();
		intent.setClass(this, RemoteServ.class);
		startService(intent);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_welcome);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregister();
	}

	@Override
	protected void onLoginRet(final LoginInfor infor) {
		super.onLoginRet(infor);
		if (infor != null)
			goto_request(infor);
		else
			goto_login();
	}

	@Override
	protected void onBindPrepared(IServManager serv) throws RemoteException {
		mServManager = serv;

		final LoginState state = LoginState.values()[mServManager
				.rzGetLoginState()];

		switch (state) {
		case OFFLINE:
			goto_login();
			break;
		case ONLINE:
			LoginInfor infor = mServManager.rzGetLoginInfor();
			goto_request(infor);
			break;
		case TRYING:
			wait_result();
			break;
		}

	}

	void goto_login() {
		Intent intent = new Intent();
		intent.setClass(this, LoginAct.class);
		startActivity(intent);

		finish();
	}

	void goto_request(LoginInfor infor) {
		Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_LOGIN_INFOR_ACCOUNT, infor.account);
		Intent intent = new Intent();
		intent.setClass(this, MembersAct.class);
		intent.putExtras(bundle);
		startActivity(intent);

		finish();
	}

	void wait_result() {
		register(RazemIntent.ACTION_LOGIN_STATE_CHANGED);
	}
}
