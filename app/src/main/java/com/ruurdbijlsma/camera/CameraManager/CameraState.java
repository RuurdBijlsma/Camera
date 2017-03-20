package com.ruurdbijlsma.camera.CameraManager;

import android.hardware.camera2.params.RggbChannelVector;

import com.ruurdbijlsma.camera.Converters.ColorTemperatureConverter;

/**
 * Gemaakt door ruurd op 19-3-2017.
 */

public class CameraState {
    public float exposureTime;
    public float focusDistance;
    public float ISO;
    public RggbChannelVector colorCorrection;
    public float aperture = 1.8f;

    CameraState(float exposureTime, float focusDistance, float ISO, RggbChannelVector colorCorrection) {
        this.exposureTime = exposureTime;
        this.focusDistance = focusDistance;
        this.ISO = ISO;
        this.colorCorrection = colorCorrection;
    }

    CameraState() {
        this.exposureTime = 0.01f;
        this.focusDistance = 1;
        this.ISO = 800;
        this.colorCorrection = ColorTemperatureConverter.kelvinToRgb(6600);
    }
}
