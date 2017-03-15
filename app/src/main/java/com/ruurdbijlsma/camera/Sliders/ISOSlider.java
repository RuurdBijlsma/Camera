package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.Camera;

import java.util.HashMap;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class ISOSlider extends ValueSlider {
    private Camera camera;

    public ISOSlider(Context context, Camera camera) {
        super(context);

        setValues(new String[]{
                "2700",
                "2000",
                "1500",
                "1000",
                "800",
                "700",
                "600",
                "500",
                "450",
                "400",
                "350",
                "300",
                "250",
                "200",
                "150",
                "100",
                "50",
        });

        this.camera = camera;
    }

    private void applyToCamera(String value){
        float ISO = stringToValue(value);
        camera.state.setISO(ISO);
    }

    @Override
    public float stringToValue(String string) {
        return super.stringToValue(string);
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
