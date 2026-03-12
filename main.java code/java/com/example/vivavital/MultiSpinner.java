package com.example.vivavital;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiSpinner extends AppCompatSpinner {
    private List<String> items = new ArrayList<>();
    private boolean[] selected;
    private MultiSpinnerListener listener;
    private String defaultText = "Select";
    private ArrayAdapter<String> adapter;
    private boolean dialogShowing = false;

    public MultiSpinner(Context context) {
        super(context);
        init();
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{defaultText});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);
    }

    public void setItems(List<String> items, String defaultText, MultiSpinnerListener listener) {
        if (items == null || items.isEmpty()) {
            this.items = new ArrayList<>();
            this.items.add("No options available");
        } else {
            this.items = new ArrayList<>(items);
        }

        this.defaultText = defaultText != null ? defaultText : "Select";
        this.listener = listener;
        this.selected = new boolean[this.items.size()];
        Arrays.fill(selected, false);

        updateSpinnerText();

        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && !dialogShowing) {
                showMultiSelectDialog();
                return true;
            }
            return false;
        });
    }

    public void showMultiSelectDialog() {
        if (getContext() == null || items.isEmpty()) return;

        dialogShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Options");

        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[0]),
                selected,
                (dialog, which, isChecked) -> selected[which] = isChecked
        );

        builder.setPositiveButton("OK", (dialog, which) -> {
            updateSpinnerText();
            if (listener != null) {
                listener.onItemsSelected(getSelectedItems());
            }
            dialogShowing = false;
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialogShowing = false);
        builder.setOnCancelListener(dialog -> dialogShowing = false);
        builder.show();
    }

    public void updateSpinnerText() {
        StringBuilder spinnerText = new StringBuilder();
        boolean hasSelection = false;

        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                if (hasSelection) spinnerText.append(", ");
                spinnerText.append(items.get(i));
                hasSelection = true;
            }
        }

        adapter.clear();
        adapter.add(hasSelection ? spinnerText.toString() : defaultText);
        adapter.notifyDataSetChanged();
    }

    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                selectedItems.add(items.get(i));
            }
        }
        return selectedItems.isEmpty() ? Arrays.asList("None") : selectedItems;
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(List<String> selectedItems);
    }

    @Override
    public boolean performClick() {
        if (!dialogShowing) {
            showMultiSelectDialog();
        }
        return true;
    }
}