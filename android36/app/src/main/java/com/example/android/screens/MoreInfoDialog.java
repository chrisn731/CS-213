package com.example.android.screens;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.R;

public class MoreInfoDialog extends DialogFragment {

    public interface MoreInfoDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onEditNameClick(DialogFragment dialog);
        void onRemoveClick(DialogFragment dialog);
    }

    MoreInfoDialogListener listener;
    String albumName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_more_info, null);
        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, id) -> {
            listener.onDialogPositiveClick(MoreInfoDialog.this);
        });

        Button edit = dialogView.findViewById(R.id.editName);
        Button delete = dialogView.findViewById(R.id.removeAlbum);
        edit.setOnClickListener(v -> listener.onEditNameClick(MoreInfoDialog.this));
        delete.setOnClickListener(v -> {
            listener.onRemoveClick(MoreInfoDialog.this);
            MoreInfoDialog.this.dismiss();
        });

        TextView nameText = dialogView.findViewById(R.id.dialogAlbumName);
        nameText.setText(albumName);

        return builder.create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumName = getArguments().getString("name");
        }
    }

    /**
     * Keeps a reference to the Main Activity for call-back functions within the listener interface.
     * @param context - this context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MoreInfoDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }
}