package com.example.android.screens.selectedphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.model.Photo;

import java.util.Iterator;

public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Photo photo;
    private DisplayPhotoActivity.TagClickListener clickListener;

    public TagRecyclerAdapter(Context context, Photo photo, DisplayPhotoActivity.TagClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.photo = photo;
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
    public TagRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tag_recycler_item, parent, false);
        return new TagRecyclerAdapter.ViewHolder(view);
    }

    /**
     * Binds existing ViewHolders to different objects in the attached data list as the user
     * scrolls.
     * @param holder - The ViewHolder (row)
     * @param position - The position in the data array that this ViewHolder corresponds to
     */
    @Override
    public void onBindViewHolder(@NonNull TagRecyclerAdapter.ViewHolder holder, int position) {
        String key = photo.getTagKey(position);
        String complete = key + ": " + photo.getTagValuePos(key, position);
        holder.tag.setText(complete);
    }

    @Override
    public int getItemCount() {
        return photo.getNumTags();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tag;
        ImageButton remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.tagItemText);
            remove = itemView.findViewById(R.id.tagItemRemove);
            remove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tagItemRemove) {
                String str = tag.getText().toString();
                String tagKey = str.substring(0, str.indexOf(':'));
                String tagVal = str.substring(str.indexOf(':') + 1);
                System.out.println(tagKey + " " + tagVal);
                clickListener.removeTag(tagKey, tagVal);
            }
        }
    }
}
