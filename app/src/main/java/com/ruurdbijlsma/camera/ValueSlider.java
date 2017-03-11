package com.ruurdbijlsma.camera;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

class ValueSlider {
    private String[] values;
    private final Context context;
    private ScrollView scrollView;
    private static final String TAG = "Camera";
    private String currentValue;
    private float scrollEndedAt = 0;

    ValueSlider(final Context context, String[] values) {
        this.values = values;
        this.context = context;
        this.scrollView = new ScrollView(context) {
            @Override
            protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                final float scrollPosition = getScrollPosition(scrollY);
                String newValue = getSelectedValue(scrollPosition);
                if (!Objects.equals(newValue, currentValue)) {
                    onValueChange(newValue, currentValue);
                    currentValue = newValue;
                }
                onScroll(scrollPosition);

                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public synchronized void run() {
                        float newScrollPosition = getScrollPosition(scrollView.getScrollY());
                        if (scrollPosition == newScrollPosition && newScrollPosition != scrollEndedAt) {
                            scrollEndedAt = newScrollPosition;
                            onScrollEnd(newScrollPosition);
                        }
                    }
                }, 100);
            }
        };
        this.currentValue = values[0];

        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(getLinearLayout());
    }

    void onScrollEnd(float scrollPosition) {
        Log.d(TAG, "SCROLL ENDED " + scrollPosition);
    }

    void onScroll(float scrollPosition) {

    }

    ScrollView getScrollView() {
        return scrollView;
    }

    void onValueChange(String newValue, String oldValue) {
        Log.d(TAG, newValue);
    }

    String getSelectedValue(float scrollPosition) {
        int index;
        if (scrollPosition >= 1)
            index = values.length - 1;
        else
            index = (int) (values.length * scrollPosition);
        return values[index];
    }

    float getNumericalValue(float min, float max) {
        float length = max - min;
        float value = length * getScrollPosition(scrollView.getScrollY());
        return min + value;
    }

    float getScrollPosition(int scrollY) {
        float maxScrollPosition = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
        float scrollPosition = scrollY / maxScrollPosition;
        return scrollPosition > 1 ? 1 : scrollPosition < 0 ? 0 : scrollPosition;
    }

    private LinearLayout getLinearLayout() {
        int textPadding = 30;
        int textSize = 14;

        LinearLayout layout = new LinearLayout(context, null);
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
        int textViewHeight = 18 + textPadding * 2;
        int containerHeight = dpToPx(220);
        int requiredPadding = containerHeight / 2 - textViewHeight;

        layout.setPadding(0, requiredPadding, 0,requiredPadding);

        return layout;
    }

    int dpToPx(int dp) {
        float dpi = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (dp * (dpi / 160));
    }
}
