package com.ihome.act.view;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayer {
	/***************** Default ******************/
	private final static int MIN_SIZE = 4096;

	private int frequency = 8000;
	private int channel = AudioFormat.CHANNEL_OUT_MONO;
	private int sampbit = AudioFormat.ENCODING_PCM_16BIT;

	private AudioTrack mTrack;

	private int mPlayedSize;

	public AudioPlayer() {
		int min_size = AudioTrack.getMinBufferSize(frequency, channel, sampbit);
		mPlayedSize = min_size * 5;

		if (mPlayedSize < 1024)
			mPlayedSize = 1024;
		if (mPlayedSize % 1024 != 0)
			mPlayedSize = ((mPlayedSize / 1024) + 1) * 1024;
	}

	public AudioPlayer(int f, int c, int s) {
		this.frequency = f;
		this.channel = c;
		this.sampbit = s;
	}

	AudioTrack initAudioTrack() {
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				frequency, channel, sampbit, mPlayedSize,
				AudioTrack.MODE_STREAM);

		return audioTrack.getState() == AudioTrack.STATE_INITIALIZED ? audioTrack
				: null;
	}

	public boolean start() {
		mTrack = initAudioTrack();

		if (mTrack == null) {
			System.out.println("AudioTrack init error!");
			return false;
		}

		mTrack.play();
		return true;
	}

	public int play(byte[] buffer, int offset, int len) {
		return mTrack.write(buffer, offset, len);
	}

	public int play(short[] buffer, int offset, int len) {
		return mTrack.write(buffer, offset, len);
	}

	public void stop() {
		if (mTrack != null) {
			mTrack.stop();
			mTrack.release();
			mTrack = null;
		}
	}

}
