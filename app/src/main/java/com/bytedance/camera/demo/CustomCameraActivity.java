package com.bytedance.camera.demo;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.bytedance.camera.demo.utils.Utils.getOutputMediaFile1;

public class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);
        mCamera = getCamera(CAMERA_TYPE);
        mSurfaceView = findViewById(R.id.img);
        SurfaceHolder surfaceHolder=mSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera=null;

            }
        });


        //todo 给SurfaceHolder添加Callback

        findViewById(R.id.btn_picture).setOnClickListener(v -> {

            mCamera.takePicture(null,null,mPicture);
//
            Camera.PictureCallback mPicture=(data,camera)->{
                File pictureFile=getOutputMediaFile1(MEDIA_TYPE_IMAGE);
                try{
                    FileOutputStream fos=new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                }catch (IOException e){
                    Log.d("mPicture","Error accessing file:"+e.getMessage());
                }
                mCamera.startPreview();
            };
            //todo 拍一张照片
        });

                findViewById(R.id.btn_record).setOnClickListener(v -> {



                    //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording) {
                mMediaRecorder.stop();
                releaseMediaRecorder();
                isRecording=false;
                //todo 停止录制
            } else {
                if(prepareVideoRecorder()){
                    //mMediaRecorder.start();

                    isRecording=true;
                }
                else
                {
//                    releaseMediaRecorder();
                    isRecording=false;

                }

                //todo 录制
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(v -> {



            if(mCamera!=null)
                {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera=null;
                }

//                mCamera=Camera.open(CAMERA_TYPE);



//                mCamera.open(Camera.CameraInfo.CAMERA_FACING_FRONT/CAMERA_TYPE);


            //todo 切换前后摄像头
        });

        findViewById(R.id.btn_zoom).setOnClickListener(v -> {
            //todo 调焦，需要判断手机是否支持
        });
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);
         cam.setDisplayOrientation(getCameraDisplayOrientation(position));

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等

        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        if(mCamera!=null)
        {
            mCamera.release();
            mCamera=null;
        }
        //todo 释放camera资源
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {

        //todo 开始预览
    }


    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {

        mMediaRecorder=new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(getOutputMediaFile1(MEDIA_TYPE_IMAGE).toString());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);
        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }catch (Exception e)
        {
            releaseMediaRecorder();
            return false;
        }
        //todo 准备MediaRecorder

        return true;
    }


    private void releaseMediaRecorder() {
        if(mMediaRecorder!=null)
        {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder=null;
            mCamera.lock();
        }
        //todo 释放MediaRecorder
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera!=null)
        {
            mCamera.release();
            mCamera=null;
        }
        if(mMediaRecorder!=null)
        {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder=null;
            mCamera.lock();
        }
        //todo 释放Camera和MediaRecorder资源
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile1(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}
