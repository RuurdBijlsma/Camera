package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.Camera;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class ExposureCompensationSlider extends CameraValueSlider {
    public ExposureCompensationSlider(Context context, Camera camera) {
        super(context, camera, new String[]{
                "-2",
                "-1",
                "0",
                "1",
                "2",
        });
    }

    protected void applyToCamera(String value) {
        float time = stringToValue(value);
        camera.state.setExposureTime(time);
    }
}
