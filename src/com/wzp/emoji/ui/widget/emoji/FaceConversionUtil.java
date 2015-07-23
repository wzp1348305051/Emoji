package com.wzp.emoji.ui.widget.emoji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wzp.emoji.R;
import com.wzp.emoji.util.LogUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

public class FaceConversionUtil {
	private static final String TAG = "FaceConversionUtil";
	/** 每一页表情的个数 */
	private static final int PAGE_SIZE = 20;
	private static FaceConversionUtil mFaceConversionUtil;
	/** 保存于内存中的表情HashMap,键：表情文字；值：表情文件名称，如：[cty]:cry */
	private HashMap<String, String> mEmojiMap;
	/** 保存于内存中的表情集合 */
	private List<Emoji> mEmojis;
	/** 表情分页的结果集合 */
	public List<List<Emoji>> mEmojiLists;

	/**
	 * 初始化mEmojiMap，mEmojis，mEmojiLists
	 * */
	private FaceConversionUtil(Context context) {
		mEmojiMap = new HashMap<String, String>();
		mEmojis = new ArrayList<Emoji>();
		mEmojiLists = new ArrayList<List<Emoji>>();
		initData(context);
	}

	private void initData(Context context) {
		try {
			InputStream inStream = context.getResources().getAssets()
					.open("emoji.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStream, "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String temp;
			while ((temp = reader.readLine()) != null) {
				buffer.append(temp);
			}
			JSONArray array = new JSONArray(buffer.toString());
			int length = array.length();
			Resources resource = context.getResources();
			for (int i = 0; i < length; i++) {
				JSONObject object = array.getJSONObject(i);
				String txt = object.getString("txt");
				String img = object.getString("img");
				mEmojiMap.put(txt, img);
				Emoji emoji = new Emoji();
				emoji.setTxt(txt);
				emoji.setImg(img);
				emoji.setId(resource.getIdentifier(img, "drawable",
						context.getPackageName()));
				mEmojis.add(emoji);
			}
			int pageCount = (int) Math
					.ceil((double) mEmojis.size() / PAGE_SIZE);// 除号在这里是整除，必须强转为double
			for (int j = 0; j < pageCount; j++) {
				List<Emoji> emojis;
				if ((j + 1) * PAGE_SIZE <= mEmojis.size()) {
					emojis = mEmojis
							.subList(j * PAGE_SIZE, (j + 1) * PAGE_SIZE);
				} else {
					emojis = mEmojis.subList(j * PAGE_SIZE, mEmojis.size());
				}
				/** 添加最后一个删除键 */
				Emoji emojiDelete = new Emoji();
				emojiDelete.setId(R.drawable.ic_delete);
				emojis.add(emojiDelete);
				mEmojiLists.add(emojis);
			}
		} catch (IOException e1) {
			LogUtil.e(TAG, TAG + ".initData(context):" + e1.getMessage());
		} catch (JSONException e2) {
			LogUtil.e(TAG, TAG + ".initData(context):" + e2.getMessage());
		}

	}

	/**
	 * 获取表情分页集合
	 * */
	public List<List<Emoji>> getEmojiLists() {
		return mEmojiLists;
	}

	public static FaceConversionUtil getInstance(Context context) {
		if (mFaceConversionUtil == null) {
			mFaceConversionUtil = new FaceConversionUtil(context);
		}
		return mFaceConversionUtil;
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public SpannableString getExpressionString(Context context, String str) {
		SpannableString spannableString = new SpannableString(str);
		// 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
		String zhengze = "\\[[^\\]]+\\]";
		// 通过传入的正则表达式来生成一个pattern
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
			LogUtil.e(
					TAG,
					TAG + ".getExpressionString(context, str):"
							+ e.getMessage());
		}
		return spannableString;
	}

	/**
	 * 添加表情
	 * 
	 * @param context
	 * @param imgId
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, int imgId,
			String spannableString) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
		Drawable drawable = context.getResources().getDrawable(imgId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan imgSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		SpannableString strSpan = new SpannableString(spannableString);
		// 开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
		// 最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
		strSpan.setSpan(imgSpan, 0, spannableString.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		return strSpan;
	}

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws Exception
	 */
	private void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			// 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
			if (matcher.start() < start) {
				continue;
			}
			String value = mEmojiMap.get(key);
			if (TextUtils.isEmpty(value)) {
				continue;
			}
			int resId = context.getResources().getIdentifier(value, "drawable",
					context.getPackageName());
			if (resId != 0) {
				Drawable drawable = context.getResources().getDrawable(resId);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				ImageSpan imgSpan = new ImageSpan(drawable,
						ImageSpan.ALIGN_BASELINE);
				// 计算该图片名字的长度，也就是要替换的字符串的长度
				int end = matcher.start() + key.length();
				// 将该图片替换字符串中规定的位置中
				spannableString.setSpan(imgSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				if (end < spannableString.length()) {
					// 如果整个字符串还未验证完，则继续。
					dealExpression(context, spannableString, patten, end);
				}
				break;
			}
		}
	}

}