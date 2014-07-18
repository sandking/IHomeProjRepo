package com.ihome.act.module;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.ihome.R;
import com.ihome.act.RemoteActivity;
import com.ihome.act.view.MemberAdapter;
import com.ihome.app.HelperUtils;
import com.ihome.serv.IServManager;
import com.ihome.serv.MemberInfor;
import com.ihome.serv.RazemIntent;

/**
 * Created by sk on 14-7-2.
 */
public class MembersAct extends RemoteActivity implements OnItemClickListener {

	private TextView txt_show;
	private GridView grid_show;

	private List<MemberInfor> member_infos;
	private MemberAdapter member_adapter;

	private Handler handler = new Handler();

	int account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_request);

		txt_show = (TextView) findViewById(R.id.txt_show);
		grid_show = (GridView) findViewById(R.id.grid_show);

		account = getIntent().getExtras().getInt(BUNDLE_LOGIN_INFOR_ACCOUNT);
		txt_show.setText(String.format("Request Act !    ID : %d \n", account));

		member_infos = new ArrayList<MemberInfor>();
		member_adapter = new MemberAdapter(this, member_infos);

		grid_show.setAdapter(member_adapter);
		grid_show.setOnItemClickListener(this);

		register(RazemIntent.ACTION_MEMBER_ACQUIRE,
				RazemIntent.ACTION_MEMBER_STATE_CHANGED);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (TEST_CALL_OUT)
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					final MemberInfor infor = find(-65535);
					if (infor == null) {
						printf("Find infor = null!!!");
						return;
					}
					
					goto_comm(infor);
				}
			}, 1000);
	}

	MemberInfor find(int id) {
		for (MemberInfor infor : member_infos)
			if (infor.getAccount() == id)
				return infor;

		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregister();
	}

	@Override
	protected void onBindPrepared(IServManager serv) throws RemoteException {
		serv.rzGetMemberList();
		serv.rzSetInfor(0x1F, "sk.", "sk.", 0,
				FileReaderHelper.readFromRaw(this, R.raw.sk));
	}

	@Override
	protected void onMemberAcquired(final MemberInfor infor) {
		super.onMemberAcquired(infor);
		if (infor.getAccount() == this.account)
			return;

		if (!member_infos.contains(infor))
			member_infos.add(infor);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txt_show.append(String.format(
						"[%s] ACQ { %d , %s , %s , %d , %d } \n",
						HelperUtils.obtainCurrTime("yyyy:MM:dd HH:mm:ss"),
						infor.getAccount(), infor.getNick(), infor.getTitle(),
						infor.getCall_state(), infor.getIcon().length));

				if (member_adapter != null)
					member_adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void onMemberOnlineStateChanged(final int account,
			final int online_state) {
		super.onMemberOnlineStateChanged(account, online_state);

		if (online_state == 0) {

		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txt_show.append(String.format("[%s] STATE { %d , %d } \n",
						HelperUtils.obtainCurrTime("yyyy:MM:dd HH:mm:ss"),
						account, online_state));

				if (member_adapter != null)
					member_adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		final MemberInfor infor = position < member_infos.size() ? member_infos
				.get(position) : null;

		if (infor == null)
			return;

		printf("infor : %d", infor.getAccount());

		goto_comm(infor);
	}

	void goto_comm(MemberInfor infor) {
		final Intent intent = new Intent();
		final Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_TYPE_GOTO_COMMUNITY, TYPE_CALLOUT);
		bundle.putInt(BUNDLE_LOGIN_INFOR_ACCOUNT, account);
		bundle.putParcelable(RazemIntent.BUNDLE_MEMBER_INFOR, infor);
		intent.putExtras(bundle);
		intent.setClass(this, CommunityAct.class);
		startActivity(intent);
	}
}
