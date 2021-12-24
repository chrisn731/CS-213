package com.example.android.screens.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.model.Album;

import java.util.List;

public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.ViewHolder> {

    private List<Album> data;
    private LayoutInflater inflater;
    private AlbumClickListener clickListener;

    public AlbumRecyclerAdapter(Context context, List<Album> data, AlbumClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.clickListener = clickListener;
    }

    /**
     * Populates the RecyclerView with enough ViewHolders to fill the available screen space.
     * @param parent - The root layout
     * @param viewType - ViewType to determine which ViewHolder to use.
     * @return The View
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.album_recycler_item, parent, false);
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
        String albumName = data.get(position).getName();
        holder.textView.setText(albumName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.albumName);
            imageButton = itemView.findViewById(R.id.buttonMore);

            itemView.setOnClickListener(this);
            imageButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonMore:
                    clickListener.showMore(getAdapterPosition(), v);
                    break;
                default:
                    clickListener.open(getAdapterPosition());
            }
        }
    }
}
