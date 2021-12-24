package com.example.android.screens.home;

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
import com.example.android.model.Photo;
import com.example.android.screens.photos.PhotoRecyclerAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoResultRecyclerAdapter extends RecyclerView.Adapter<PhotoResultRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Photo> results;
    private LayoutInflater inflater;

    public PhotoResultRecyclerAdapter(Context context, List<Photo> results) {
        this.context = context;
        this.results = results;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.photo_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo p = results.get(position);

        InputStream stream = null;
        try {
            stream = context.getContentResolver().openInputStream(p.getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
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
        return results.size();
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
        }
    }
}
