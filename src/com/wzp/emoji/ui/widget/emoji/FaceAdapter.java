package com.wzp.emoji.ui.widget.emoji;

import java.util.List;

import com.wzp.emoji.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class FaceAdapter extends BaseAdapter {
	private List<Emoji> mData;
	private LayoutInflater mInflater;
	private class VHolder {
		private ImageView mIvFace;
	}

	public FaceAdapter(Context context, List<Emoji> data) {
		mInflater = LayoutInflater.from(context);
		mData = data;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		if (mData != null && mData.size() != 0) {
			return mData.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VHolder holder = null;
		if (convertView == null) {
			holder = new VHolder();
			convertView = mInflater.inflate(R.layout.item_face, null);
			holder.mIvFace = (ImageView) convertView
					.findViewById(R.id.iv_item_face);
			convertView.setTag(holder);
		} else {
			holder = (VHolder) convertView.getTag();
		}
		Emoji item = (Emoji) getItem(position);
		if (item != null) {
			holder.mIvFace.setImageResource(item.getId());
		}
		return convertView;
	}

}
