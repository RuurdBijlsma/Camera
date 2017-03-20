package com.ruurdbijlsma.camera.Buttons;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.ruurdbijlsma.camera.R;
import com.ruurdbijlsma.camera.Sliders.CameraValueSlider;
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
    private Activity activity;
    private ImageButton autoButton;

    public ButtonManager(final Activity activity, CameraValueSlider[] sliders, FrameLayout layout) {
        this.layout = layout;
        autoButton = (ImageButton) activity.findViewById(R.id.autoButton);

        ImageButton imageButton;
        imageButton = (ImageButton) activity.findViewById(R.id.whiteBalanceButton);
        whiteBalanceButton = new SliderButton(imageButton, R.mipmap.wbbutton, R.mipmap.wbbuttonactive, sliders[0], this, true);

        imageButton = (ImageButton) activity.findViewById(R.id.focusButton);
        focusButton = new SliderButton(imageButton, R.mipmap.mfbutton, R.mipmap.mfbuttonactive, sliders[1], this, true);

        imageButton = (ImageButton) activity.findViewById(R.id.exposureButton);
        exposureButton = new SliderButton(imageButton, R.mipmap.expbutton, R.mipmap.expbuttonactive, sliders[2], this, false);

        imageButton = (ImageButton) activity.findViewById(R.id.isoButton);
        isoButton = new SliderButton(imageButton, R.mipmap.isobutton, R.mipmap.isobuttonactive, sliders[3], this, false);

        imageButton = (ImageButton) activity.findViewById(R.id.shutterButton);
        shutterButton = new SliderButton(imageButton, R.mipmap.shutterbutton, R.mipmap.shutterbuttonactive, sliders[4], this, false);

        imageButton = (ImageButton) activity.findViewById(R.id.aeLockButton);
        aeLockButton = new Button(imageButton, R.mipmap.aelockbutton, R.mipmap.aelockbuttonactive) {
            @Override
            public void activate() {
                super.activate();
                lockAe();
            }

            @Override
            public void deactivate() {
                super.deactivate();
                unlockAe();
            }
        };

        aeLockButton.deactivate();
    }

    void hideAutoButton() {
        autoButton.setVisibility(View.GONE);
    }

    void showAutoButton() {
        autoButton.setVisibility(View.VISIBLE);
    }

    public void unlockAe() {
        deactivateAllSliderButtons();
        shutterButton.disable();
        isoButton.disable();
        exposureButton.enable();
    }

    public void lockAe() {
        deactivateAllSliderButtons();
        shutterButton.enable();
        isoButton.enable();
        exposureButton.disable();
    }

    public void deactivateAllSliderButtons() {
        whiteBalanceButton.deactivate();
        focusButton.deactivate();
        exposureButton.deactivate();
        isoButton.deactivate();
        shutterButton.deactivate();
        setActiveSlider(null);
    }

    public Button getActiveSliderButton() {
        if (whiteBalanceButton.isActive)
            return whiteBalanceButton;
        if (focusButton.isActive)
            return focusButton;
        if (exposureButton.isActive)
            return exposureButton;
        if (isoButton.isActive)
            return isoButton;
        if (shutterButton.isActive)
            return shutterButton;
        return null;
    }

    void setActiveSlider(@Nullable ValueSlider slider) {
        layout.removeAllViews();

        if (slider != null && layout.getChildAt(0) != slider)
            layout.addView(slider);
    }
}
