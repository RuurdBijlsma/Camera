package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.CameraManager.Camera;
import com.ruurdbijlsma.camera.Converters.ExposureTimeConverter;

import java.util.LinkedHashMap;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class ExposureSlider extends CameraStringSlider {
    public ExposureSlider(Context context, Camera camera) {
        super(context, camera, ExposureTimeConverter.exposureTimeFractions);
    }

    public void applyToCamera(String value) {
        float time = stringToValue(value);
        camera.state.setExposureTime(time);
    }
}
