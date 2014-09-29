package com.tongcheng.android.myWidget;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tongcheng.android.R;
import com.tongcheng.android.base.ImageLoadeCallback;
import com.tongcheng.android.base.ImageLoader;
import com.tongcheng.android.scenery.sceneryUtils.ImageIndexUtil;
import com.tongcheng.entity.common.BaseAdvertisementObject;
import com.tongcheng.util.NoticeTools;
import com.tongcheng.util.SystemConfig;
import com.tongcheng.util.Tools;

public class AdvertisementView extends RelativeLayout implements
		OnItemSelectedListener, OnItemClickListener, OnTouchListener {

	// 指示器处于上方.
	public final static int LOCATION_INDICATER_TOP = ALIGN_PARENT_TOP;

	// 指示器处于下方.
	public final static int LOCATION_INDICATER_BOTTOM = ALIGN_PARENT_BOTTOM;

	private final static boolean _DBG_ = true;

	private final static int FLAG_AUTO_SWITCH = 0x10;

	// 默认3秒跳转广告.
	private final static int DEFAULT_SWITCH_RATE = 3000;

	private int mAutoSwitchRate = DEFAULT_SWITCH_RATE;

	private final ArrayList<BaseAdvertisementObject> mDatas = new ArrayList<BaseAdvertisementObject>();

	private final Context mContext;
	private LayoutInflater mInflater;

	private final RelativeLayout.LayoutParams adContentParam = new RelativeLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	private final RelativeLayout.LayoutParams adIndicaterContainerParam = new RelativeLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	// 广告容器
	private MyInfiniteGallery mAdContent;
	private AdvertisementAdapter mDefaultAdapter;

	private int dmWidth;

	private int widthRat = 72;
	private int heightRat = 13;

	// 广告指示器
	private LinearLayout mAdIndicaterContainer;
	private ImageIndexUtil mAdIndicater;
	private int mIndicaterLocation = LOCATION_INDICATER_BOTTOM;
	private int mIndicaterMargin;

	// 统计工具需要的值
	private String mEventId;
	private String mParam;

	// 是否自动切换
	private boolean flag_switch = true;

	// 是否当前为Manual状态
	private boolean flag_manual = false;

	// 是否已经开启自动轮播.
	private boolean flag_start = false;

	private OnItemClickListener mItemClickListener;

	// ----------------------
	private ImageLoader mImageLoader;
	private Timer mTimer = null;
	private TimerTask mTask = null;

	@SuppressLint("HandlerLeak")
	private Handler mAutoSwitchHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FLAG_AUTO_SWITCH:
				switchItem();
				break;
			default:
				super.handleMessage(msg);
			}
		};
	};

	private final Handler mPbHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProgressBar pb_loaing = (ProgressBar) msg.obj;
			if (msg.what != 0) {
				pb_loaing.setProgress(msg.what);
				pb_loaing.postInvalidate();
			}
		};
	};

	public AdvertisementView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initView();
	}

	public AdvertisementView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AdvertisementView(Context context) {
		super(context);
		this.mContext = context;
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initView();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			flag_manual = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			flag_manual = false;
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final int item_position = position % mDatas.size();

		final BaseAdvertisementObject data_obj = mDatas.get(item_position);

		boolean flag = false;

		if (mItemClickListener != null) {
			flag = mItemClickListener.onItemClick(parent, view, item_position,
					id, data_obj);
		}

		if (!flag) {
			if (!TextUtils.isEmpty(this.mEventId)) {
				Tools.setUmengId(mContext, mEventId, mParam);
			}

			if (data_obj != null
					&& !TextUtils.isEmpty(data_obj.getRedirectUrl())) {
				SystemConfig.iTagType = 2;
				String tag = data_obj.getTag();
				SystemConfig.ADTAG2 = tag;
				NoticeTools.initNoticeUrl((Activity) mContext,
						data_obj.getRedirectUrl(), "");
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		mAdIndicater.setSelectIndex(position % mDatas.size());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/**
	 * 设置监听器.
	 * 
	 * @param l
	 */
	public void setOnItemClickListener(OnItemClickListener l) {
		this.mItemClickListener = l;
	}

	/**
	 * 设置指示器的位置.
	 * 
	 * @param location
	 *            {@link #LOCATION_INDICATER_TOP} &
	 *            {@link #LOCATION_INDICATER_BOTTOM}
	 */
	public void setIndicaterLocation(int location) {
		this.mIndicaterLocation = location;
	}

	/**
	 * 设置自动播放间隔时间 (单位:ms).
	 * 
	 * @param rate
	 */
	public void setAutoSwitchRate(int rate) {
		this.mAutoSwitchRate = rate;
	}

	/**
	 * @see #setAdvertisementData(ArrayList, boolean)
	 * @param datas
	 */
	public void setAdvertisementData(
			ArrayList<? extends BaseAdvertisementObject> datas) {
		setAdvertisementData(datas, true);
	}

	/**
	 * 为广告填充数据.
	 * 
	 * @param datas
	 * @param clearFirst
	 *            填充前是否清除之前的数据.
	 */
	public void setAdvertisementData(
			ArrayList<? extends BaseAdvertisementObject> datas,
			boolean clearFirst) {
		if (datas == null) {
			return;
		}

		if (clearFirst) {
			mDatas.clear();
		}

		mDatas.addAll(datas);

		set();

		// resetIndicater();

		play();

		mDefaultAdapter.notifyDataSetChanged();
	}

	/**
	 * 设置广告的宽高比例，在调用setAdvertismentlistData之前才会起作用
	 * 
	 * @param widthRat
	 * @param heightRat
	 */
	public void setAdvertisementRate(int widthRat, int heightRat) {
		if (widthRat <= 0 || heightRat <= 0) {
			// throw new RuntimeException("widthRat & heightRat must > 0 !");
			return;
		}

		this.widthRat = widthRat;
		this.heightRat = heightRat;
	}

	/**
	 * 设置图片加载器.
	 * 
	 * @param loader
	 */
	public final void setImageLoader(ImageLoader loader) {
		mImageLoader = loader;
	}

	/**
	 * 设置屏幕宽度.
	 */
	public final void setDmWidth(int w) {
		this.dmWidth = w;
	}

	/**
	 * 设置统计的参数.
	 * 
	 * @param eventId
	 * @param param
	 */
	public void setEventId(String eventId, String param) {
		this.mEventId = eventId;
		this.mParam = param;
	}

	/**
	 * 禁止自动播放. 于 {@code play} 前使用，{@code play} 后使用 {@code stop} 终止.
	 * 
	 * @see #play()
	 * @see #stop()
	 */
	public void disabledAutoSwitch() {
		flag_switch = false;
	}

	/**
	 * 自动切换开启.
	 */
	public void play() {
		if (!flag_switch) {
			return;
		}

		if (mDatas.size() <= 1) {
			return;
		}

		if (flag_start) {
			return;
		}

		flag_start = true;

		mTimer = new Timer();

		mTask = new TimerTask() {
			@Override
			public void run() {
				notifyAutoSwitch();
			}
		};

		mTimer.schedule(mTask, mAutoSwitchRate, mAutoSwitchRate);
	}

	/**
	 * 停止自动播放.
	 */
	public void stop() {
		if (!flag_start) {
			return;
		}

		flag_start = false;

		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mAutoSwitchHandler != null) {
			mAutoSwitchHandler.removeMessages(FLAG_AUTO_SWITCH);
		}
	}

	/**
	 * 重设指示器的位置.
	 */
	public void resetIndicater() {
		mAdIndicater.setSelectIndex(0);
	}

	/**
	 * 通知需要切换广告
	 */
	protected void notifyAutoSwitch() {
		if (flag_manual) {
			return;
		}

		mAutoSwitchHandler.sendEmptyMessage(FLAG_AUTO_SWITCH);
	}

	/**
	 * 切换广告内容
	 */
	protected void switchItem() {
		mAdContent.onScroll(null, null, 1, 0);
		mAdContent.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
	}

	/**
	 * 设置广告属性. 重写设置Gallery的属性.
	 * 
	 * @param ad
	 */
	protected void setAdContentProperties(MyInfiniteGallery ad) {
		ad.setVerticalFadingEdgeEnabled(false);
		ad.setHorizontalFadingEdgeEnabled(false);
		ad.setSoundEffectsEnabled(false);
		ad.setSpacing(0);
		ad.setUnselectedAlpha(1);
	}

	/**
	 * 获取广告适配器，可根据自身需求传递广告adapter来做Item特殊性.
	 * 
	 * @return
	 */
	protected AdvertisementAdapter getAdvertisementAdapter() {
		return new DefaultAdvertisementAdapter(mDatas);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initView() {
		WindowManager windowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metric);

		dmWidth = metric.widthPixels;

		mIndicaterMargin = (int) (3 * metric.density + 0.5f);

		mAdContent = new MyInfiniteGallery(mContext.getApplicationContext());
		setAdContentProperties(mAdContent);
		mAdContent.setOnTouchListener(this);
		mAdContent.setOnItemSelectedListener(this);
		mAdContent.setOnItemClickListener(this);

		mDefaultAdapter = getAdvertisementAdapter();
		mAdContent.setAdapter(mDefaultAdapter);

		mAdIndicaterContainer = new LinearLayout(
				mContext.getApplicationContext());
		mAdIndicater = new ImageIndexUtil(mContext.getApplicationContext());

		mAdIndicaterContainer.addView(mAdIndicater);
	}

	/**
	 * set self , set & add child view.
	 */
	private void set() {
		final int w = dmWidth;
		final int h = dmWidth / widthRat * heightRat;

		final int ad_count = mDatas.size();

		// set self values.
		ViewGroup.LayoutParams rl_param = getLayoutParams();

		if (rl_param == null) {
			rl_param = new LayoutParams(w, h);
		} else {
			rl_param.width = w;
			rl_param.height = h;
		}
		setLayoutParams(rl_param);

		// remove views
		if (getChildCount() > 0) {
			removeAllViews();
		}

		// set & add ad_content.
		addView(mAdContent, adContentParam);

		// set & add ad_indicater.
		adIndicaterContainerParam.addRule(mIndicaterLocation);
		adIndicaterContainerParam.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE);
		adIndicaterContainerParam.bottomMargin = mIndicaterMargin;
		adIndicaterContainerParam.topMargin = mIndicaterMargin;

		mAdIndicaterContainer.setBackgroundColor(Color.RED);
		addView(mAdIndicaterContainer, adIndicaterContainerParam);

		mAdContent.setGalleryCount(ad_count);
		mAdIndicater.setTotal(ad_count);

		mAdIndicater.setVisibility(ad_count <= 1 ? View.GONE : View.VISIBLE);
	}

	void printf(String format, Object... args) {
		if (_DBG_)
			Log.e(getClass().getSimpleName(), String.format(format, args));
	}

	public interface OnItemClickListener {

		/**
		 * OnItemClickListener
		 * 
		 * @param parent
		 * @param view
		 * @param position
		 * @param id
		 * @param obj
		 *            被点击的广告对象.
		 * 
		 * @return 是否消费完成，消费完成则不继续旧代码逻辑.
		 */
		boolean onItemClick(AdapterView<?> parent, View view, int position,
				long id, BaseAdvertisementObject obj);
	}

	private static class ViewHolder {
		public ImageView ad_img;
		public ProgressBar ad_pb;
	}

	private final class DefaultAdvertisementAdapter extends
			AdvertisementAdapter {

		private ViewHolder mHolder;

		public DefaultAdvertisementAdapter(
				ArrayList<BaseAdvertisementObject> datas) {
			super(datas);
		}

		@Override
		public View getView(BaseAdvertisementObject data, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.image_of_home_gallery_item, parent, false);

				mHolder = new ViewHolder();
				mHolder.ad_img = (ImageView) convertView
						.findViewById(R.id.img_home_gallery);
				mHolder.ad_pb = (ProgressBar) convertView
						.findViewById(R.id.pb_img_loading);

				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			if (data != null && mImageLoader != null) {
				mImageLoader.displayImage(data.getImageUrl(), mHolder.ad_img,
						new ImageLoadeCallback(mHolder.ad_pb, mPbHandler));
			} else {
				// TODO data 为 null 无load 时显示默认图片.
				mHolder.ad_img
						.setBackgroundResource(R.drawable.bg_home_ad_small);
			}
			return convertView;
		}
	}

	/**
	 * 广告适配器基类，循环播放.
	 * 
	 * @author sk. 09145
	 * @date 2014-9-25
	 */
	public static abstract class AdvertisementAdapter extends BaseAdapter {
		private ArrayList<BaseAdvertisementObject> datas;

		public AdvertisementAdapter(ArrayList<BaseAdvertisementObject> datas) {
			this.datas = datas;
		}

		public int getDataSize() {
			return datas == null ? 0 : datas.size();
		}

		@Override
		public int getCount() {
			final int count = getDataSize();
			return count > 1 ? Integer.MAX_VALUE : count;
		}

		@Override
		public Object getItem(int position) {
			return datas == null ? null : datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int item_position = position % getDataSize();
			final BaseAdvertisementObject data = (BaseAdvertisementObject) getItem(item_position);
			return getView(data, convertView, parent);
		}

		/**
		 * 
		 * @param data
		 *            广告对象数据.
		 * @param convertView
		 * @param parent
		 * @return
		 */
		public abstract View getView(BaseAdvertisementObject data,
				View convertView, ViewGroup parent);
	}
}
