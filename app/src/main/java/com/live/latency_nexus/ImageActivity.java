package com.live.latency_nexus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
    private ZoomageView ImageZoomageView;
    ImageView image_back;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageZoomageView = findViewById(R.id.imageViewImageFullScreen);
        image_back=findViewById(R.id.imageView15);
        Intent intent;
        intent = getIntent();
        url = intent.getStringExtra("url");

        Picasso.get().load(url).into(ImageZoomageView);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}