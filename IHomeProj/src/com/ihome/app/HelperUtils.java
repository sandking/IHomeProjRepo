package com.ihome.app;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperUtils
{
	@SuppressLint("SimpleDateFormat")
	public static String obtainCurrTime(String format)
	{
		return new SimpleDateFormat(format).format(new Date());
	}

	public static File[] getContextFiles(Context ctx, final String prefix, final String extension)
	{
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.startsWith(prefix) && name.endsWith(extension);
			}
		};
		return filesDir.listFiles(filter);
	}
}