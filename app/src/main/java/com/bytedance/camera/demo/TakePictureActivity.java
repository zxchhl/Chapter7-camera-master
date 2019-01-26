package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
import java.util.logging.SimpleFormatter;

import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_IMAGE;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private File imgFile;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                ActivityCompat.requestPermissions(TakePictureActivity.this,new String[]{Manifest.permission.CAMERA
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE);
            } else {
                takePicture();
            }
        });

    }

    private void takePicture() {
//        Intent takePictureIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        Intent takePictureIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgFile = Utils.getOutputMediaFile1(MEDIA_TYPE_IMAGE);
        if(imgFile!=null){
            Uri fileUri=
                    FileProvider.getUriForFile(this,"com.bytedance.camera.demo",imgFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    //todo 打开相机
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras=data.getExtras();
//            Bitmap imageBitmap=(Bitmap)extras.get("data");
//
//            imageView.setImageBitmap(imageBitmap);
            setPic();
        }
    }

    private void setPic() {

        int targetW=imageView.getWidth();
        int targetH=imageView.getHeight();
        BitmapFactory.Options bmOptions=new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds=true;
        try {
            BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int photoW=bmOptions.outWidth;
        int photoH=bmOptions.outHeight;
        int scaleFactor=Math.min(photoW/targetW,photoH/targetH);
        bmOptions.inJustDecodeBounds=false;
        bmOptions.inSampleSize=scaleFactor;
        bmOptions.inPurgeable=true;
        Bitmap bitmap=BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
        imageView.setImageBitmap(bitmap);


        //todo 根据imageView裁剪
        //todo 根据缩放比例读取文件，生成Bitmap

        //todo 如果存在预览方向改变，进行图片旋转

        //todo 如果存在预览方向改变，进行图片旋转
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_DENIED&&
                        grantResults[1]==PackageManager.PERMISSION_DENIED)
                {
                    takePicture();
                }
                    //todo 判断权限是否已经授予
                break;
            }
        }
    }
}
