package myfactory.nomi.com.photoanalyzer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST= 1;
    private static final int CAPTURE_IMAGE_REQUEST= 2;
    public final static String EXTRA_INFO_IMAGE = "myfactory.nomi.com.photoanalyzer.INFO_IMAGE";
    public final static String EXTRA_INFO_CAPTURE = "myfactory.nomi.com.photoanalyzer.INFO_CAPTURE";
    public final static String INFO_CAPTURE = "myfactory.nomi.com.photoanalyzer.CAPTURE";
    public final static String APP_PATH_SD_CARD = "/bao";

    private Bitmap bm;
    private String currentDateandTime;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    public void onClickCameraCardview(View v) {
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                System.out.println("________________________"+photoURI.toString()+"__________________________________");
                mCurrentPhotoPath = photoURI.toString();

                galleryAddPic();


                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }*/


        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);

    }

    /*private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onClickGalleryCardview(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onClickAnalyzedCardview(View v) {
        Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Intent intent = new Intent(this, SelectedImageActivity.class);
            intent.putExtra(EXTRA_INFO_IMAGE, uri.toString());
            intent.putExtra(EXTRA_INFO_CAPTURE, "");
            startActivity(intent);
        }

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {

            /*System.out.println("________________________" + mCurrentPhotoPath + "__________________________________");
            Intent intent = new Intent(this, SelectedImageActivity.class);
            intent.putExtra(EXTRA_INFO_IMAGE, mCurrentPhotoPath.toString());
            startActivity(intent);*/


            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
            try {
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                currentDateandTime = sdf.format(new Date()).replace(" ","");
                OutputStream fOut = null;
               // File file = new File(fullPath, currentDateandTime);
                File file = new File(this.getExternalFilesDir(null), currentDateandTime+".jpg");
                System.out.println("=============="+file.getName()+"=============================+");
                file.createNewFile();
                System.out.println("=============="+file.getName()+"=============================+");
                fOut = new FileOutputStream(file);

                // 100 means no compression, the lower you go, the stronger the compression
                bm = (Bitmap) data.getExtras().get("data");
                bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            String pathName = this.getExternalFilesDir(null) + "/" + currentDateandTime +".jpg" ;

            System.out.println("________________________" + pathName + "__________________________________");
            Intent intent = new Intent(this, SelectedImageActivity.class);
            intent.putExtra(EXTRA_INFO_IMAGE, "");
            intent.putExtra(EXTRA_INFO_CAPTURE, pathName);
            startActivity(intent);
        }
    }
}
