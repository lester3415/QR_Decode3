package com.example.lester.qr_decode3;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.journeyapps.barcodescanner.CaptureActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CaptureActivityAnyOrientation extends CaptureActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //6.0以上，檢查相機與儲存空間權限
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        //setContentView(R.layout.activity_capture_activity_any_orientation);
    }
}