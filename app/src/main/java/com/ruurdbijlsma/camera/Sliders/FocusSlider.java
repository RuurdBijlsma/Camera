package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.Camera;

import java.util.HashMap;

/**
 * Gemaakt door ruurd op 12-3-2017.
 */

public class FocusSlider extends ValueSlider {
    private HashMap stringToValueMap = new HashMap() {{
        put("Infinity", 1000);
        put("5m", 5);
        put("4m", 4);
        put("3m", 3);
        put("2m", 2);
        put("1m", 1);
        put("80cm", 0.80);
        put("60cm", 0.60);
        put("40cm", 0.40);
        put("30cm", 0.30);
        put("20cm", 0.20);
        put("15cm", 0.15);
        put("10cm", 0.10);
        put("5cm", 0.05);
    }};

    private Camera camera;
    private final float minimumFocus = 0.05f;
    private final float maximumFocus = 10;

    public FocusSlider(Context context, Camera camera) {
        super(context);

        setValues(new String[]{
                "Infinity",
                "5m",
                "4m",
                "3m",
                "2m",
                "1m",
                "80cm",
                "60cm",
                "40cm",
                "30cm",
                "20cm",
                "15cm",
                "10cm",
                "5cm",
        });

        this.camera = camera;
    }

    private void applyToCamera(String value) {
        float distance = stringToValue(value);
        camera.state.setFocusDistanceInMeters(distance);
    }


    @Override
    public float stringToValue(String string) {
        Object value = stringToValueMap.get(string);
        float result = 0;
        if (value instanceof Integer)
            result = (float) (int) value;
        if (value instanceof Double)
            result = (float) (double) value;
        return result;
    }

    @Override
    public void onValueChange(String newValue, String oldValue) {
        applyToCamera(newValue);
    }

    @Override
    public void onScrollEnd(float scrollPosition) {
        String value = getSelectedValue(scrollPosition);
        applyToCamera(value);
    }
}
