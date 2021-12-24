package com.example.android.screens.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.model.Album;
import com.example.android.model.Photo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder> {

    private Album album;
    private LayoutInflater inflater;
    private Context context;
    private PhotoClickListener clickListener;

    public PhotoRecyclerAdapter(Context context, Album album, PhotoClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.album = album;
        this.context = context;
        this.clickListener = clickListener;
    }

    /**
     * Populates the RecyclerView with enough ViewHolders to fill the available screen space.
     * @param parent - The root layout
     * @param viewType - ViewType to determine which ViewHolder to use.
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.photo_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds existing ViewHolders to different objects in the attached data list as the user
     * scrolls.
     * @param holder - The ViewHolder (row)
     * @param position - The position in the data array that this ViewHolder corresponds to
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo p = album.getPhotos().get(position);

        InputStream stream = null;
        try {
            stream = context.getContentResolver().openInputStream(p.getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        System.out.println(bitmap.getWidth() + ", " + bitmap.getHeight());
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 200, 150, true);
        holder.thumbnail.setImageBitmap(resized);
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return album.getPhotoCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            thumbnail.setScaleType(ImageView.ScaleType.CENTER);
            itemView.setOnClickListener(this);
            thumbnail.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            clickListener.display(getAdapterPosition());
        }
    }

}
