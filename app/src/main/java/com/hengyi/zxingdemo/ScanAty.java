package com.hengyi.zxingdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;
import com.hengyi.zxingdemo.scan.ImageScanningCallback;
import com.hengyi.zxingdemo.scan.ImageScanningTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;

public class ScanAty extends AppCompatActivity implements QRCodeView.Delegate  {
    @BindView(R.id.scan_view)
    QRCodeView mQRCodeView;

    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private static final int REQUEST_CODE_PICK_IMAGE = 0x12;
    private static final int RESULT_CODE_PICK_IMAGE = 0x14;

    public static final int REQUEST_CODE_SUCCESS = 200;
    public static final String SCAN_CODE = "scan_code";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_scan);
        ButterKnife.bind(this);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        mQRCodeView.startSpot();
        Intent intent = new Intent();
        intent.putExtra(SCAN_CODE, result);
        setResult(REQUEST_CODE_SUCCESS, intent);
        this.finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this , "打开相机出错" , Toast.LENGTH_SHORT).show();
    }

    @OnClick({/*R.id.light, */R.id.choose_picture})
    void onCustomClick(View view) {
        switch (view.getId()) {
//            case R.id.light:
//                String str = light.getText().toString();
//                if ("开灯".equals(str)) {
//                    mQRCodeView.openFlashlight();
//                    light.setText("关灯");
//                } else {
//                    mQRCodeView.closeFlashlight();
//                    light.setText("开灯");
//                }
//                break;
            case R.id.choose_picture:
                int checked = ContextCompat.checkSelfPermission(this
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checked == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                ActivityCompat.requestPermissions(this
                        , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} , 0x10);
            }
            break;
        }
    }
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zxing_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bar_code:
                mQRCodeView.changeToScanQRCodeStyle();
                break;
            case R.id.sweep_bar_code:
                mQRCodeView.changeToScanBarcodeStyle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 0x12){
            mQRCodeView.showScanRect();
            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
                final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);
            /*
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        return QRCodeDecoder.syncDecodeQRCode(picturePath);
                    }
                    @Override
                    protected void onPostExecute(String result) {
                        if (TextUtils.isEmpty(result)) {
                            Toast.makeText(ScanAty.this ,"未发现二维码"  , Toast.LENGTH_SHORT).show();
                        } else {
                            onScanQRCodeSuccess(result);
                        }
                    }
                }.execute();
            }
        }

        if (Activity.RESULT_OK == resultCode && null != data) {
            Uri uri = data.getData();
            ImageScanningTask scanningTask = new ImageScanningTask(uri, this , new ImageScanningCallback() {
                        @Override
                        public void onFinishScanning(Result result) {
                            if (result != null) {
                                Intent intent = new Intent();
                                intent.putExtra("result", result.getText());
                                setResult(RESULT_CODE_PICK_IMAGE, intent);
                                finish();
                            } else {
                                // 识别失败
                            }
                        }
                    });
            scanningTask.execute();
        } else {
            // 什么也没有
        }
    }
}
