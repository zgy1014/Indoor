package com.zgy.parking.indoor.utils;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 拍照辅助工具类
 *
 * @author zoupeng@huaxunchina.cn
 *         <p>
 *         2015-6-16 下午8:41:19
 */
public class PhotoUtil {
    public static final String TAG = "PhotoUtil";
    public static final int PHOTO = 1;
    public static final int TAKE_PICTURE = 2;
    public static final int UPLOAD_TAKE_PICTURE = 3;
    public static final int SELECT_PICTURE = 4;
    public static final int CUT_OK = 5;
    public static String SDPATH = Environment.getExternalStorageDirectory() + "/indoor/images/";


    public static File createSDDir(String dirName)
            throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdirs());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }




    public static String saveBitImageUrl(Bitmap bm, String picName) {
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
            return f.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
    }






}
