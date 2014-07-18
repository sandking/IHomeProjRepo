package com.ihome.act.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ihome.R;
import com.ihome.act.RemoteActivity;
import com.ihome.serv.IServManager;
import com.ihome.serv.RazemIntent;

/**
 * Created by sk on 14-7-2.
 */
public class InCommingAct extends RemoteActivity implements OnClickListener {
	final static int FLAG_KEEP_NO_KEYGUARD = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

	final static int FLAG_KEEP_SCREEN_ON = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
			| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

	private IServManager mServManager;

	private TextView txt_show;
	private Button btn_answer, btn_reject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Window win = getWindow();
		win.addFlags(FLAG_KEEP_NO_KEYGUARD);
		win.addFlags(FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.act_incomming);

		txt_show = (TextView) findViewById(R.id.txt_show);
		btn_answer = (Button) findViewById(R.id.btn_answer);
		btn_reject = (Button) findViewById(R.id.btn_reject);

		btn_answer.setOnClickListener(this);
		btn_reject.setOnClickListener(this);

		int target = getIntent().getExtras().getInt(
				RazemIntent.BUNDLE_CALL_INCOMMING_TAG_TARGET, 0);

		txt_show.setText("From : " + target);
	}

	@Override
	protected void onBindPrepared(IServManager serv) throws RemoteException {
		mServManager = serv;
	}

	@Override
	public void onClick(View v) {

		try {
			switch (v.getId()) {
			case R.id.btn_answer:
				// mServManager.rzAnswer();
				final Intent intent = new Intent();
				final Bundle bundle = new Bundle();

				bundle.putInt(BUNDLE_LOGIN_INFOR_ACCOUNT,
						mServManager.rzGetLoginInfor().account);

				bundle.putInt(BUNDLE_TYPE_GOTO_COMMUNITY, TYPE_INCOMMING);
				intent.putExtras(bundle);
				intent.setClass(this, CommunityAct.class);
				startActivity(intent);
				finish();
				 
				break;
			case R.id.btn_reject:
				mServManager.rzReject();
				finish();      
				break;
			}
		} catch (RemoteException e) {
			handleRemoteException(e);
		}
	}

}
