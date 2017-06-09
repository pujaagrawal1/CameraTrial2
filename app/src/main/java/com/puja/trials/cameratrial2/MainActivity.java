package com.puja.trials.cameratrial2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import junit.runner.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    public static int cameraID = 0;
    private final int firstInterval = 3000; //5 sec
    private final int secondInterval = 15000; //15 sec
    private final int thirdInterval = 30000; //30 sec
    private final int fourthInterval = 60000; //60 sec
    static int picCount = 0;
    private Handler cameraHandler = new Handler();

    private Runnable cameraRunnable = new Runnable(){
        public void run() {

            Log.d("Trial", picCount + " - Switching Camera Modes");

            if (picCount <= 10) {
                if (picCount > 0 && picCount % 2 == 0) {
                    backCamera();

                }else if (picCount % 2 == 1){
                    frontCamera();
                }
                if (picCount == 10)
                    cameraHandler.postDelayed(cameraRunnable, secondInterval);
                else
                    cameraHandler.postDelayed(cameraRunnable, firstInterval);
            } else if (picCount <= 20) {
                if (picCount > 0 && picCount % 2 == 0) {
                    backCamera();

                }else if (picCount % 2 == 1){
                    frontCamera();
                }
                if (picCount == 20)
                    cameraHandler.postDelayed(cameraRunnable, thirdInterval);
                else
                    cameraHandler.postDelayed(cameraRunnable, secondInterval);

            } else if (picCount <= 30) {
                if (picCount > 0 && picCount % 2 == 0) {
                    backCamera();

                }else if (picCount % 2 == 1){
                    frontCamera();
                }
                if (picCount == 30){
                    cameraHandler.postDelayed(cameraRunnable, fourthInterval);
                    //   cameraHandler.removeCallbacks(cameraRunnable);
                }else
                    cameraHandler.postDelayed(cameraRunnable, thirdInterval);
            } else if (picCount < 50) {
                if (picCount > 0 && picCount % 2 == 0) {
                    backCamera();

                }else if (picCount % 2 == 1){
                    frontCamera();
                }
                cameraHandler.postDelayed(cameraRunnable, fourthInterval);
            }
            if (picCount == 50){
                cameraHandler.removeCallbacks(cameraRunnable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backCamera();
        cameraHandler.postDelayed(cameraRunnable, firstInterval);
    }

    public void frontCamera(){
        cameraID = 1;
        picCount++;
        startCamera();
    }

    public void backCamera() {
        cameraID = 0;
        picCount++;
        startCamera();
    }

    private void startCamera() {

        Camera mCamera = Camera.open(cameraID);
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(10));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(640, 480);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setPictureFormat(ImageFormat.JPEG);
        mCamera.setParameters(params);
        mCamera.startPreview();
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    Log.d("Trial", "onPictureTaken - wrote bytes: " + data.length);

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    Bitmap bitmap= BitmapFactory.decodeByteArray(data, 0, data.length,opts);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = 300;
                    int newHeight = 300;
                    // calculate the scale - in this case = 0.4f
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    matrix.postRotate(90);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    String partFilename = "frontCapture_" + String.valueOf(System.currentTimeMillis()) + "_" ;

                    storeCameraPhotoInSDCard(resizedBitmap, partFilename);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally
                {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    Toast.makeText(getApplicationContext(), "Image snapshot Done",Toast.LENGTH_SHORT).show();

                }
                Log.d("Trial", "onPictureTaken - jpeg");
            }
        });
    }


    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "/appdownloads/frontCapture_" + currentDate + ".jpg");
        //   File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "photo_" + currentDate + ".jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
