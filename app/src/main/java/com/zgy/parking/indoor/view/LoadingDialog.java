package com.zgy.parking.indoor.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zgy.parking.indoor.R;


public class LoadingDialog extends Dialog {

	private TextView contentTV;

	public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initView(context);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, R.style.Custom_dialog);
		initView(context);
	}

	public LoadingDialog(Context context) {
		this(context, 0);
	}

	@SuppressLint("InflateParams")
	private void initView(Context context) {
		View contentView = LayoutInflater.from(context).inflate(R.layout.view_loading_dialog, null);

		contentTV = (TextView) contentView.findViewById(R.id.loading_dialog_content);

		setContentView(contentView);
	}

	public void setLoadingMessage(String content) {
		contentTV.setText(content);
	}

	public void showDialog() {
		this.show();
	}
}
