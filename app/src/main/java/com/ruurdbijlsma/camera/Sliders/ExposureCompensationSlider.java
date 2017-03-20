package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.CameraManager.Camera;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class ExposureCompensationSlider extends CameraValueSlider {
    public ExposureCompensationSlider(Context context, Camera camera) {
        super(context, camera, new String[]{
                "12",
                "11",
                "10",
                "9",
                "8",
                "7",
                "6",
                "5",
                "4",
                "3",
                "2",
                "1",
                "0",
                "-1",
                "-2",
                "-3",
                "-4",
                "-5",
                "-6",
                "-7",
                "-8",
                "-9",
                "-10",
                "-11",
                "-12",
        });
    }

    public void applyToCamera(String value) {
        float compensation = stringToValue(value);
        camera.state.setExposureCompensation((int) compensation);
    }
}
