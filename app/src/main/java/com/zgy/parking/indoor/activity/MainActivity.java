package com.zgy.parking.indoor.activity;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.utils.IPermission;
import com.zgy.parking.indoor.utils.LogUtils;

import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text = (TextView)findViewById(R.id.activity_main);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permission = new String[]{Manifest.permission.CAMERA};
                requestRunTimePermission(permission, new PermisionListenr());

                Intent intent = new Intent(MainActivity.this, ParkingFindCarActivity.class);
                startActivity(intent);
            }
        });
    }


    private class PermisionListenr
            implements IPermission {
        @Override
        public void onGranted() {
            LogUtils.e("test", "onGranted");
        }

        @Override
        public void onDenied(List<String> deniedPermission) {
            LogUtils.e("test", "onDenied");
        }
    }


}
