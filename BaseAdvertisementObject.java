package com.tongcheng.entity.common;

import java.io.Serializable;

public class BaseAdvertisementObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1159247545653707478L;

	/**
	 * 重定向. ()
	 */
	protected String redirectUrl;
	
	/**
	 * 图片路径. (通常作为图片下载url.)
	 */
	protected String imageUrl;
	
	/**
	 * 图片tag.
	 */
	protected String tag;

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
