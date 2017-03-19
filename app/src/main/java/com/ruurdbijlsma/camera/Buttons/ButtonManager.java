package com.ruurdbijlsma.camera.Buttons;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.ruurdbijlsma.camera.R;
import com.ruurdbijlsma.camera.Sliders.ValueSlider;

/**
 * Gemaakt door ruurd op 19-3-2017.
 */

public class ButtonManager {
    private Button whiteBalanceButton;
    private Button focusButton;
    private Button exposureButton;
    private Button isoButton;
    private Button shutterButton;
    private Button aeLockButton;

    private FrameLayout layout;

    public ButtonManager(Activity activity, ValueSlider[] sliders, FrameLayout layout) {
        this.layout = layout;

        ImageButton imageButton;
        imageButton = (ImageButton) activity.findViewById(R.id.whiteBalanceButton);
        whiteBalanceButton = new SliderButton(imageButton, R.mipmap.wbbutton, R.mipmap.wbbuttonactive, sliders[0], this);

        imageButton = (ImageButton) activity.findViewById(R.id.focusButton);
        focusButton = new SliderButton(imageButton, R.mipmap.mfbutton, R.mipmap.mfbuttonactive, sliders[1], this);

        imageButton = (ImageButton) activity.findViewById(R.id.exposureButton);
        exposureButton = new SliderButton(imageButton, R.mipmap.expbutton, R.mipmap.expbuttonactive, sliders[2], this);

        imageButton = (ImageButton) activity.findViewById(R.id.isoButton);
        isoButton = new SliderButton(imageButton, R.mipmap.isobutton, R.mipmap.isobuttonactive, sliders[3], this);

        imageButton = (ImageButton) activity.findViewById(R.id.shutterButton);
        shutterButton = new SliderButton(imageButton, R.mipmap.shutterbutton, R.mipmap.shutterbuttonactive, sliders[4], this);

        imageButton = (ImageButton) activity.findViewById(R.id.aeLockButton);
        aeLockButton = new Button(imageButton, R.mipmap.aelockbutton, R.mipmap.aelockbuttonactive);
    }

    void deactivateAllSliderButtons() {
        whiteBalanceButton.deactivate();
        focusButton.deactivate();
        exposureButton.deactivate();
        isoButton.deactivate();
        shutterButton.deactivate();
    }

    void setActiveSlider(@Nullable ValueSlider slider) {
        layout.removeAllViews();

        if (slider != null && layout.getChildAt(0) != slider)
            layout.addView(slider);
    }
}
