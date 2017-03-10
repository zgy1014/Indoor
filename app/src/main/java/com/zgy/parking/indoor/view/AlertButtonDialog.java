package com.zgy.parking.indoor.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.zgy.parking.indoor.R;


public class AlertButtonDialog {
    private Context      context;
    private Dialog       dialog;
    private LinearLayout lLayout_bg;
    private TextView     txt_title;
    private TextView     txt_msg;
    private Button       btn_neg;
    private Button       btn_pos;
    private ImageView    img_line;
    private Display      display;
    private boolean showTitle  = false;
    private boolean showMsg    = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private ImageView    lineView;
    private LinearLayout btnLayoutLL;

    public AlertButtonDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public void setButtonInVisiable() {
        btn_neg.setVisibility(View.GONE);
        btn_pos.setVisibility(View.GONE);
        lineView.setVisibility(View.GONE);
        btnLayoutLL.setVisibility(View.GONE);
        showNegBtn = true;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    public AlertButtonDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context)
                                  .inflate(R.layout.view_alertdialog, null);

        // 获取自定义Dialog布局中的控件
        btnLayoutLL = (LinearLayout) view.findViewById(R.id.dialog_btn_layout);
        lineView = (ImageView) view.findViewById(R.id.dialog_content_btn_line);
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.8),
                                                                LayoutParams.WRAP_CONTENT));
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    if (singlerClickListener != null) {
                        singlerClickListener.onClick(null);
                    }
                }
                return false;
            }
        });
        return this;
    }

    public AlertButtonDialog setBtnLayoutInvisible() {
        btnLayoutLL.setVisibility(View.GONE);
        lineView.setVisibility(View.GONE);
        return this;
    }

    public AlertButtonDialog setContentTextColor(String color) {
        txt_msg.setTextColor(Color.parseColor(color));
        return this;
    }
    public AlertButtonDialog setTitleTextColor(String color) {
        txt_title.setTextColor(Color.parseColor(color));
        return this;
    }

    public AlertButtonDialog setContentTextGravity(int gravity) {
        txt_msg.setGravity(gravity);
        return this;
    }


    public AlertButtonDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public AlertButtonDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public void disMissDialog() {
        dialog.dismiss();
    }

    public AlertButtonDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public AlertButtonDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public AlertButtonDialog setPositiveButton(String text, final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public AlertButtonDialog setNegativeButton(String text, final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private OnClickListener singlerClickListener;

    public AlertButtonDialog setSingleButton(OnClickListener listener) {
        this.singlerClickListener = listener;
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (singlerClickListener != null) {
                        singlerClickListener.onClick(v);
                    }
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }
}
