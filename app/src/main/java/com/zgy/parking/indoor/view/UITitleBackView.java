package com.zgy.parking.indoor.view;

/*
 *  @项目名：  pakinglog
 *  @包名：    com.odin.parkinglog.view
 *  @文件名:   UITitleBackView
 *  @创建者:   odin
 *  @创建时间:  2016/9/24 12:47
 *  @描述：    TODO
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zgy.parking.indoor.R;


public class UITitleBackView
        extends LinearLayout
        implements View.OnClickListener
{
    private View divideview;
    private ImageView mBackImage;
    private Activity activity;
    private TextView mTitle;
    private int falg = 1;

    private FrameLayout mFrameLayout;
    private TextView    mRightTextView;
    private Button      mRightBtnView;
    private ImageView   mRightImageView;
    private final static int BUTTONVISIABLE = 0;
    private final static int IMAGEVISIABLE  = 1;
    private final static int TEXTVISIABLE   = 2;
    private int                          mWhichvisiable;
    // private boolean mUibUndefineBtnVisiable;
    private ImageView                    mUndefineImageBtn;
    private onContainerClickListener     mContainerListener;
    private onUndefineBtnClickListener   mUndefineListener;
    private onBackImageClickListener     mBackImageListener;
    private OnRightTextViewClickListener mRightTextViewClickListener;
    private RelativeLayout               mLayout;

    public UITitleBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this.activity = (Activity) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.title_navigation, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UITitleBackView);

        mWhichvisiable = ta.getInt(R.styleable.UITitleBackView_uibRightView, BUTTONVISIABLE);
        // mUibUndefineBtnVisiable = ta.getBoolean(
        // R.styleable.UITitleBackView_uibUndefineBtn, false);
        // 加载三个布局

        mRightTextView = (TextView) View.inflate(context, R.layout.uib_right_textview, null);
        mRightBtnView = (Button) View.inflate(context, R.layout.uib_right_buttonview, null);
        mRightImageView = (ImageView) View.inflate(context, R.layout.uib_right_imageview, null);

        mUndefineImageBtn = (ImageView) findViewById(R.id.uib_imgbtn_undefine);
        mBackImage = (ImageView) findViewById(R.id.uib_left_back_image);
        mTitle = (TextView) findViewById(R.id.uib_mid_title_txt);
        mFrameLayout = (FrameLayout) findViewById(R.id.uib_container);
        divideview = findViewById(R.id.uib_divideview);

        mLayout = (RelativeLayout) findViewById(R.id.title_navigation_background);
        addRightView();
        mUndefineImageBtn.setOnClickListener(this);
        mFrameLayout.setOnClickListener(this);
        mBackImage.setOnClickListener(this);
        mRightTextView.setOnClickListener(this);
        ta.recycle();
    }

    /**
     * 设置左边返回键是否可见
     *
     * @param isBackImageVisiable
     *            默认可见
     */
    public void setBackImageVisiale(boolean isBackImageVisiable) {
        mBackImage.setVisibility(isBackImageVisiable
                                 ? View.VISIBLE
                                 : View.GONE);
    }




    /**
     * 设置右边文字颜色
     *
     * @param
     *
     */
    public void setRightTextViewColor(int color) {
        mRightTextView.setTextColor(color);
    }

    /**
     * 设置背景颜色 默认#06c1ae
     *
     * @param color
     */
    public void setBackGroundColor(int color) {
        mLayout.setBackgroundColor(color);
    }


    /**
     * 设置中间字体的文字
     *
     * @param text
     */
    public void setTitleText(CharSequence text) {
        mTitle.setText(text);
    }

    public void setAddActivty(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置右边的button的背景
     *
     * @param background
     */
    public void setBtnBackGround(Drawable background) {

        mRightBtnView.setBackgroundDrawable(background);
    }

    /**
     * 设置右二的ImageView的图片
     *
     * @param image
     */
    public void setRigthImageView(Drawable image) {
        mRightImageView.setImageDrawable(image);
    }

    /**
     * 设置右二内容是否显示,默认显示
     *
     * @param
     */
    public void setRightContentVisbile(boolean isVisiable) {
        mFrameLayout.setVisibility(isVisiable
                                   ? View.VISIBLE
                                   : View.GONE);
    }

    /**
     * 设置右一的ImageView的图片
     *
     * @param image
     */
    public void setUndefineImageView(Drawable image) {
        mUndefineImageBtn.setImageDrawable(image);
    }

    /**
     * 设置右一图片是否显示,默认隐藏
     */
    public void setUndefineBtnVisiable(boolean isVisiable) {
        mUndefineImageBtn.setVisibility(isVisiable
                                        ? View.VISIBLE
                                        : View.GONE);
    }

    /**
     * 设置右边的文本的内容
     *
     * @param rightText
     */
    public void setRigthTextView(String rightText) {
        mRightTextView.setText(rightText);
    }

    /**
     * 添加右边的布局
     */
    public void addRightView() {
        switch (mWhichvisiable) {
            case BUTTONVISIABLE:
                mFrameLayout.addView(mRightBtnView);

                break;
            case IMAGEVISIABLE:
                mFrameLayout.addView(mRightImageView);

                break;
            case TEXTVISIABLE:
                mFrameLayout.addView(mRightTextView);
                break;
            default:
                break;
        }
    }

    public int getFalg() {
        return falg;
    }

    public void setFalg(int falg) {
        this.falg = falg;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uib_left_back_image:
                if (activity != null) {
                    activity.finish();
                    //activity.overridePendingTransition(R.anim.new_push_left_in,R.anim.new_push_left_out);// 从右向左推出动画效果
                }
                if (mBackImageListener != null) {
                    mBackImageListener.onBackImageClick();
                }
                break;
            case R.id.uib_imgbtn_undefine:
                if (mUndefineListener != null) {
                    mUndefineListener.onUndefineBtnClick();
                }
                break;
            case R.id.uib_container:
                if (mContainerListener != null) {
                    mContainerListener.onContainerClick();
                }
                break;
            case R.id.uib_subtextview:
                System.out.println("");
                if (mRightTextViewClickListener != null) {
                    mRightTextViewClickListener.onRightTextViewClick();
                }
                break;
            default:
                break;
        }

    }

    public void setOnContainerClickListener(onContainerClickListener listener) {

        this.mContainerListener = listener;
    }

    public void setOnUndefineBtnClick(onUndefineBtnClickListener listener) {

        this.mUndefineListener = listener;
    }

    public void setOnBackImageClickListener(onBackImageClickListener listener) {

        this.mBackImageListener = listener;
    }

    public void setOnRightTextViewClickListener(OnRightTextViewClickListener listener) {
        this.mRightTextViewClickListener = listener;
    }


    /**
     * 添加右二的按钮接口
     *
     * @author xiezhuolin
     *
     */
    public interface onContainerClickListener {
        public void onContainerClick();
    }

    /**
     * 添加右一得按钮接口
     *
     * @author xiezhuolin
     *
     */
    public interface onUndefineBtnClickListener {
        public void onUndefineBtnClick();
    }

    /**
     * 返回键
     *
     * @author xiezhuolin
     *
     */
    public interface onBackImageClickListener {
        public void onBackImageClick();
    }

    /**
     *
     * @author jiangyichao
     *
     */
    public interface OnRightTextViewClickListener {
        public void onRightTextViewClick();
    }
}

