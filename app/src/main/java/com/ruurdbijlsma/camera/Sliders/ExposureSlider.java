package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.Camera;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class ExposureSlider extends CameraStringSlider {
    public ExposureSlider(Context context, Camera camera) {
        super(context, camera, new LinkedHashMap() {{
            put("1/6000", 0.000166666667);
            put("1/4000", 0.00025);
            put("1/2000", 0.0005);
            put("1/1000", 0.001);
            put("1/500", 0.002);
            put("1/250", 0.004);
            put("1/125", 0.008);
            put("1/60", 0.0166666667);
            put("1/30", 0.0333333333);
            put("1/15", 0.0666666667);
            put("1/8", 0.125);
            put("1/4", 0.25);
            put("1/2", 0.5);
            put("1", 1);
            put("2", 2);
            put("4", 4);
            put("8", 8);
            put("15", 15);
            put("30", 30);
        }});
    }

    public void applyToCamera(String value) {
        float time = stringToValue(value);
        camera.state.setExposureTime(time);
    }
}
