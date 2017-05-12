package com.mindfire.resourcemanager;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * This utility class provides helper methods for various common functionality on input fields.
 * <p>
 * <br><i>Author : <b>Ankit Bansal</b></i>
 * <br><i>Created Date : <b>12 May 2017</b></i>
 * <br><i>Modified Date : <b>12 May 2017</b></i>
 */
public final class InputUtility {
    public static void showKeyboard(View view) {
        if (view == null) return;

        // Request the focus and show keyboard if applicable
        view.requestFocus();
        InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(View view) {
        if (view == null || !view.isFocused()) return;

        // Hide the keyboard from supplied view
        InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void changeInputType(EditText editText, int inputType) {
        if (editText == null) return;

        // Get the selected text bounds, if there is any selection
        int selectionStart = editText.isFocused() ? editText.getSelectionStart() : -1;
        int selectionEnd = editText.isFocused() ? editText.getSelectionEnd() : -1;

        // Change input type and update selection
        editText.setInputType(inputType);
        if (selectionStart != -1 && selectionEnd != -1)
            editText.setSelection(selectionStart, selectionEnd);
    }
}
