package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;
import android.hardware.camera2.params.RggbChannelVector;

import com.ruurdbijlsma.camera.CameraManager.Camera;
import com.ruurdbijlsma.camera.Converters.ColorTemperatureConverter;

import java.util.LinkedHashMap;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class ColorCorrectionSlider extends CameraStringSlider {
    public ColorCorrectionSlider(Context context, Camera camera) {
        super(context, camera, new LinkedHashMap() {{
            put("8000k", ColorTemperatureConverter.kelvinToNormalizedRgb(8000));
            put("7800k", ColorTemperatureConverter.kelvinToNormalizedRgb(7800));
            put("7600k", ColorTemperatureConverter.kelvinToNormalizedRgb(7600));
            put("7400k", ColorTemperatureConverter.kelvinToNormalizedRgb(7400));
            put("7200k", ColorTemperatureConverter.kelvinToNormalizedRgb(7200));
            put("7000k", ColorTemperatureConverter.kelvinToNormalizedRgb(7000));
            put("6800k", ColorTemperatureConverter.kelvinToNormalizedRgb(6800));
            put("6600k", ColorTemperatureConverter.kelvinToNormalizedRgb(6600));
            put("6400k", ColorTemperatureConverter.kelvinToNormalizedRgb(6400));
            put("6200k", ColorTemperatureConverter.kelvinToNormalizedRgb(6200));
            put("6000k", ColorTemperatureConverter.kelvinToNormalizedRgb(6000));
            put("5800k", ColorTemperatureConverter.kelvinToNormalizedRgb(5800));
            put("5600k", ColorTemperatureConverter.kelvinToNormalizedRgb(5600));
            put("5400k", ColorTemperatureConverter.kelvinToNormalizedRgb(5400));
            put("5200k", ColorTemperatureConverter.kelvinToNormalizedRgb(5200));
            put("5000k", ColorTemperatureConverter.kelvinToNormalizedRgb(5000));
            put("4800k", ColorTemperatureConverter.kelvinToNormalizedRgb(4800));
            put("4600k", ColorTemperatureConverter.kelvinToNormalizedRgb(4600));
            put("4400k", ColorTemperatureConverter.kelvinToNormalizedRgb(4400));
            put("4200k", ColorTemperatureConverter.kelvinToNormalizedRgb(4200));
            put("4000k", ColorTemperatureConverter.kelvinToNormalizedRgb(4000));
            put("3800k", ColorTemperatureConverter.kelvinToNormalizedRgb(3800));
            put("3600k", ColorTemperatureConverter.kelvinToNormalizedRgb(3600));
            put("3400k", ColorTemperatureConverter.kelvinToNormalizedRgb(3400));
            put("3200k", ColorTemperatureConverter.kelvinToNormalizedRgb(3200));
            put("3000k", ColorTemperatureConverter.kelvinToNormalizedRgb(3000));
            put("2800k", ColorTemperatureConverter.kelvinToNormalizedRgb(2800));
            put("2600k", ColorTemperatureConverter.kelvinToNormalizedRgb(2600));
            put("2400k", ColorTemperatureConverter.kelvinToNormalizedRgb(2400));
            put("2200k", ColorTemperatureConverter.kelvinToNormalizedRgb(2200));
            put("2000k", ColorTemperatureConverter.kelvinToNormalizedRgb(2000));
            put("1800k", ColorTemperatureConverter.kelvinToNormalizedRgb(1800));
            put("1600k", ColorTemperatureConverter.kelvinToNormalizedRgb(1600));
        }});
    }

    public void applyToCamera(String value) {
        RggbChannelVector colorCorrection = (RggbChannelVector) stringToValueMap.get(value);
        camera.state.setColorCorrection(colorCorrection);
    }
}
