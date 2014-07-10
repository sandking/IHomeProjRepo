package com.ihome.app;

/**
 * Created by sk on 14-6-27.
 */
public interface CrashListener
{
	/**
	 * Called when catch uncaught exception.
	 *
	 * @param ex
	 * @return if true - do report.
	 */
	boolean onCatch(Throwable ex);

	/**
	 * Called when commit.
	 *
	 * @param delete if true - delete the log-file.
	 */
	void onCommit(boolean delete);
}
