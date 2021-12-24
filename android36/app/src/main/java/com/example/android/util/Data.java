package com.example.android.util;

import android.content.Context;

import com.example.android.model.Album;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Data {

   private static ArrayList<Album> albums = new ArrayList<>();

    /**
     * Name to give the photos application save data
     */
    private static final String dataFileName = "photos.dat";

    /**
     * Writes all data to disk. Called upon application entry.
     */
    public static void syncToDisk(Context context) {
        System.out.println(
                "Syncing to disk at: " + dataFileName
        );
        try {
            FileOutputStream fos = context.openFileOutput(dataFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(albums);
            oos.close();
            System.out.println("Done.");
        } catch (IOException e) {

        }
    }

    /**
     * Loads in photos data from disk. Called upon application exit and cleanup.
     * @throws ClassNotFoundException Thrown if failed to store data into users list
     */
    @SuppressWarnings("unchecked")
    public static void loadFromDisk(Context context) {
        System.out.println(
                "Loading " + dataFileName + " from disk..."
        );

        try {
            FileInputStream fis = context.openFileInput(dataFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) ois.readObject();
            ois.close();
            System.out.println("Done.");
        } catch (FileNotFoundException | ClassNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Album> getAlbums() {
        return albums;
    }

    public static Album getAlbum(String album) {
        for (Album a : albums) {
            if (a.getName().equals(album))
                return a;
        }
        return null;
    }

    public static Album getAlbum(int pos) {
        Album a = null;
        try {
            a = albums.get(pos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ArrayIndexOutOfBoundsException(
                    "getAlbum(): Attempting to access albums at index " +
                            "(" + pos + ")!"
            );
        } finally {
            if (a == null) {
                throw new ArrayIndexOutOfBoundsException(
                        "getAlbum(): Attempting to access albums at index " +
                                "(" + pos + ")!"
                );
            }
        }
        return a;
    }

    public static void addAlbum(Album a) {
        albums.add(a);
    }

    public static void removeAlbum(Album a) {
        albums.remove(a);
    }

    public static void removeAlbum(int index) {
        albums.remove(index);
    }
}
