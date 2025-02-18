package com.example.expensetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ActivityExportPreview extends AppCompatActivity {
    private ListView previewListView;
    private TextView previewTotal;
    private RelativeLayout layout;

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_preview);
        previewListView = findViewById(R.id.previewListView);
        previewTotal = findViewById(R.id.previewTotal);
        Button saveImageButton = findViewById(R.id.saveImageButton);
        Button shareImageButton = findViewById(R.id.shareImageButton);
        layout = findViewById(R.id.exportView);
        back=findViewById(R.id.back);

        back.setOnClickListener(v->{
            this.finish();
        });

        Intent intent = getIntent();
        ArrayList<String> expenses = intent.getStringArrayListExtra("expenses");
        int total = intent.getIntExtra("total", 0);

        previewTotal.setText(String.valueOf(total));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenses);
        previewListView.setAdapter(adapter);

        saveImageButton.setOnClickListener(v -> saveImage());
        shareImageButton.setOnClickListener(v -> {saveImage(); shareImage();});
    }

    private void saveImage() {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        layout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = layout.getDrawingCache();
        save(bitmap);
    }

    private void save(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Download");
        String fileName = "expenseList.jpg";
        File myFile = new File(file, fileName);
        if (myFile.exists()) {
            myFile.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Download");
        String fileName = "expenseList.jpg";
        File myFile = new File(file, fileName);

        if (myFile.exists()) {
            Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    myFile
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is my expense list!");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toast.makeText(this, "Image not found. Save the image first.", Toast.LENGTH_SHORT).show();
        }
    }

}
