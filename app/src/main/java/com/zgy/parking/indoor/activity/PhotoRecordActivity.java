package com.zgy.parking.indoor.activity;


import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.bean.PhotoImageInfo;
import com.zgy.parking.indoor.utils.PhotoUtil;
import com.zgy.parking.indoor.view.CameraPreview;
import com.zgy.parking.indoor.view.LoadingDialog;
import com.zgy.parking.indoor.view.UITitleBackView;

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
import butterknife.OnClick;

public class PhotoRecordActivity
        extends BaseActivity implements UITitleBackView.onBackImageClickListener {



    @BindView(R.id.photo_record_button)
    ImageView photoRecordButton;

    @BindView(R.id.layout)
    FrameLayout mLayout;

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;

    @BindView(R.id.photo_record_uib)
    UITitleBackView mUib;

    private final int PROCESS = 1;
    public static final String CAMERA_PATH_VALUE1 = "PHOTO_PATH";
    public static final String CAMERA_PATH_VALUE2 = "PATH";
    public static final String CAMERA_RETURN_PATH = "return_path";

    private int PHOTO_SIZE_W = 2000;
    private int PHOTO_SIZE_H = 2000;



    private CameraPreview preview;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_record);
        // 获取控件
        ButterKnife.bind(this);
        setStatusBlack(this);
        initHeader();
        InitData();

    }

    private void initHeader() {
        mUib.setRightContentVisbile(false);
        mUib.setBackImageVisiale(true);
        mUib.setOnBackImageClickListener(this);
        mUib.setTitleText("反向寻车");

    }

    private void InitData() {
        preview = new CameraPreview(this, surfaceView);
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mLayout.addView(preview);
        preview.setKeepScreenOn(true);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
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


    @OnClick(R.id.photo_record_button)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_record_button:
                takePhoto();
                break;
        }
    }

    private void takePhoto() {
        try {

            loadingDialog = new LoadingDialog(PhotoRecordActivity.this);
            loadingDialog.setLoadingMessage("照片处理中...");
            loadingDialog.showDialog();
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);

        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(this, "拍照失败，请重试！", Toast.LENGTH_LONG)
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
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                // mCurrentCameraId = 0;
                camera = Camera.open();
                camera.startPreview();
                preview.setCamera(camera);
                preview.reAutoFocus();
            } catch (RuntimeException ex) {
                Toast.makeText(this, "未发现相机", Toast.LENGTH_LONG).show();
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
    public void onBackImageClick() {
        finish();
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

                loadingDialog.dismiss();

                finish();

            } else {
                Toast.makeText(PhotoRecordActivity.this, "拍照失败，请稍后重试！",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private List<PhotoImageInfo> photoList = new ArrayList<>();

    private LoadingDialog loadingDialog;

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
        Matrix m = new Matrix();
        m.setRotate(90, PHOTO_SIZE_W / 2, PHOTO_SIZE_H / 2);

        Bitmap rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0,
                PHOTO_SIZE_W, PHOTO_SIZE_H, m, true);
        if (rotatedImage != croppedImage)
            croppedImage.recycle();
        return rotatedImage;
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
               setResult(RESULT_OK, intent);

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
