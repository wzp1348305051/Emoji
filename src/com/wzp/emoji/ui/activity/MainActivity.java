package com.wzp.emoji.ui.activity;

import com.wzp.emoji.R;
import com.wzp.emoji.ui.widget.emoji.EmojiRelativeLayout;
import com.wzp.emoji.ui.widget.emoji.EmojiRelativeLayout.OnSendListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
	private MainActivity mInstance = this;
	private EmojiRelativeLayout mRlEmoji;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		mRlEmoji = (EmojiRelativeLayout) findViewById(R.id.rl_main_emoji);
		mRlEmoji.setOnSendListener(new OnSendListener() {
			
			@Override
			public void send(String text) {
				Toast.makeText(mInstance, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
