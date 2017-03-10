package com.zgy.parking.indoor.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.activity.PhotoRecordActivity;
import com.zgy.parking.indoor.adapter.PhotoImageAdapter;
import com.zgy.parking.indoor.bean.PhotoImageInfo;
import com.zgy.parking.indoor.utils.PhotoUtil;
import com.zgy.parking.indoor.view.CameraPreview;
import com.zgy.parking.indoor.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * 作者：lenovo on 2017/2/13
 *
 * 拍照记录找车
 */

public class FragmentPhoto extends Fragment implements View.OnClickListener {

    private final int PROCESS = 1;
    public static final String CAMERA_PATH_VALUE1 = "PHOTO_PATH";
    public static final String CAMERA_PATH_VALUE2 = "PATH";
    public static final String CAMERA_RETURN_PATH = "return_path";

    private int PHOTO_SIZE_W = 2000;
    private int PHOTO_SIZE_H = 2000;

    private View rootView;


    private CameraPreview preview;
    private Camera camera;

    @BindView(R.id.rl_fragment_photo_back)
    RelativeLayout mRecordBack;

    @BindView(R.id.tv_clear_photo)
    TextView mTVClear;

    @BindView(R.id.action_button)
    ImageView action_button;

    @BindView(R.id.to_record_photo)
    ImageView to_record_photo;

    @BindView(R.id.layout)
    FrameLayout mLayout;

    @BindView(R.id.surfaceView)
    SurfaceView mSurfaceView;

    @BindView(R.id.fragment_photo_viewpager)
    ViewPager mViewPager;

    @BindView(R.id.photo_point_container)
    LinearLayout mPointContainer;

    private PhotoImageAdapter pageAdapter;
    private List<PhotoImageInfo> photoList;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_photo, null);
            // 获取控件

        } else {
            ViewParent parent = rootView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(rootView);
            }
        }

        ButterKnife.bind(this, rootView);

        initView();
        InitData();


        return rootView;
    }

    private void initView() {

        mRecordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        to_record_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhotoRecordActivity.class);
                startActivity(intent);
            }
        });

        mTVClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoList.clear();
                pageAdapter.setList(photoList);
                DataSupport.deleteAll(PhotoImageInfo.class);

                pageAdapter.notifyDataSetChanged();
                to_record_photo.setVisibility(View.GONE);
                action_button.setVisibility(View.VISIBLE);
                mTVClear.setVisibility(View.GONE);
                //动态加载的点删除
                mPointContainer.removeAllViews();

              //  initDateCamera();
            }
        });

    }

    /**
     * 照片动态加载的点
     */
    private void InitDataPointContainer() {
        if(photoList !=null && photoList.size() !=0){
            mPointContainer.removeAllViews();
            if(photoList.size() !=1){
                for (int i = 0; i < photoList.size(); i++){
                    ImageView iv = new ImageView(getActivity());
                    iv.setImageResource(R.drawable.dot_current1);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i != 0) {
                        params.leftMargin = 20;
                    } else {
                        // 设置默认点
                        iv.setImageResource(R.drawable.dot_current2);
                    }
                    mPointContainer.addView(iv, params);
                }
            }

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    // 当页面被选中的时候
                    // 改变原点
                    position = position % photoList.size();
                    for (int i = 0; i < mPointContainer.getChildCount(); i++) {
                        ImageView iv = (ImageView) mPointContainer.getChildAt(i);
                        iv.setImageResource(position == i ? R.drawable.dot_current2 : R.drawable.dot_current1);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
        }

    }


    private void InitData() {
        preview = new CameraPreview(getActivity(), mSurfaceView);
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mLayout.addView(preview);
        preview.setKeepScreenOn(true);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        preview.pointFocus(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

    }

    private LoadingDialog loadingDialog;

    private void takePhoto() {
        try {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.setLoadingMessage("照片处理中...");
            loadingDialog.showDialog();

            camera.takePicture(shutterCallback, rawCallback, jpegCallback);

        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(getActivity(), "拍照失败，请重试！", Toast.LENGTH_LONG)
                    .show();
            try {
                camera.startPreview();
            } catch (Throwable e) {

            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        photoList =  DataSupport.findAll(PhotoImageInfo.class);

        initDateCamera();

        if(photoList !=null && photoList.size() !=0){
            mTVClear.setVisibility(View.VISIBLE);
            action_button.setVisibility(View.GONE);
            if(photoList.size() ==3){
                to_record_photo.setVisibility(View.GONE);
            }else{
                to_record_photo.setVisibility(View.VISIBLE);
            }
            //照片动态加载的点
            InitDataPointContainer();
        }else{

            mTVClear.setVisibility(View.GONE);
            to_record_photo.setVisibility(View.GONE);
            action_button.setVisibility(View.VISIBLE);

            if(photoList == null){
                photoList = new ArrayList<PhotoImageInfo>();
            }

        }

        pageAdapter = new PhotoImageAdapter(getActivity(), photoList);
        mViewPager.setAdapter(pageAdapter);


    }

    /**
     * 初始化可以拍照的照相机
     */
    private void initDateCamera() {

        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                // mCurrentCameraId = 0;
                camera = Camera.open();
                camera.startPreview();
                preview.setCamera(camera);
                preview.reAutoFocus();
            } catch (RuntimeException ex) {
           //     Toast.makeText(getActivity(), "未发现相机", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
            preview.setNull();
        }
        super.onPause();

    }


    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }


    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };


    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };


    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            new SaveImageTask(data).execute();
            resetCam();
        }
    };




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_button:
                takePhoto();
                break;
        }
    }



    //处理拍摄的照片
    private class SaveImageTask extends AsyncTask<Void, Void, String> {
        private byte[] data;

        SaveImageTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            // Write to SD Card
            String path = "";
            try {
                path = saveToSDCard(data);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return path;
        }


        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);

            if (!TextUtils.isEmpty(path)) {

                PhotoImageInfo imageInfo = new PhotoImageInfo();


                    imageInfo.setLocation("深圳"+" ("+"西乡街道"+")");

                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                String time = dateFormat.format(now);
                if(!TextUtils.isEmpty(time)){
                    imageInfo.setTime(time);
                }

                imageInfo.setCover(path);
                photoList.add(imageInfo);
                DataSupport.saveAll(photoList);
                pageAdapter.setList(photoList);
                pageAdapter.notifyDataSetChanged();
                to_record_photo.setVisibility(View.VISIBLE);
                action_button.setVisibility(View.GONE);
                mTVClear.setVisibility(View.VISIBLE);


                loadingDialog.dismiss();

            } else {
                Toast.makeText(getActivity(), "拍照失败，请稍后重试！",
                        Toast.LENGTH_LONG).show();
            }
        }
    }



    Bitmap croppedImage;

    /**
     * 将拍下来的照片存放在SD卡中
     */
    public String saveToSDCard(byte[] data) throws IOException {

        // 获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // PHOTO_SIZE = options.outHeight > options.outWidth ? options.outWidth
        // : options.outHeight;
        PHOTO_SIZE_W = options.outWidth;
        PHOTO_SIZE_H = options.outHeight;
        options.inJustDecodeBounds = false;
        Rect r = new Rect(0, 0, PHOTO_SIZE_W, PHOTO_SIZE_H);
        try {
            croppedImage = decodeRegionCrop(data, r);
        } catch (Exception e) {
            return null;
        }
        String imagePath = "";
        try {
            //   imagePath = saveToFile(croppedImage);
            path = System.currentTimeMillis() + ".jpg";
            imagePath = PhotoUtil.saveBitImageUrl(croppedImage, path);

        } catch (Exception e) {

        }
        croppedImage.recycle();
        return imagePath;
    }

    public String path;

    private Bitmap decodeRegionCrop(byte[] data, Rect rect) {
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is,false);
            try {
                croppedImage = decoder.decodeRegion(rect,
                        new BitmapFactory.Options());
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
//        Matrix m = new Matrix();
//        m.setRotate(90, PHOTO_SIZE_W / 2, PHOTO_SIZE_H / 2);
//
//        Bitmap rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0,
//                PHOTO_SIZE_W, PHOTO_SIZE_H, m, true);

        int mWidth = croppedImage.getWidth();
        int mHeight = croppedImage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 1;
        float scaleHeight = 1;

        if (mWidth <= mHeight) {
            scaleWidth = (float) (320 / mWidth);
            scaleHeight = (float) (180 / mHeight);
        } else {
            scaleWidth = (float) (180 / mWidth);
            scaleHeight = (float) (320 / mHeight);
        }
      //  matrix.postScale(scaleWidth, scaleHeight);
        matrix.setRotate(90, scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(croppedImage, 0, 0, mWidth, mHeight, matrix, true);


        if (newBitmap != newBitmap)
            croppedImage.recycle();
        return newBitmap;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROCESS) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                if (data != null) {
                    intent.putExtra(CAMERA_RETURN_PATH,
                            data.getStringExtra(CAMERA_PATH_VALUE2));
                }
                getActivity().setResult(RESULT_OK, intent);

            } else {
                if (data != null) {
                    File dir = new File(data.getStringExtra(CAMERA_PATH_VALUE2));
                    if (dir != null) {
                        dir.delete();
                    }
                }
            }
        }
    }

}
