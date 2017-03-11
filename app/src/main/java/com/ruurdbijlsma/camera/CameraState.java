package com.ruurdbijlsma.camera;

import android.hardware.camera2.CaptureRequest;

/**
 * Gemaakt door ruurd op 10-3-2017.
 */


class CameraState {
    Mode exposureMode;
    Mode focusMode;
    Mode whiteBalanceMode;

    long exposureTime;
    private float focusDistance;

    CameraState() {
        exposureMode = Mode.AUTO;
        focusMode = Mode.AUTO;
        whiteBalanceMode=Mode.MANUAL;

        exposureTime = (long) (0.01 * 1000000000);
        setFocusDistanceInMeters(0.01f);
    }

    float getFocusDistanceInMeters() {
        return 1 / focusDistance;
    }

    void setFocusDistanceInMeters(float focusDistance) {
        this.focusDistance = 1 / focusDistance;
    }

    void applyToRequestBuilder(CaptureRequest.Builder request) {
        if (exposureMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AE_MODE, 0);
            request.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
        }

        if (focusMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AF_MODE, 0);
            request.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
        }

        if (whiteBalanceMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);
        }
    }
}
