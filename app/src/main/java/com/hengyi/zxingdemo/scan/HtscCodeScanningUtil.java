package com.hengyi.zxingdemo.scan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;

public class HtscCodeScanningUtil {
    public static Result scanImage(Uri uri , Context context) {
        Result result = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            bitmap = getSmallerBitmap(bitmap);
            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                Reader reader = new QRCodeReader();
                try {
                    result = reader.decode(binaryBitmap);
                } catch (NotFoundException | ChecksumException | FormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Bitmap getSmallerBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int size = bitmap.getWidth() * bitmap.getHeight() / 160000;
            if (size <= 1) {
                return bitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postScale((float) (1 / Math.sqrt(size)), (float) (1 / Math.sqrt(size)));
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        }
        return null;
    }
}
