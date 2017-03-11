package com.ruurdbijlsma.camera;

import android.hardware.camera2.CaptureRequest;

/**
 * Gemaakt door ruurd op 10-3-2017.
 */


public class CameraState {
    private Mode exposureMode;
    private Mode focusMode;
    private Mode whiteBalanceMode;

    private float exposureTime;
    private float focusDistance;

    public CameraState() {
        exposureMode = Mode.AUTO;
        focusMode = Mode.AUTO;
        whiteBalanceMode = Mode.AUTO;

        exposureTime = 0.01f;
        focusDistance = 1;
    }

    public float getFocusDistanceInMeters() {
        return 1 / focusDistance;
    }

    public void setFocusDistanceInMeters(float focusDistance) {
        this.focusDistance = 1 / focusDistance;
        focusMode = Mode.MANUAL;
        onChange();
    }

    void applyToRequestBuilder(CaptureRequest.Builder request) {
        if (exposureMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AE_MODE, 0);
            long nanoseconds =(long) (exposureTime * 1000000000);
            request.set(CaptureRequest.SENSOR_EXPOSURE_TIME, nanoseconds);
        }

        if (focusMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AF_MODE, 0);
            request.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
        }

        if (whiteBalanceMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);
        }
    }

    public void onChange() {
    }

    public float getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(float exposureTime) {
        this.exposureTime = exposureTime;
        exposureMode = Mode.MANUAL;
        onChange();
    }

    public Mode getExposureMode() {
        return exposureMode;
    }

    public Mode getFocusMode() {
        return focusMode;
    }
}
