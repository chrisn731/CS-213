package com.example.android.screens.slideshow;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.R;
import com.example.android.model.Photo;
import com.example.android.util.Data;

public class SlideShowActivity extends AppCompatActivity {
    private SlideShowViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int albumIndex = getIntent().getExtras().getInt("albumIndex");
        model = new ViewModelProvider(this).get(SlideShowViewModel.class);
        model.getPhotos(Data.getAlbum(albumIndex));
        model.getSelected().observe(this, photos -> updateSlideImage());

        setContentView(R.layout.activity_slide_show);

        findViewById(R.id.slideShowNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextImage();
            }
        });

        findViewById(R.id.slideShowPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrevImage();
            }
        });


    }

    private void loadNextImage() {
        model.loadNextImage();
    }

    private void loadPrevImage() {
        model.loadPrevImage();
    }

    private void updateSlideImage() {
        Photo curr = model.getSelected().getValue();
        ImageView image = findViewById(R.id.slideShowImage);
        image.setImageURI(curr.getUri());
        ((TextView) findViewById(R.id.slideShowImageCaption)).setText(
                curr.getCaption()
        );
    }
}
