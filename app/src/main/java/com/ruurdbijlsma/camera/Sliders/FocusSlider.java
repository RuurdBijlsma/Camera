package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.CameraManager.Camera;

import java.util.LinkedHashMap;

/**
 * Gemaakt door ruurd op 12-3-2017.
 */

public class FocusSlider extends CameraStringSlider {
    public FocusSlider(Context context, Camera camera) {
        super(context, camera, new LinkedHashMap() {{
            put("Infinity", 1000000);
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
            put("9cm", 0.09);
            put("8cm", 0.08);
            put("7cm", 0.07);
        }});
    }

    public void applyToCamera(String value) {
        final float distance = stringToValue(value);
        camera.state.setFocusDistanceInMeters(distance);
    }
}
