package com.tpad.ihome.inter;

import android.graphics.Bitmap;

public class VPConnect
{
	public native static void init(VPInitCompleteListener l, Bitmap bmp, int camera_w, int camera_h, int account_id);

	public native static void deInit(VPDeInitCompleteListener l);

	public native static void call(String ip, int port, String key, int link_dir, int udp_socket);

	public native static void endCall();

	public native static void setMediaListener(VPMediaListener l);

	public native static void setConnectListener(VPConnectListener l);

	public native static void setCalloutListener(VPCalloutListener l);

	public native static void setAudioBuf(byte[] play_buf, byte[] cap_buf);

	public native static void setCameraBuffer(byte[] buf);

	public native static void setAudioMute(int mute);// 0--> mute 1--> not mute

	public native static void setVideoMute(int mute);

}
