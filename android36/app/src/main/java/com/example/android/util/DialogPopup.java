package com.example.android.util;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.util.function.Function;

public abstract class DialogPopup {

    public static <R> void showDialogPopup(Context context, Function<String,R> onConfirm,
                                                     String title, String message, String hint)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        EditText editText = new EditText(context);
        editText.setHint(hint);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setView(editText);

        alert.setPositiveButton("Confirm", (dialog, whichButton) -> onConfirm.apply(editText.getText().toString().trim()));
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {});

        AlertDialog dialog = alert.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean notEmpty = !s.toString().trim().isEmpty();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(notEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void showConfirmationPopup(Context context, Thunk onConfirm,
                                           String title, String message)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);

        alert.setPositiveButton("Confirm", (dialog, whichButton) -> onConfirm.apply());
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {});

        alert.show();
    }

    public static void enableTextListener(EditText editText, AlertDialog dialog) {
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean notEmpty = !s.toString().trim().isEmpty();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(notEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
