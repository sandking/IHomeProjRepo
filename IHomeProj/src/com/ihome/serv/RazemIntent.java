package com.ihome.serv;

public class RazemIntent {

	// ACTION_FLAG : CALL_STATE
	public final static String ACTION_CALL_SUCCESS = "com.ihome.action.CALL_SUCCESS";
	public final static String ACTION_CALL_FAILED = "com.ihome.action.CALL_FAILED";
	public final static String ACTION_CALL_INCOMMING = "com.ihome.action.CALL_INCOMMING";

	// ACTION_FLAG : LOGIN_STATE
	public final static String ACTION_LOGIN_STATE_CHANGED = "com.ihome.action.LOGIN_STATE_CHANGED";

	public final static String ACTION_MEMBER_ACQUIRE = "com.ihome.action.MEMBER_ACQUIRE";
	public final static String ACTION_MEMBER_STATE_CHANGED = "com.ihome.action.MEMBER_STATE_CHANGED";

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

	/**@deprecated*/public final static String BUNDLE_MEMBER_ACQUIRE_ACCOUT = "bundle_acquire_account";
	/**@deprecated*/public final static String BUNDLE_MEMBER_ACQUIRE_NICKNAME = "bundle_acquire_nick";
	/**@deprecated*/public final static String BUNDLE_MEMBER_ACQUIRE_TITLE = "bundle_acquire_title";
	/**@deprecated*/public final static String BUNDLE_MEMBER_ACQUIRE_CALLSTATUS = "bundle_acquire_callstatus";
	/**@deprecated*/public final static String BUNDLE_MEMBER_ACQUIRE_ICON = "bundle_acquire_icon";

	public final static String BUNDLE_MEMBER_INFOR = "bundle_member_infor";

	public final static String BUNDLE_MEMBER_ONLINE_ACCOUNT = "bundle_online_account";
	public final static String BUNDLE_MEMBER_ONLINE_STATE = "bundle_online_state";
}
