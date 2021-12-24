package com.example.android.screens.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.model.Album;
import com.example.android.model.Photo;
import com.example.android.screens.photos.ShowPhotosActivity;
import com.example.android.util.Data;
import com.example.android.util.DialogPopup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

interface AlbumClickListener {
    void showMore(int pos, View view);
    void open(int pos);
}

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView albumList;
    AlbumRecyclerAdapter adapter;
    PhotoResultRecyclerAdapter photoAdapter;
    Toolbar toolbar;
    ArrayList<Album> albums;
    int selectedIndex;
    TextView noResultBox;
    MenuItem undoButton;
    MenuItem menuSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act);
        toolbar = findViewById(R.id.mytoolbar1);
        setSupportActionBar(toolbar);

        Data.loadFromDisk(this);
        albums = Data.getAlbums();


        adapter = new AlbumRecyclerAdapter(this, albums, new AlbumClickListener() {
            @Override
            public void showMore(int pos, View anchor) {
                showInfo(pos, anchor);
            }

            @Override
            public void open(int pos) {
                openAlbum(pos);
            }
        });
        noResultBox = findViewById(R.id.noResultsFound);
        if (albums.size() == 0) {
            noResultBox.setVisibility(View.VISIBLE);
            noResultBox.setText("No Albums");
        } else {
            noResultBox.setVisibility(View.GONE);
        }

        albumList = findViewById(R.id.albumListView1);

        noResultBox.setVisibility(albums.size() == 0 ? View.VISIBLE : View.GONE);
        albumList.setLayoutManager(new LinearLayoutManager(this));
        albumList.setAdapter(adapter);
    }

    public void showInfo(int pos, View anchor) {
        selectedIndex = pos;
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.more_info_popup);
        popup.show();
    }

    private void openAlbum(int pos) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", pos);
        Intent intent = new Intent(this, ShowPhotosActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void doImageSearch() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        String[] spinnerOptions = {"Person", "Location"};

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.search_photos_popup, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.searchOptions);
        RadioButton searchSingle = dialogView.findViewById(R.id.searchSingleOption);
        RadioButton searchOR = dialogView.findViewById(R.id.searchOROption);
        RadioButton searchAND = dialogView.findViewById(R.id.searchANDOption);
        AutoCompleteTextView value1 = dialogView.findViewById(R.id.SearchTagValue1);
        AutoCompleteTextView value2 = dialogView.findViewById(R.id.SearchTagValue2);
        Spinner key1 = dialogView.findViewById(R.id.TagOptions1);
        Spinner key2 = dialogView.findViewById(R.id.TagOptions2);
        value1.setThreshold(1);
        value2.setThreshold(1);

        key2.setVisibility(View.GONE);
        value2.setVisibility(View.GONE);
        searchSingle.setOnClickListener(v -> {
            key2.setVisibility(View.GONE);
            value2.setVisibility(View.GONE);
        });
        searchOR.setOnClickListener(v -> {
            key2.setVisibility(View.VISIBLE);
            value2.setVisibility(View.VISIBLE);
        });
        searchAND.setOnClickListener(v -> {
            key2.setVisibility(View.VISIBLE);
            value2.setVisibility(View.VISIBLE);
        });

        ArrayAdapter<String> key1Adapter = new ArrayAdapter<>(this, R.layout.spinner_list_item, spinnerOptions);
        key1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        key1.setAdapter(key1Adapter);
        ArrayAdapter<String> key2Adapter = new ArrayAdapter<>(this, R.layout.spinner_list_item, spinnerOptions);
        key2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        key2.setAdapter(key2Adapter);

        ArrayList<String> personSuggestions =  new ArrayList<>();
        ArrayList<String> locationSuggestions =  new ArrayList<>();
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                for (Iterator<String> i = p.getTagValues("Person"); i.hasNext(); ) {
                    String toAdd = i.next();
                    if (!personSuggestions.contains(toAdd))
                        personSuggestions.add(toAdd);
                }
                for (Iterator<String> i = p.getTagValues("Location"); i.hasNext(); ) {
                    String toAdd = i.next();
                    if (!locationSuggestions.contains(toAdd))
                        locationSuggestions.add(toAdd);
                }
            }
        }

        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                personSuggestions
        );
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                locationSuggestions
        );
        value1.setAdapter(personAdapter);
        key1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String key = (String) parent.getItemAtPosition(position);
                if (key.equals("Person")) {
                    value1.setAdapter(personAdapter);
                } else if (key.equals("Location")) {
                    System.out.println("is this runningl");
                    value1.setAdapter(locationAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter<String> personAdapter2 = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                personSuggestions
        );
        ArrayAdapter<String> locationAdapter2 = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                locationSuggestions
        );
        value2.setAdapter(personAdapter2);
        key2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String key = (String) parent.getItemAtPosition(position);
                if (key.equals("Person")) {
                    value2.setAdapter(personAdapter2);
                } else if (key.equals("Location")) {
                    value2.setAdapter(locationAdapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        alert.setView(dialogView);
        alert.setPositiveButton("Search", (dialog, whichButton) -> {
            String resKey1 = (String) key1.getSelectedItem();
            String resKey2 = (String) key2.getSelectedItem();
            String resVal1 = value1.getText().toString();
            String resVal2 = value2.getText().toString();
            int id = radioGroup.getCheckedRadioButtonId();

            int operation;
            switch (id) {
                case R.id.searchANDOption:
                    operation = 2;
                    break;
                case R.id.searchOROption:
                    operation = 1;
                    break;
                default:
                    operation = 0;
                    break;
            }
            ArrayList<Photo> results = new ArrayList<>();
            for (Album a : albums) {
                for (Photo p : a.getPhotos()) {
                    boolean toAdd = false;
                    switch (operation) {
                        case 0:
                            if (p.tagPairExists(resKey1, resVal1))
                                toAdd = true;
                            break;
                        case 1:
                            if (p.tagPairExists(resKey1, resVal1) || p.tagPairExists(resKey2, resVal2))
                                toAdd = true;
                            break;
                        case 2:
                            if (p.tagPairExists(resKey1, resVal1) && p.tagPairExists(resKey2, resVal2))
                                toAdd = true;
                            break;
                    }
                    if (toAdd && !results.contains(p))
                        results.add(p);
                }
            }
            setRecyclerToPhotoAdapter(results);
        });
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {});

        alert.show();
    }


    private void setRecyclerToAlbumAdapter() {
        menuSearchButton.setVisible(true);
        undoButton.setVisible(false);
        toolbar.setTitle("Albums");
        noResultBox.setVisibility(albums.size() == 0 ? View.VISIBLE : View.GONE);
        albumList.setLayoutManager(new LinearLayoutManager(this));
        albumList.setAdapter(adapter);
    }

    private void setRecyclerToPhotoAdapter(List<Photo> photoResults) {
        menuSearchButton.setVisible(false);
        undoButton.setVisible(true);
        toolbar.setTitle("Search Results");
        noResultBox.setVisibility(photoResults.size() == 0 ? View.VISIBLE : View.GONE);
        noResultBox.setText("Nothing Found");
        photoAdapter = new PhotoResultRecyclerAdapter(this, photoResults);
        albumList.setLayoutManager(new GridLayoutManager(this, 3));
        albumList.setAdapter(photoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_albums, menu);
        menuSearchButton = menu.findItem(R.id.search_action);
        MenuItem menuAddButton = menu.findItem(R.id.plus);
        undoButton = menu.findItem(R.id.undo_action);
        undoButton.setVisible(false);

        menuAddButton.setOnMenuItemClickListener(item -> {
            DialogPopup.showDialogPopup(
                    this,
                    this::createAlbum,
                    "Create New Album",
                    "Enter the album name:",
                    ""
            );
            return false;
        });
        undoButton.setOnMenuItemClickListener(item -> {
            setRecyclerToAlbumAdapter();
            return false;
        });


        menuSearchButton.setOnMenuItemClickListener(item -> {
            doImageSearch();
            return false;
        });


        return super.onCreateOptionsMenu(menu);
    }

    public Void createAlbum(String name) {
        if (name == null || duplicateNameFound(name))
            return null;
        albums.add(new Album(name));
        adapter.notifyItemInserted(albums.size());
        if (albums.size() == 0) {
            noResultBox.setVisibility(View.VISIBLE);
            noResultBox.setText("No Albums");
        } else {
            noResultBox.setVisibility(View.GONE);
        }
        setRecyclerToAlbumAdapter();
        return null;
    }

    /**
     * Checks whether a name is already used by an album within the user
     * @param name - we want to check for
     * @return - whether or not the name is in use
     */
    private boolean duplicateNameFound(String name) {
        for (Album a : albums) {
            if (a.getName().equals(name)) {
                Toast.makeText(getApplicationContext(), "Album name already exists", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    public void deleteAlbum() {
        albums.remove(selectedIndex);
        adapter.notifyItemRemoved(selectedIndex);
        if (albums.size() == 0) {
            noResultBox.setVisibility(View.VISIBLE);
            noResultBox.setText("No Albums");
        } else {
            noResultBox.setVisibility(View.GONE);
        }
    }

    public Void editName(String newName) {
        if (duplicateNameFound(newName))
            return null;
        albums.get(selectedIndex).setName(newName);
        adapter.notifyDataSetChanged();
        return null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_edit_name:
            DialogPopup.showDialogPopup(
                    this,
                    this::editName,
                    "Edit Album Name",
                    "",
                    albums.get(selectedIndex).getName()
            );
            break;
        case R.id.menu_delete:
            DialogPopup.showConfirmationPopup(
                    this,
                    this::deleteAlbum,
                    "Delete Album",
                    "Are you sure you want to delete '" + albums.get(selectedIndex).getName() + "'?"
            );
        }
        return false;
    }

    @Override
    protected void onPause() {
        Data.syncToDisk(this);
        super.onPause();
    }
}