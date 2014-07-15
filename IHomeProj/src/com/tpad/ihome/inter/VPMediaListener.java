package com.tpad.ihome.inter;

public interface VPMediaListener
{
	void onVideoPlayed(int x, int y, int w, int h);

	int onAudioPlayed(int size);

	int captureAudio(int size);

	int captureVideo(int size);
} 
    