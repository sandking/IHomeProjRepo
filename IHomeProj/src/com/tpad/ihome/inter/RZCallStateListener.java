package com.tpad.ihome.inter;

public interface RZCallStateListener
{
	void onCallIncomming(int targetid);

	void onCallSuccess(String ip, int port, String key, int link_dir, int udp_socket);

	void onCallFailed(int state);
}