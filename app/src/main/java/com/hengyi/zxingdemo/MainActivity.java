package com.hengyi.zxingdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.input_content)
    EditText inContent;
    @BindView(R.id.show_code)
    ImageView showCode;
    @BindView(R.id.show_content)
    TextView showContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
    @OnClick({R.id.btn_generate , R.id.btn_scan})
    void onClick (View view){
        switch (view.getId()){
            case R.id.btn_generate:
                String qrContent = inContent.getText().toString();
                showCode.setImageBitmap(ZXingUtils.createQRImage(qrContent , 300 , 300));
                break;
            case R.id.btn_scan:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //权限还没有授予，需要在这里写申请权限的代码
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 60);
                } else {
                    //权限已经被授予，在这里直接写要执行的相应方法即可
                    startActivityForResult(new Intent(this , ScanAty.class) , 0x100);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 ){
            String result = data.getStringExtra("scan_code");
            showContent.setText(result);
        }
        if (requestCode == 0x100){
            String result = data.getStringExtra("result");
            showContent.setText(result);
        }
    }
}
