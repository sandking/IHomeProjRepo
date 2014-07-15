package com.tpad.ihome.inter;

public interface RZMemberChangedListener
{
	void onMemberAcquired(int accountid, String id, String title, int callstatus, byte[] icon);

	void onMemberOnlineStateChanged(int account_id, int online_offline);

	void onMemberChanged(int account_id, int changed_bits);
}