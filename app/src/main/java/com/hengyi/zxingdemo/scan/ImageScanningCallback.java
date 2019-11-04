package com.hengyi.zxingdemo.scan;

import com.google.zxing.Result;

public interface ImageScanningCallback {
    void onFinishScanning(Result result);
}
