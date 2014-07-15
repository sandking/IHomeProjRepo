package com.ihome.act.module;

import android.os.Bundle;
import android.os.Handler;
import com.ihome.R;
import com.ihome.act.BaseActivity;

/**
 * Created by sk on 14-6-27.
 */
public class WelcomeAct extends BaseActivity
{
	private Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_welcome);
	}
}
