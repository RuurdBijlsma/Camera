package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;

import com.ruurdbijlsma.camera.Camera;

import java.util.HashMap;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class WhiteBalanceSlider extends ValueSlider {
    private Camera camera;
    private HashMap stringToValueMap = new HashMap() {{
        put("Cloudy", CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
        put("Warm Fluorescent", CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT);
        put("Twilight", CaptureRequest.CONTROL_AWB_MODE_TWILIGHT);
        put("Shade", CaptureRequest.CONTROL_AWB_MODE_SHADE);
        put("Incandescent", CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT);
        put("Daylight", CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT);
        put("Fluorescent", CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);
        put("Off", CaptureRequest.CONTROL_AWB_MODE_OFF);
        put("Auto", CaptureRequest.CONTROL_AWB_MODE_AUTO);
    }};

    public WhiteBalanceSlider(Context context, Camera camera) {
        super(context);

        setValues(new String[]{
                "Cloudy",
                "Warm Fluorescent",
                "Twilight",
                "Shade",
                "Incandescent",
                "Daylight",
                "Fluorescent",
                "Off",
        });

        this.camera = camera;
    }

    @Override
    public float stringToValue(String string) {
        Object value = stringToValueMap.get(string);
        float result = 0;
        if (value instanceof Integer)
            result = (float) (int) value;
        if (value instanceof Double)
            result = (float) (double) value;
        return result;
    }


    private void applyToCamera(String value){
        float cc = stringToValue(value);
        camera.state.setWhiteBalance((int) cc);
    }

    @Override
    public void onValueChange(String newValue, String oldValue) {
//        applyToCamera(newValue);
    }

    @Override
    public void onScrollEnd(float scrollPosition) {
        String value = getSelectedValue(scrollPosition);
        applyToCamera(value);
    }
}
