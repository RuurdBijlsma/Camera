package com.ruurdbijlsma.camera;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

class ValueSlider {
    private String[] values;
    private final Context context;
    private ScrollView scrollView;
    private static final String TAG = "Camera";
    private String currentValue;

    ValueSlider(final Context context, ScrollView scrollView, String[] values) {
        this.values = values;
        this.context = context;
        this.scrollView = scrollView;
        this.currentValue = values[0];

        scrollView.addView(getLinearLayout());
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                String newValue = getSelectedValue();
                if (!Objects.equals(newValue, currentValue)) {
                    onValueChange(newValue, currentValue);
                    currentValue = newValue;
                }
            }
        });
    }

    void onValueChange(String newValue, String oldValue) {
        Log.d(TAG, newValue);
    }

    String getSelectedValue() {
        float scrollPosition = getScrollPosition();
        int index;
        if (scrollPosition >= 1)
            index = values.length - 1;
        else
            index = (int) (values.length * scrollPosition);
        return values[index];
    }

    float getNumericalValue(float min, float max) {
        float length = max - min;
        float value = length * getScrollPosition();
        return min + value;
    }

    float getScrollPosition() {
        float scrollY = scrollView.getScrollY();
        float maxScrollPosition = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
        return scrollY / maxScrollPosition;
    }

    private LinearLayout getLinearLayout() {
        int containerSize = 900;
        int textPadding = 35;
        int textSize = 18;

        LinearLayout layout = new LinearLayout(context, null);
        layout.setPadding(0, containerSize / 2, 0, containerSize / 2);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (String value : values) {
            TextView textView = new TextView(context);
            textView.setText(value + " –");
            textView.setGravity(Gravity.RIGHT);
            textView.setPadding(textPadding, textPadding, textPadding, textPadding);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(textSize);
            layout.addView(textView);
        }

        return layout;
    }
}
