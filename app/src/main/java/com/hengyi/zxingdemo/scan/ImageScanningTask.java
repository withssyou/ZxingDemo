package com.hengyi.zxingdemo.scan;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.zxing.Result;
import com.hengyi.zxingdemo.scan.HtscCodeScanningUtil;
import com.hengyi.zxingdemo.scan.ImageScanningCallback;

public class ImageScanningTask  extends AsyncTask<Uri, Void, Result> {
    private Uri uri;
    private Context context;
    private ImageScanningCallback callback;

    public ImageScanningTask(Uri uri , Context context, ImageScanningCallback callback) {
        this.uri = uri;
        this.callback = callback;
        this.context = context;
    }
    @Override
    protected Result doInBackground(Uri... params) {
        return HtscCodeScanningUtil.scanImage(uri , context);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        callback.onFinishScanning(result);
    }
}
