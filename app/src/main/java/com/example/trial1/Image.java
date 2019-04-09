package com.example.trial1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static android.graphics.Bitmap.CompressFormat.PNG;

public class Image extends Activity {

    private static final String LOG_TAG = Image.class.getSimpleName();
Bitmap bitmap;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        String displayPath = intent.getStringExtra(encode.EXTRA_FILE_TAG);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
         bitmap = BitmapFactory.decodeFile(displayPath);
        imageView.setImageBitmap(bitmap);
        Calendar calendar = Calendar.getInstance();
        String name = "" + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get
                (Calendar.DATE) + calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE)
                + calendar.get(Calendar.SECOND);
        File tempFile = new File(Environment.getExternalStorageDirectory().getPath() +
                "/Pictures/" + name + ".jpg");
        try {
            tempFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(tempFile);
           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_image, menu);
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

    public void saveImage(View view) {
        String path=MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"encimage","image ");
     Uri URI = Uri.parse(path);
        Toast.makeText(Image.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();

    }
}
