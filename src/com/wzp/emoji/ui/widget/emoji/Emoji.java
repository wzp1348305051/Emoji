package com.wzp.emoji.ui.widget.emoji;

public class Emoji {
	/** 表情资源图片对应的ID */
	private int mId;
	/** 表情资源的文件名 */
	private String mImg;
	/** 表情资源对应的文字标识 */
	private String mTxt;

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getImg() {
		return mImg;
	}

	public void setImg(String img) {
		mImg = img;
	}

	public String getTxt() {
		return mTxt;
	}

	public void setTxt(String txt) {
		mTxt = txt;
	}

}
