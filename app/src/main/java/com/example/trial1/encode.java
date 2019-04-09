package com.example.trial1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.io.File;
import java.net.URI;

public class encode extends Activity {

    private static final String LOG_TAG = encode.class.getSimpleName();

    private File baseImage;
    private boolean isBase = true;
    private File secretFile;
    private File encodedTempImage;
    private ImageView baseView;
    private ImageView secretView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

     //   ActionButton actionButton = (ActionButton) findViewById(R.id.fab_encode);
       // actionButton.setImageResource(R.drawable.ic_arrow_forward_black_24dp);

        encodedTempImage = new File(getCacheDir(), "temp.png");
        baseView = (ImageView) findViewById(R.id.imgView);
        secretView = (ImageView) findViewById(R.id.imgSec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_encode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void uploadImage1(View v) {
        isBase = true;
        uploadImage();
    }

    public void uploadImage2(View v) {
        isBase = false;
        uploadFile();
    }

    public static final int SELECT_PICTURE = 1; //for the result listener
    public static final int SELECT_FILE = 2;
    public String selectedImagePath;

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    private void uploadFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "File"), SELECT_FILE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                baseImage = new File(selectedImagePath);
                baseView.setImageBitmap(decodeBitmapScaledDown(secretView, selectedImagePath));
            }
            if (requestCode == SELECT_FILE) {
                Uri selectedSecretUri = data.getData();
                try {
                    Log.e(LOG_TAG, getPath(selectedSecretUri));
                    secretFile = new File(getPath(selectedSecretUri));
                } catch (Exception e) {
                    Log.e(LOG_TAG, "exception", e);
                }

                if (getApplicationContext().getContentResolver().getType(selectedSecretUri)
                        .contains("image")) {
                    secretView.setImageBitmap(decodeBitmapScaledDown(secretView, getPath(selectedSecretUri)));
                }
            }
        }
    }

    public static Bitmap decodeBitmapScaledDown(ImageView imageView, String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        //for later use
        //String imageType = options.outMimeType;

        int reqHeight = imageView.getMaxHeight();
        int reqWidth = imageView.getMaxWidth();

        int inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {

            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


    public static final String EXTRA_FILE_TAG = "ENCODED FILE";

   public void encodeImage(View v) {
        if (baseImage != null && secretFile != null) {
            try {
                Log.v(LOG_TAG, "sending image to encoder");
                ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Encoding");
                progress.setMessage("This may take a few minutes for large files...");
                progress.show();
                new encodeTask(this, progress).execute(baseImage, secretFile,
                        encodedTempImage);
            } catch (OutOfMemoryError e) {
                Log.e(LOG_TAG, "exception", e);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "You need two images", Toast
                    .LENGTH_SHORT);
            toast.show();
        }
    }
}
