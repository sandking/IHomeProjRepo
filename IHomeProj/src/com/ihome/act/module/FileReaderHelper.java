package com.ihome.act.module;

import java.io.InputStream;

import android.content.Context;

public class FileReaderHelper
{
	public static byte[] readFromRaw(Context context, int res)
	{
		InputStream ins = null;
		byte[] read_buf = null;
		try
		{
			ins = context.getResources().openRawResource(res);

			int byteCount = ins.available();

			read_buf = new byte[byteCount];

			ins.read(read_buf);

			ins.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return read_buf;
	}
}