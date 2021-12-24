package com.example.android.screens.selectedphoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.model.Album;
import com.example.android.model.Photo;
import com.example.android.util.Data;
import com.example.android.util.DialogPopup;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DisplayPhotoActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    Toolbar toolbar;
    View tags;
    ImageView display;
    Photo photo;
    ImageButton addTag;
    int albumIndex;
    boolean showUI = true;
    TagRecyclerAdapter adapter;
    Animation animateShowTags;
    Animation animateHideTags;
    Animation animateShowToolbar;
    Animation animateHideToolbar;

    public interface TagClickListener {
        void removeTag(String key, String val);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        Bundle bundle = getIntent().getExtras();
        photo = (Photo) bundle.getSerializable("photo");
        albumIndex = bundle.getInt("albumIndex");

        toolbar = findViewById(R.id.displayPhotoToolbar);
        tags = findViewById(R.id.tagControls);
        display = findViewById(R.id.mainImageDisplay);
        addTag = findViewById(R.id.buttonAddTag);

        addTag.setOnClickListener(v -> addTag());

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitle(photo.getCaption());

        display.setOnClickListener(v -> toggleShowUI());
        display.setImageURI(photo.getUri());

        animateHideTags = AnimationUtils.loadAnimation(this, R.anim.animation_hide_tags);
        animateShowTags = AnimationUtils.loadAnimation(this, R.anim.animation_show_tags);
        animateHideToolbar = AnimationUtils.loadAnimation(this, R.anim.animation_hide_toolbar);
        animateShowToolbar = AnimationUtils.loadAnimation(this, R.anim.animation_show_toolbar);

        adapter = new TagRecyclerAdapter(this, photo, (key, val) -> {
            photo.removeTagPair(key, val);
            adapter.notifyDataSetChanged();
        });
        RecyclerView tagList = findViewById(R.id.tagRecycler);
        tagList.setLayoutManager(new LinearLayoutManager(this));
        tagList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_display_photo, menu);
        MenuItem menuEditButton = menu.findItem(R.id.editName);
        MenuItem menuDeleteButton = menu.findItem(R.id.deletePhoto);
        MenuItem menuShowMoreButton = menu.findItem(R.id.showMoveOptions);

        menuEditButton.setOnMenuItemClickListener(item -> {
            DialogPopup.showDialogPopup(
                    this,
                    this::editCaption,
                    "Edit Photo Caption",
                    "",
                    photo.getCaption()
            );
            return false;
        });
        menuDeleteButton.setOnMenuItemClickListener(item -> {
            DialogPopup.showConfirmationPopup(
                    this,
                    this::deletePhoto,
                    "Delete Photo",
                    "Are you sure you want to delete this photo?"
            );
            return false;
        });
        menuShowMoreButton.setOnMenuItemClickListener(item -> {
            PopupMenu popup = new PopupMenu(this, findViewById(R.id.showMoveOptions));
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.photo_move_popup);
            popup.show();
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void addTag() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_tag_dialog, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.tagKeyGroup);
        EditText editText = dialogView.findViewById(R.id.tagValue);
        alert.setView(dialogView);

        alert.setPositiveButton("Confirm", (dialog, whichButton) -> {
            int selected = radioGroup.getCheckedRadioButtonId();
            String key = ((RadioButton) dialogView.findViewById(selected)).getText().toString();
            photo.addTagPair(key, editText.getText().toString().trim());
            adapter.notifyDataSetChanged();
        });
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {});

        AlertDialog d = alert.create();
        //DialogPopup.enableTextListener(editText, d);

        alert.show();
    }

    public Void editCaption(String caption) {
        photo.setCaption(caption);
        toolbar.setTitle(caption);
        return null;
    }

    private void deletePhoto() {
        Data.getAlbum(albumIndex).removePhoto(photo);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void toggleShowUI() {
        int i = (showUI = !showUI) ? View.VISIBLE : View.INVISIBLE;
        if (showUI) {
            tags.startAnimation(animateShowTags);
            toolbar.startAnimation(animateShowToolbar);
        } else {
            tags.startAnimation(animateHideTags);
            toolbar.startAnimation(animateHideToolbar);
        }
        toolbar.setVisibility(i);
        tags.setVisibility(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("photo", photo);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.move_to:
                copyTo(this::doMove);
                break;
            case R.id.copy_to:
                copyTo(this::doCopy);
        }
        return false;
    }

    private void copyTo(Consumer<String> onConfirm) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("title");
        alert.setMessage("age");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_copy_photo_to, null);
        alert.setView(dialogView);
        Spinner spinner = dialogView.findViewById(R.id.spinner);
        ArrayList<String> albumNames = new ArrayList<>();
        for (Album a : Data.getAlbums()) {
            if (!a.contains(photo))
                albumNames.add(a.getName());
        }
        if (albumNames.size() == 0) {
            displayResult("This image already exists in all albums!");
            return;
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        alert.setPositiveButton("Confirm", (dialog, whichButton) ->
            onConfirm.accept((String) spinner.getSelectedItem())
        );
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {});

        alert.show();
    }

    private void doCopy(String selected) {
        if (Data.getAlbum(selected) == null)
            return;
        Data.getAlbum(selected).addPhoto(photo);
        displayResult("Photo successfully copied!");
    }

    private void doMove(String selected) {
        Data.getAlbum(selected).addPhoto(photo);
        Data.getAlbum(albumIndex).removePhoto(photo);
        displayResult("Photo successfully moved!");
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void displayResult(String msg) {
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onPause() {
        Data.syncToDisk(this);
        super.onPause();
    }
}