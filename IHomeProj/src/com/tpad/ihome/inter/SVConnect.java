package com.tpad.ihome.inter;

public class SVConnect
{
	
	public native static void init(RZInitCompleteListener l);

//	public native static void setLoginStateListener(RZLoginStateListener listener);

	public native static void setCacheRootPath(String path);
	
	public native static int login(String addr, int account, String passwd);
	
	public native static void logout();

	public native static void setInfomation(int set_bits, String id, String title, int call_status, byte[] icon_buf);

	public native static void setMemberChangedListener(RZMemberChangedListener listener);
    
	public native static void setEventCallback(RZEventCallback event);
	
	public native static void getMemberList();
  
	public native static void getMemberInfo(int account, int update_bits);
	
	public native static void setCallStateListener(RZCallStateListener listener);

	public native static void call(int targetid);
    
	public native static void answer();
   
	public native static void reject();
	
}
