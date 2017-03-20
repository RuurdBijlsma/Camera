package com.ruurdbijlsma.camera.CameraManager;

import android.hardware.camera2.params.RggbChannelVector;

/**
 * Gemaakt door ruurd op 19-3-2017.
 */

class CameraState {
    float exposureTime;
    float focusDistance;
    float ISO;
    RggbChannelVector colorCorrection;

    CameraState(float exposureTime, float focusDistance, float ISO, RggbChannelVector colorCorrection) {
        this.exposureTime = exposureTime;
        this.focusDistance = focusDistance;
        this.ISO = ISO;
        this.colorCorrection = colorCorrection;
    }
}
