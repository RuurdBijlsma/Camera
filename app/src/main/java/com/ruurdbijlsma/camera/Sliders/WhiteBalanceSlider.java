package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;

import com.ruurdbijlsma.camera.CameraManager.Camera;

import java.util.LinkedHashMap;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class WhiteBalanceSlider extends CameraStringSlider {
    public WhiteBalanceSlider(Context context, Camera camera) {
        super(context, camera, new LinkedHashMap() {{
            put("Off", CaptureRequest.CONTROL_AWB_MODE_OFF);//0
            put("Auto", CaptureRequest.CONTROL_AWB_MODE_AUTO);//1
            put("Incandescent", CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT);//2
            put("Fluorescent", CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);//3
            put("Warm Fluorescent", CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT);//4
            put("Daylight", CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT);//5
            put("Cloudy", CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);//6
            put("Twilight", CaptureRequest.CONTROL_AWB_MODE_TWILIGHT);//7
            put("Shade", CaptureRequest.CONTROL_AWB_MODE_SHADE);//8
        }});
    }

    public void applyToCamera(String value) {
        float balanceMode = stringToValue(value);
        camera.state.setWhiteBalance((int) balanceMode);
    }
}
