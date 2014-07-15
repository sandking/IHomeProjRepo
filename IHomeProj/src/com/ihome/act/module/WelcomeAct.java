package com.ihome.act.module;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

import com.ihome.R;
import com.ihome.act.RemoteActivity;
import com.ihome.serv.IServManager;
import com.ihome.serv.LoginInfor;
import com.ihome.serv.LoginManager.LoginResult;
import com.ihome.serv.LoginManager.LoginState;
import com.ihome.serv.RazemIntent;
import com.ihome.serv.RemoteServ;

/**
 * Created by sk on 14-6-27.
 */
public class WelcomeAct extends RemoteActivity {

	private IServManager mServManager;

	private TextView txt_show;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Intent intent = new Intent();
		intent.setClass(this, RemoteServ.class);
		startService(intent);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_welcome);
		txt_show = (TextView) findViewById(R.id.txt_show);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregister();
	}

	@Override
	protected void onLoginRet(final LoginInfor infor, LoginResult ret) {
		super.onLoginRet(infor, ret);
		switch (ret) {
		case _RAZEM_LOGIN_RESULT_SUCCESS:
			goto_request(infor);
			break;
		default:
			goto_login();
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		mServManager = IServManager.Stub.asInterface(arg1);

		try {
			final LoginState state = LoginState.values()[mServManager
					.rzGetLoginState()];

			printf("LoginState - %s", state);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					txt_show.setText(String.format("LoginState - %s", state));
				}
			});

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
		} catch (RemoteException e) {
			handleRemoteException(e);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		mServManager = null;
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
