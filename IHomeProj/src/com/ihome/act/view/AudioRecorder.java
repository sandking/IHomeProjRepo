package com.ihome.act.view;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioRecorder {

	private final static int MIN_SIZE = 4096;

	private int frequency = 8000;
	private int channel = AudioFormat.CHANNEL_IN_MONO;
	private int sampbit = AudioFormat.ENCODING_PCM_16BIT;

	private int mRecordSize;

	private AudioRecord mRecord;
	
	public AudioRecorder() {
		
		int min_size = AudioRecord
				.getMinBufferSize(frequency, channel, sampbit);
		
		mRecordSize = min_size * 5;
		
		if (mRecordSize < 1024)
			mRecordSize = 1024;
		if (mRecordSize % 1024 != 0)
			mRecordSize = ((mRecordSize / 1024) + 1) * 1024;
	}

	private AudioRecord findRecord() {
		AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
				frequency, channel, sampbit, mRecordSize);

		if (record.getState() == AudioRecord.STATE_INITIALIZED) {
			return record;
		}

		return null;
	}

	public int getRecordSize() {
		return mRecordSize;
	}

	public boolean start() {
		if (mRecord != null)
			return false;

		mRecord = findRecord();

		if (mRecord != null) {
			mRecord.startRecording();
			return true;
		}

		return false;
	}

	public boolean stop() {
		if (mRecord.getState() == AudioRecord.STATE_INITIALIZED) {
			mRecord.stop();
			mRecord.release();
			mRecord = null;
			return true;
		}
		return false;
	}

	public int record(byte[] buffer, int offset, int length) {
		return mRecord.read(buffer, 0, length);
	}

	public int record(short[] buffer, int offset, int length) {
		return mRecord.read(buffer, 0, length);
	}
}
