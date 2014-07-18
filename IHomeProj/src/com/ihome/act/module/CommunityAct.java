package com.ihome.act.module;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.ihome.R;
import com.ihome.act.RemoteActivity;
import com.ihome.act.view.AudioPlayer;
import com.ihome.act.view.AudioRecorder;
import com.ihome.act.view.CaptureView;
import com.ihome.act.view.RenderView;
import com.ihome.serv.IServManager;
import com.ihome.serv.MemberInfor;
import com.ihome.serv.RazemIntent;
import com.tpad.ihome.inter.VPCalloutListener;
import com.tpad.ihome.inter.VPConnect;
import com.tpad.ihome.inter.VPConnectListener;
import com.tpad.ihome.inter.VPDeInitCompleteListener;
import com.tpad.ihome.inter.VPInitCompleteListener;
import com.tpad.ihome.inter.VPMediaListener;

/**
 * Created by sk on 14-7-2.
 */
public class CommunityAct extends RemoteActivity implements
		VPInitCompleteListener, VPDeInitCompleteListener, VPConnectListener,
		VPMediaListener {

	static {
		System.loadLibrary("vp");
	}

	final static int FLAG_KEEP_NO_KEYGUARD = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

	final static int FLAG_KEEP_SCREEN_ON = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
			| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

	private final static int preview_width = 640;
	private final static int preview_height = 480;

	private final static int VIDEO_CAPTURE_BUF = (int) (preview_width
			* preview_height * 1.5f);

	private final static int AUDIO_BUF = 20 * 1024;

	private final Bitmap preview_bmp = Bitmap.createBitmap(preview_width,
			preview_height, Config.ARGB_8888);

	private RenderView render_view;
	private CaptureView capture_view;

	private MemberInfor out_memberInfor;
	private int bundle_type;

	private boolean vpinit_flag = false;

	private final Handler handler = new Handler();

	private final Runnable deinit_action = new Runnable() {
		@Override
		public void run() {
			VPConnect.deInit(CommunityAct.this);
			vpinit_flag = false;
		}
	};

	private boolean isConnected = false;

	private boolean isFailed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window win = getWindow();
		win.addFlags(FLAG_KEEP_NO_KEYGUARD);
		win.addFlags(FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.act_community);

		out_memberInfor = getIntent().getExtras().getParcelable(
				RazemIntent.BUNDLE_MEMBER_INFOR);

		bundle_type = getIntent().getExtras().getInt(
				BUNDLE_TYPE_GOTO_COMMUNITY, -1);

		final int account = getIntent().getExtras().getInt(
				BUNDLE_LOGIN_INFOR_ACCOUNT);

		printf("BUNDLE TYPE : %d    id : %d", bundle_type, account);

		render_view = (RenderView) findViewById(R.id.render_view);
		capture_view = (CaptureView) findViewById(R.id.capture_view);

		register(RazemIntent.ACTION_CALL_SUCCESS,
				RazemIntent.ACTION_CALL_FAILED);

		audio_player_buf = new byte[AUDIO_BUF];
		audio_recorder_buf = new byte[AUDIO_BUF];
		video_capture_buf = new byte[VIDEO_CAPTURE_BUF];

		capture_view.setCaptureBuf(video_capture_buf);

		init_vp(account);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregister();

		if (isFailed) {
			printf("onDestory - call failed will deinit_vp directly!!!");

			deinit_vp();
		}
		if (isConnected) {
			printf("onDestory - call failed will end call!!!");

			VPConnect.endCall();
		}
	}

	private void init_vp(final int account) {
		if (vpinit_flag) {
			printf("init_vp   vpinit_flag : %s", vpinit_flag);
			return;
		}

		final Runnable init_action = new Runnable() {
			@Override
			public void run() {
				vpinit_flag = true;

				VPConnect.init(CommunityAct.this, preview_bmp, preview_width,
						preview_height, account);
			}
		};
		new Thread(init_action, "VPConnect-Thread").start();
	}

	private void deinit_vp() {
		if (!vpinit_flag) {
			printf("deinit_vp   vpinit_flag : %s", vpinit_flag);
			return;
		}

		runOnUiThread(deinit_action);
	}

	@Override
	protected void onBindPrepared(IServManager serv) throws RemoteException {

		switch (bundle_type) {
		case TYPE_CALLOUT:
			printf("call out");
			if (out_memberInfor != null)
				serv.rzCall(out_memberInfor.getAccount());
			break;
		case TYPE_INCOMMING:
			printf("incomming");
			serv.rzAnswer();
			break;
		default:
			finish();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isFailed || isConnected)
				finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCallSuccess(final String ip, final int port,
			final String key, final int link_dir, final int udp_socket) {
		super.onCallSuccess(ip, port, key, link_dir, udp_socket);
		printf("onCallSuccess { %s , %d , %s , %d }", ip, port, key, link_dir,
				udp_socket);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				VPConnect.call(ip, port, key, link_dir, udp_socket);
			}
		});
	}

	@Override
	protected void onCallFailed(int state) {
		super.onCallFailed(state);
		printf("onCallFailed { %d }", state);
		isFailed = true;
		finish();
	}

	final Rect render_rect = new Rect();

	@Override
	public void onVideoPlayed(int x, int y, int w, int h) {
		render_rect.set(x, y, x + w, y + h);
		render_view.render(preview_bmp, render_rect);
	}

	@Override
	public int onAudioPlayed(int size) {
		int psize = 0;
		if (audio_player != null)
			psize = audio_player.play(audio_player_buf, 0, size);

		return psize;
	}

	@Override
	public int captureAudio(int size) {
		int rsize = 0;

		if (audio_recorder != null)
			rsize = audio_recorder.record(audio_recorder_buf, 0, size);
		return rsize;
	}

	@Override
	public int captureVideo(int size) {
		capture_view.capture();
		return VIDEO_CAPTURE_BUF;
	}

	@Override
	public void onConnected() {
		printf("onConnected!!!");
		isConnected = true;
		init_audio_hardware();

		if (TEST_CALL_OUT)

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, 100);
	}

	@Override
	public void onDisconnected() {
		printf("onDisconnected!!!");
		release_audio_hardware();
		deinit_vp();
	}

	@Override
	public void onDeInitCompleted() {
		printf("onDeInitCompleted!!!");

		if (preview_bmp != null && !preview_bmp.isRecycled())
			preview_bmp.recycle();
	}

	@Override
	public void onInitCompleted() {
		printf("onInitCompleted!!!");

		VPConnect.setAudioBuf(audio_player_buf, audio_recorder_buf);
		VPConnect.setCameraBuffer(video_capture_buf);

		VPConnect.setConnectListener(this);
		VPConnect.setMediaListener(this);
	}

	private AudioPlayer audio_player;
	private AudioRecorder audio_recorder;

	private byte[] audio_player_buf, audio_recorder_buf;
	private byte[] video_capture_buf;

	private void init_audio_hardware() {
		// initialize the audio player.
		audio_player = new AudioPlayer();
		audio_player.start();

		// initialize the audio recorder.
		audio_recorder = new AudioRecorder();
		audio_recorder.start();

		printf("init_audio_hardware !!!");
	}

	private void release_audio_hardware() {
		if (audio_player != null) {
			audio_player.stop();
			audio_player = null;
		}

		if (audio_recorder != null) {
			audio_recorder.stop();
			audio_recorder = null;
		}

		printf("release_audio_hardware !!!");
	}
}
