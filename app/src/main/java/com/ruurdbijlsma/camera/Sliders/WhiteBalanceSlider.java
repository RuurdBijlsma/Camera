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

    public WhiteBalanceSlider(Context context, Camera camera) {
        super(context);

        setValues(new String[]{
                "1",
                "2",
                "3",
        });

        this.camera = camera;
    }

    private void applyToCamera(String value){
        float cc = stringToValue(value);
        camera.state.setWhiteBalance(cc);
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
