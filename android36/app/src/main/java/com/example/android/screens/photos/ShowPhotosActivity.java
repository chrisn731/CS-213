package com.example.android.screens.photos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.model.Album;
import com.example.android.model.Photo;
import com.example.android.screens.selectedphoto.DisplayPhotoActivity;
import com.example.android.screens.slideshow.SlideShowActivity;
import com.example.android.util.Data;

import java.io.File;

interface PhotoClickListener {
    void display(int pos);
}

public class ShowPhotosActivity extends AppCompatActivity {

    Album album;
    PhotoRecyclerAdapter adapter;
    private static final int PICK_IMAGE_FILE = 1;
    private static final int OPEN_IMAGE = 2;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos);
        Bundle bundle = getIntent().getExtras();
        album = Data.getAlbum(bundle.getInt("index"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.photoViewToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(album.getName());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        adapter = new PhotoRecyclerAdapter(this, album, pos -> displayPhoto(pos));
        RecyclerView photoList = findViewById(R.id.photoListView);
        photoList.setLayoutManager(new GridLayoutManager(this, 3));
        photoList.setItemViewCacheSize(20);
        photoList.setDrawingCacheEnabled(true);
        photoList.setAdapter(adapter);
    }

    public void displayPhoto(int pos) {
        Bundle bundle = new Bundle();
        bundle.putInt("albumIndex", Data.getAlbums().indexOf(album));
        bundle.putSerializable("photo", album.getPhotos().get(pos));
        Intent intent = new Intent(this, DisplayPhotoActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, OPEN_IMAGE);
    }

    private void playSlideShow() {
        if (album.getPhotoCount() == 0) {
            Toast.makeText(
                    getApplicationContext(),
                    "Add some photos to use the slideshow!",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("albumIndex", Data.getAlbums().indexOf(album));
        Intent intent = new Intent(this, SlideShowActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_photos, menu);
        MenuItem menuAddButton = menu.findItem(R.id.plus);
        MenuItem menuPlayButton = menu.findItem(R.id.play_slideshow);

        menuAddButton.setOnMenuItemClickListener(item -> {
            addPhoto();
            return false;
        });
        menuPlayButton.setOnMenuItemClickListener(item -> {
            System.out.println("PLAY SLIDESHOW");
            playSlideShow();
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void addPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_FILE);
    }

    private void doAddPhoto(Uri uri) {
        Photo newPhoto = null;
        for (Album a : Data.getAlbums()) {
            if (a != album) {
                newPhoto = a.getPhotoByFile(uri);
                if (newPhoto != null) {
                    System.out.println("RETURNED: " +newPhoto);
                    break;
                }
            }
        }

        if (newPhoto == null) {
            String filename = getFileName(uri);
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            newPhoto = new Photo(uri, filename);
        }

        /*
        if (album.contains(newPhoto)) {
            Toast.makeText(
                    getApplicationContext(),
                    "Photo already exists within this album.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

         */
        album.addPhoto(newPhoto);
        adapter.notifyItemInserted(album.getPhotoCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_FILE) {
                doAddPhoto(data.getData());
            } else if (requestCode == OPEN_IMAGE) {
                if (data != null && data.getExtras() != null) {
                    Photo p = (Photo) data.getExtras().get("photo");
                    album.updatePhoto(p);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    null
            );
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onPause() {
        Data.syncToDisk(this);
        super.onPause();
    }
}