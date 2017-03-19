package com.ruurdbijlsma.camera.Sliders;

import android.content.Context;

import com.ruurdbijlsma.camera.Camera;

/**
 * Gemaakt door ruurd op 11-3-2017.
 */

public class ISOSlider extends CameraValueSlider {
    public ISOSlider(Context context, Camera camera) {
        super(context, camera, new String[]{
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
    }

    public void applyToCamera(String value) {
        float ISO = stringToValue(value);
        camera.state.setISO(ISO);
    }
}
