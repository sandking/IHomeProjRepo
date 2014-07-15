package com.ihome.serv;

public class RazemIntent {

	// ACTION_FLAG : CALL_STATE
	public final static String ACTION_CALL_SUCCESS = "com.ihome.action.CALL_SUCCESS";
	public final static String ACTION_CALL_FAILED = "com.ihome.action.CALL_FAILED";
	public final static String ACTION_CALL_INCOMMING = "com.ihome.action.CALL_INCOMMING";

	// ACTION_FLAG : LOGIN_STATE
	public final static String ACTION_LOGIN_STATE_CHANGED = "com.ihome.action.LOGIN_STATE_CHANGED";
	
	// BUNDLE_FLAG : CALL_SUCCESS
	public final static String BUNDLE_CALL_SUCCESS_TAG_IP = "bundle_call_success_ip";
	public final static String BUNDLE_CALL_SUCCESS_TAG_PORT = "bundle_call_success_port";
	public final static String BUNDLE_CALL_SUCCESS_TAG_KEY = "bundle_call_success_key";
	public final static String BUNDLE_CALL_SUCCESS_TAG_LINK_DIR = "bundle_call_success_link_dir";
	public final static String BUNDLE_CALL_SUCCESS_TAG_UDP_SOCKET = "bundle_call_success_udp_socket";

	// BUNDLE_FLAG : CALL_INCOMMING
	public final static String BUNDLE_CALL_INCOMMING_TAG_TARGET = "bundle_call_incomming_target";

	// BUNDLE_FLAG : CALL_FAILED
	public final static String BUNDLE_CALL_FAILED_TAG_STATE = "bundle_call_failed_state";

	// BUNDLE_FLAG : LOGIN_RET
	public final static String BUNDLE_LOGIN_TAG_RESULT = "bundle_login_result";
	public final static String BUNDLE_LOGIN_TAG_INFOR = "bundle_login_infor";
}
