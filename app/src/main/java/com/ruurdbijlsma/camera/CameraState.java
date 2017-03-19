package com.ruurdbijlsma.camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.view.Surface;

/**
 * Gemaakt door ruurd op 10-3-2017.
 */


public class CameraState {
    private Mode exposureMode;
    private Mode focusMode;
    private Mode whiteBalanceMode;

    private float exposureTime;
    private float focusDistance;
    private float ISO;
    private int whiteBalance;
    private int exposureCompensation;

    public CameraState() {
        exposureMode = Mode.AUTO;
        focusMode = Mode.AUTO;
        whiteBalanceMode = Mode.AUTO;

        exposureTime = 0.01f;
        focusDistance = 1;
        ISO = 800;
        whiteBalance = CaptureRequest.CONTROL_AWB_MODE_AUTO;
        exposureCompensation = 0;
    }

    void applyToRequestBuilder(CaptureRequest.Builder request) {
        if (exposureMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AE_MODE, 0);
            long nanoseconds = (long) (exposureTime * 1000000000);
            request.set(CaptureRequest.SENSOR_EXPOSURE_TIME, nanoseconds);
        }

        if (focusMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_AF_MODE, 0);
            request.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
        }

        if (exposureMode == Mode.MANUAL) {
            int isoValue = (int) ISO;
            request.set(CaptureRequest.SENSOR_SENSITIVITY, isoValue);
        } else {
            request.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, exposureCompensation);
        }

        if (whiteBalanceMode == Mode.MANUAL) {
            request.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            request.set(CaptureRequest.CONTROL_AWB_MODE, whiteBalance);
            Log.d("DEBUG", String.valueOf(request.get(CaptureRequest.CONTROL_AWB_MODE)));
        }
    }


    CaptureRequest getPreviewRequest(CameraDevice device, Surface surface) throws CameraAccessException {
        int type = CameraDevice.TEMPLATE_PREVIEW;

        CaptureRequest.Builder captureRequestBuilder = device.createCaptureRequest(type);
        captureRequestBuilder.addTarget(surface);

        applyToRequestBuilder(captureRequestBuilder);

        return captureRequestBuilder.build();
    }

    public void onChange() {
    }

    public void setExposureCompensation(int exposureCompensation) {
        this.exposureCompensation = exposureCompensation;
        onChange();
    }

    public void setExposureTime(float exposureTime) {
        this.exposureTime = exposureTime;
        exposureMode = Mode.MANUAL;
        onChange();
    }

    public void setISO(float ISO) {
        this.ISO = ISO;
        exposureMode = Mode.MANUAL;
        onChange();
    }

    public void setWhiteBalance(int whiteBalance) {
        this.whiteBalance = whiteBalance;
        whiteBalanceMode = Mode.MANUAL;
        onChange();
    }

    public void setFocusDistanceInMeters(float focusDistance) {
        this.focusDistance = 1 / focusDistance;
        focusMode = Mode.MANUAL;
        onChange();
    }

    public float getFocusDistanceInMeters() {
        return 1 / focusDistance;
    }

    public Mode getExposureMode() {
        return exposureMode;
    }

    public Mode getFocusMode() {
        return focusMode;
    }

    public float getExposureTime() {
        return exposureTime;
    }

    public void setExposureMode(Mode exposureMode) {
        this.exposureMode = exposureMode;
        onChange();
    }
}
