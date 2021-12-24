package com.example.android.screens.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.model.Album;
import com.example.android.model.Photo;

import java.util.List;

public class SlideShowViewModel extends ViewModel {
    private Album album;
    private int currPhotoIndex = 0;
    private MutableLiveData<List<Photo>> photos = new MutableLiveData<>();
    private MutableLiveData<Photo> selected = new MutableLiveData<>();

    public LiveData<List<Photo>> getPhotos(Album a) {
        album = a;
        loadPhotos();
        selected.setValue(photos.getValue().get(currPhotoIndex));
        return photos;
    }

    private void loadPhotos() {
        photos.setValue(album.getPhotos());
    }

    public void loadNextImage() {
        if (++currPhotoIndex >= photos.getValue().size())
            currPhotoIndex = 0;
        selected.setValue(photos.getValue().get(currPhotoIndex));
    }

    public void loadPrevImage() {
        if (--currPhotoIndex < 0)
            currPhotoIndex = photos.getValue().size() - 1;
        selected.setValue(photos.getValue().get(currPhotoIndex));
    }

    public LiveData<Photo> getSelected() {
        return selected;
    }
}
