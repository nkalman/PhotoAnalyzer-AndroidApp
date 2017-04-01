package myfactory.nomi.com.photoanalyzer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class SelectedImageActivity extends AppCompatActivity {

    private Uri uriToImage;
    Button btnStartProgress;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    private long fileSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_image);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        View someView = findViewById(R.id.activity_selected_image);
        View root = someView.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.black));


        Intent intent = getIntent();

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (intent.getType().startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
        else if (intent.getStringExtra(MainActivity.EXTRA_INFO_CAPTURE).equals("")) {
            Uri uri =  Uri.parse(intent.getStringExtra(MainActivity.EXTRA_INFO_IMAGE));
            uriToImage = uri;
            System.out.println("________________________" + uri.toString() + "__________________________________elso eset, galerias");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (intent.getStringExtra(MainActivity.EXTRA_INFO_IMAGE).equals("")) {
            String pathName = intent.getStringExtra(MainActivity.EXTRA_INFO_CAPTURE);
            System.out.println("________________________" + pathName + "__________________________________masodk eset, fenykepezos");
            File path = new File(pathName);
            if(path.exists()){
                System.out.println("leteziik-------------------------------------------------------");
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bm = BitmapFactory.decodeFile(pathName, options);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bm);
            }
        }


    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "Item name");


        menu.add(Menu.NONE,
                1,
                Menu.NONE,
                getResources().getString(R.string.analyze))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(Menu.NONE,
                0,
                Menu.NONE,
                getResources().getString(R.string.share_image))
                .setIcon(R.drawable.ic_share_black_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                shareImage();
                return true;
            case 1:
                pressAnalyzeImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareImage() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        startActivity(shareIntent);
    }

    public void pressAnalyzeImage() {
        btnStartProgress = (Button) findViewById(R.id.button_analyze);
        btnStartProgress.callOnClick();
    }

    public void analyzeImage(View v) {

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(true);
        progressBar.setMessage(getResources().getString(R.string.analyze_progress_bar));
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        //reset progress bar status
        progressBarStatus = 0;

        //reset filesize
        fileSize = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {

                    // process some tasks
                    progressBarStatus = doSomeTasks();

                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }

                // ok, file is downloaded,
                if (progressBarStatus >= 100) {

                    // sleep 2 seconds, so that you can see the 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // close the progress bar dialog
                    progressBar.dismiss();
                }
            }
        }).start();

    }


    public int doSomeTasks() {

        while (fileSize <= 1000000) {

        fileSize++;

        if (fileSize == 100000) {
        return 10;
        } else if (fileSize == 200000) {
        return 20;
        } else if (fileSize == 300000) {
        return 30;
        }

        }

        return 100;

        }

 }
