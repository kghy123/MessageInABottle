package com.kghy1234gmail.messagesinabottle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageSelectActivity extends AppCompatActivity {

    String fileName;

    String currentPhotoPath;
    String filePhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode==RESULT_CANCELED) return;

                getIntent().putExtra("imgPath", data.getData().toString());
                getIntent().setData(data.getData());
                Log.d("uri", data.getData().toString());
                setResult(RESULT_OK, getIntent());
                finish();


                break;

            case 20:

                if(resultCode==RESULT_CANCELED) {

                    File file = new File(currentPhotoPath);
                    file.delete();
                    galleryAddPic();
                    return;
                }


                getIntent().setData(Uri.parse(currentPhotoPath));

                galleryAddPic();
                setResult(10, getIntent());
                finish();




                break;

            case 30:


                break;
        }

    }

    public byte[] getBytesFromBitmap(Bitmap bm){

        byte[] bytes = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
        bytes = os.toByteArray();


        return bytes;
    }

    public void clickGoToGallery(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 10);
    }

    public void clickGoToCamera(View v){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(photoFile != null){
            Uri photoUri = FileProvider.getUriForFile(this, "com.kghy1234gmail.messagesinabottle", photoFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            Log.d("photoFile", "uri 생성");
        }

        startActivityForResult(intent, 20);

    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MessageInABottle/");

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("file", "이미지 파일 생성");

        currentPhotoPath = image.getAbsolutePath();
        filePhotoPath = "file://" + currentPhotoPath;

        Log.d("currentPhotoPath", currentPhotoPath);

        return image;
    }

    public void galleryAddPic(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);

        Uri contentUri = Uri.fromFile(f);
        Log.d("contentUri", contentUri.toString());
        intent.setData(contentUri);
        sendBroadcast(intent);
        Log.d("broadcase","성공");
    }


}
