package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;

import com.ruurdbijlsma.camera.Camera;

import java.util.HashMap;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class WhiteBalanceSlider extends CameraStringSlider {
    public WhiteBalanceSlider(Context context, Camera camera) {
        super(context, camera, new HashMap() {{
            put("Cloudy", CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
            put("Warm Fluorescent", CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT);
            put("Twilight", CaptureRequest.CONTROL_AWB_MODE_TWILIGHT);
            put("Shade", CaptureRequest.CONTROL_AWB_MODE_SHADE);
            put("Incandescent", CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT);
            put("Daylight", CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT);
            put("Fluorescent", CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);
            put("Off", CaptureRequest.CONTROL_AWB_MODE_OFF);
            put("Auto", CaptureRequest.CONTROL_AWB_MODE_AUTO);
        }});
    }

    protected void applyToCamera(String value) {
        float balanceMode = stringToValue(value);
        camera.state.setWhiteBalance((int) balanceMode);
    }
}
