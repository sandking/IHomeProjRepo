package com.ihome.serv;

import android.os.Parcel;
import android.os.Parcelable;

public class MemberInfor implements Parcelable {

	/* User account id */private int account;
	/* User nick name */private String nick;
	/* User title */private String title;
	/* User call state */private int call_state;
	/* User icon length */private int icon_length;
	/* User icon content */private byte[] icon;

	public MemberInfor() {
	}

	public MemberInfor(int account, String nick, String title, int call_state,
			byte[] icon) {
		this.account = account;
		this.nick = nick;
		this.title = title;
		this.call_state = call_state;
		this.icon_length = icon.length;
		this.icon = icon;
	}

	public int getAccount() {
		return account;
	}

	public void setAccount(int account) {
		this.account = account;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCall_state() {
		return call_state;
	}

	public void setCall_state(int call_state) {
		this.call_state = call_state;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MemberInfor) {
			MemberInfor new_infor = (MemberInfor) o;
			if (account == new_infor.account)
				return true;
		}
		return false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(account);
		dest.writeString(nick);
		dest.writeString(title);
		dest.writeInt(call_state);
		dest.writeInt(icon_length);
		dest.writeByteArray(icon);
	}

	public static final Parcelable.Creator<MemberInfor> CREATOR = new Parcelable.Creator<MemberInfor>() {

		@Override
		public MemberInfor createFromParcel(Parcel source) {

			final int acc = source.readInt();
			final String nick = source.readString();
			final String title = source.readString();
			final int call_state = source.readInt();
			final int icon_length = source.readInt();

			final byte[] icon = new byte[icon_length];
			source.readByteArray(icon);

			return new MemberInfor(acc, nick, title, call_state, icon);
		}

		@Override
		public MemberInfor[] newArray(int size) {
			return new MemberInfor[size];
		}
	};
}
