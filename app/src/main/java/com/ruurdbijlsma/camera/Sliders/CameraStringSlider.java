package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.CameraManager.Camera;

import java.util.LinkedHashMap;

/**
 * Gemaakt door ruurd op 16-3-2017.
 */

public abstract class CameraStringSlider extends CameraValueSlider {
    protected LinkedHashMap stringToValueMap;

    public CameraStringSlider(Context context, Camera camera, LinkedHashMap values) {
        super(context, camera, (String[]) values.keySet().toArray(
                new String[values.keySet().size()]
        ));
        stringToValueMap = values;
    }

    @Override
    public float stringToValue(String string) {
        Object value = stringToValueMap.get(string);
        float result = 0;
        if (value instanceof Integer)
            result = (float) (int) value;
        else if (value instanceof Double)
            result = (float) (double) value;

        return result;
    }
}
