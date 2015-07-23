package com.wzp.emoji.ui.widget.emoji;

import java.util.ArrayList;
import java.util.List;

import com.wzp.emoji.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 自定义Emoji控件，通过配置asserts文件夹中emoji.txt文件，设置emoji图标，
 * 设置setOnSendListener实现发送按钮的监听，设置setOnCorpusSelectedListener实现表情按钮监听
 * */
public class EmojiRelativeLayout extends RelativeLayout implements
		OnItemClickListener, OnClickListener {
	private EmojiRelativeLayout mInstance = this;
	private Context mContext;
	private View mView;
	/** 表情按钮 */
	private ImageView mIvFace;
	/** 输入框 */
	private EditText mEtInput;
	/** 发送按钮 */
	private Button mBtnSend;
	/** 表情区域 */
	private RelativeLayout mRlFace;
	/** 显示表情页的viewpager */
	private ViewPager mVpFace;
	/** 游标显示布局 */
	private LinearLayout mLlDots;
	/** 表情页集合 */
	private List<View> mFaceViews;
	/** 游标点集合 */
	private List<ImageView> mDotViews;
	/** 表情集合,内部List代表每一页的表情集合，外部List是页的集合 */
	private List<List<Emoji>> mEmojis;
	/** 表情页的监听事件 */
	private OnFaceSelectedListener mFaceSelectedListener;
	/** 发送按钮的监听事件 */
	private OnSendListener mSendListener;
	private FaceConversionUtil mFaceConversionUtil;

	/**
	 * 表情选择监听
	 */
	public interface OnFaceSelectedListener {
		/**
		 * 表情被选择
		 * */
		public void onFaceSelected(Emoji emoji);

		/**
		 * 表情被删除
		 * */
		public void onFaceDeleted();
	}

	/**
	 * 发送按钮监听接口
	 * */
	public interface OnSendListener {

		/**
		 * 发送按钮处理方法
		 * 
		 * @param text
		 *            :输入框中的内容
		 * */
		public void send(String text);
	}

	public void setOnCorpusSelectedListener(OnFaceSelectedListener listener) {
		mFaceSelectedListener = listener;
	}

	public void setOnSendListener(OnSendListener listener) {
		mSendListener = listener;
	}

	public EmojiRelativeLayout(Context context) {
		super(context);
		mContext = context;
		mFaceConversionUtil = FaceConversionUtil.getInstance(context);
	}

	public EmojiRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mFaceConversionUtil = FaceConversionUtil.getInstance(context);
	}

	public EmojiRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mFaceConversionUtil = FaceConversionUtil.getInstance(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initView();
	}

	private void initView() {
		mView = LayoutInflater.from(mContext).inflate(R.layout.emoji,
				mInstance, true);
		mIvFace = (ImageView) mView.findViewById(R.id.iv_emoji_face);
		mEtInput = (EditText) mView.findViewById(R.id.et_emoji_input);
		mBtnSend = (Button) mView.findViewById(R.id.btn_emoji_send);
		mRlFace = (RelativeLayout) mView.findViewById(R.id.rl_emoji_face);
		mVpFace = (ViewPager) mView.findViewById(R.id.vp_emoji_face);
		mLlDots = (LinearLayout) mView.findViewById(R.id.ll_emoji_dots);

		mIvFace.setOnClickListener(mInstance);
		mEtInput.setOnClickListener(mInstance);
		mBtnSend.setOnClickListener(mInstance);
		initViewPager();
		initDots();
	}

	/**
	 * 初始化ViewPager
	 * */
	private void initViewPager() {
		mEmojis = mFaceConversionUtil.getEmojiLists();
		mFaceViews = new ArrayList<View>();
		for (int i = 0; i < mEmojis.size(); i++) {
			GridView gvPage = new GridView(mContext);
			FaceAdapter adapter = new FaceAdapter(mContext, mEmojis.get(i));
			gvPage.setAdapter(adapter);
			gvPage.setOnItemClickListener(mInstance);
			gvPage.setVerticalScrollBarEnabled(false);
			gvPage.setNumColumns(7);
			gvPage.setBackgroundColor(Color.TRANSPARENT);
			gvPage.setHorizontalSpacing(1);
			gvPage.setVerticalSpacing(1);
			gvPage.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			gvPage.setCacheColorHint(0);
			gvPage.setPadding(5, 0, 5, 0);
			gvPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
			gvPage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			gvPage.setGravity(Gravity.CENTER);
			mFaceViews.add(gvPage);
		}
		mVpFace.setAdapter(new EmojiPagerAdapter(mFaceViews));
		mVpFace.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				for (int i = 0; i < mDotViews.size(); i++) {
					if (i == position) {
						mDotViews.get(i).setSelected(true);
					} else {
						mDotViews.get(i).setSelected(false);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mVpFace.setCurrentItem(0);
	}

	/**
	 * 初始化底部小圆点
	 * */
	private void initDots() {
		mDotViews = new ArrayList<ImageView>();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		for (int i = 0; i < mEmojis.size(); i++) {
			ImageView dot = (ImageView) inflater.inflate(R.layout.dot,
					null);
			mLlDots.addView(dot);
			mDotViews.add(dot);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_emoji_face:
			if (mRlFace.getVisibility() == View.GONE) {
				mRlFace.setVisibility(View.VISIBLE);
				mView.setAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.push_bottom_in));
			} else if (mRlFace.getVisibility() == View.VISIBLE) {
				mRlFace.setVisibility(View.GONE);
				mView.setAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.push_bottom_out));
			}
			/** 隐藏软键盘 */
			InputMethodManager manager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(mEtInput.getWindowToken(), 0);
			break;
		case R.id.et_emoji_input:
			hideFaceView();
			break;
		case R.id.btn_emoji_send:
			hideFaceView();
			String text = mEtInput.getText().toString();
			mEtInput.setText("");
			mSendListener.send(text);
			break;
		}
	}

	/**
	 * 隐藏表情页
	 * */
	public void hideFaceView() {
		if (mRlFace.getVisibility() == View.VISIBLE) {
			mRlFace.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Emoji emoji = (Emoji) parent.getItemAtPosition(position);
		/** 删除表情 */
		if (emoji.getId() == R.drawable.ic_delete) {
			if (mFaceSelectedListener != null) {
				mFaceSelectedListener.onFaceDeleted();
			}
			int selection = mEtInput.getSelectionStart();
			String text = mEtInput.getText().toString();
			if (selection > 0) {
				String temp = text.substring(selection - 1);
				if ("]".equals(temp)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					mEtInput.getText().delete(start, end);
					return;
				}
				mEtInput.getText().delete(selection - 1, selection);
			}
		}
		/** 添加表情 */
		if (!TextUtils.isEmpty(emoji.getTxt())) {
			if (mFaceSelectedListener != null) {
				mFaceSelectedListener.onFaceSelected(emoji);
			}
			SpannableString spannableString = mFaceConversionUtil.addFace(
					getContext(), emoji.getId(), emoji.getTxt());
			mEtInput.append(spannableString);
		}

	}

}
