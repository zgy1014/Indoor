package com.zgy.parking.indoor.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void showMessage(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void phoneError(Context context) {
		Toast.makeText(context, "手机号不规范", Toast.LENGTH_SHORT).show();
	}

	public static void passwordError(Context context) {
		Toast.makeText(context, "密码格式错误", Toast.LENGTH_SHORT).show();
	}

	public static void smsCodeError(Context context) {
		Toast.makeText(context, "验证码不能为空", Toast.LENGTH_SHORT).show();
	}
}
