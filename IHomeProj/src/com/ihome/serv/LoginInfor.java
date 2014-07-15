package com.ihome.serv;

import android.os.Parcel;
import android.os.Parcelable;

public final class LoginInfor implements Parcelable {

	private final static int DELAY_BASE = 3000;
	private final static int DELAY_INCREASE = 5000;

	public final String ip;
	public final int account;
	public final String pwd;
	public int retrys;

	public LoginInfor(String ip, int account, String pwd) {
		this.ip = ip;
		this.account = account;
		this.pwd = pwd;
		this.retrys = -1;
	}

	public int getDelayRetry() {
		if (retrys == 0)
			return 0;

		return DELAY_BASE + retrys * DELAY_INCREASE;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(this.ip);
		arg0.writeInt(this.account);
		arg0.writeString(this.pwd);
		arg0.writeInt(this.retrys);
	}

	@Override
	public String toString() {
		return String.format("infor - { %s - %d - %s - %d}", ip, account, pwd,
				retrys);
	}

	public static final Parcelable.Creator<LoginInfor> CREATOR = new Parcelable.Creator<LoginInfor>() {

		@Override
		public LoginInfor createFromParcel(Parcel arg0) {
			return new LoginInfor(arg0.readString(), arg0.readInt(),
					arg0.readString());
		}

		@Override
		public LoginInfor[] newArray(int arg0) {
			return new LoginInfor[arg0];
		}
	};
}
