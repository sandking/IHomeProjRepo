package com.ihome.act.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class RenderView extends SurfaceView implements Callback {

	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	private Paint mPaint;
	private Rect frameRect;

	private boolean isCreated;
	private final byte[] lock_preview = new byte[0];

	public RenderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHolder = getHolder();
		mHolder.addCallback(this);

		mPaint = new Paint();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (lock_preview) {
			isCreated = true;
			frameRect = holder.getSurfaceFrame();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		synchronized (lock_preview) {
			isCreated = false;
		}
	}

	public void render(Bitmap bmp, Rect rect) {
		synchronized (lock_preview) {
			if (!isCreated)
				return;

			try {

				mCanvas = mHolder.lockCanvas();

				if (mCanvas != null)
					render_view(mCanvas, bmp, rect);

			} finally {
				if (mCanvas != null)
					mHolder.unlockCanvasAndPost(mCanvas);
			}

		}
	}

	private void render_view(Canvas canvas, Bitmap bmp, Rect render_rect) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG));

		int width = frameRect.right;
		int height = frameRect.bottom;

		int space = ((int) (render_rect.width() - render_rect.height() * 1.0f
				/ height * width) >> 1);

		render_rect.set(render_rect.left + space, render_rect.top,
				render_rect.width() - space, render_rect.height());

		canvas.drawBitmap(bmp, render_rect, frameRect, mPaint);
	}
}
