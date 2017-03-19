package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.Camera;

/**
 * Gemaakt door ruurd op 16-3-2017.
 */

public abstract class CameraValueSlider extends ValueSlider {

    protected Camera camera;

    public CameraValueSlider(Context context, Camera camera) {
        super(context);
        this.camera = camera;
    }

    public CameraValueSlider(Context context, Camera camera, String[] values) {
        super(context, values);
        this.camera = camera;
    }

    public abstract void applyToCamera(String value);

    @Override
    public void onValueChange(String newValue, String oldValue) {
        applyToCamera(newValue);
    }
}
