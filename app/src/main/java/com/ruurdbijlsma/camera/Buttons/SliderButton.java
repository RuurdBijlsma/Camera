package com.ruurdbijlsma.camera.Buttons;

import android.widget.ImageButton;

import com.ruurdbijlsma.camera.Sliders.CameraValueSlider;

/**
 * Gemaakt door ruurd op 19-3-2017.
 */

public class SliderButton extends Button {
    public boolean hasAutoButton;
    private CameraValueSlider slider;
    private ButtonManager manager;

    public SliderButton(ImageButton view, int inactiveResourceId, int activeResourceId, CameraValueSlider slider, ButtonManager manager, boolean hasAutoButton) {
        super(view, inactiveResourceId, activeResourceId);
        this.slider = slider;
        this.manager = manager;
        this.hasAutoButton = hasAutoButton;
    }

    @Override
    public void activate() {
        manager.deactivateAllSliderButtons();
        manager.setActiveSlider(slider);
        slider.applyToCamera(slider.getSelectedValue());
        if (hasAutoButton)
            manager.showAutoButton();
        super.activate();
    }

    @Override
    public void deactivate() {
        manager.hideAutoButton();
        manager.setActiveSlider(null);
        super.deactivate();
    }
}
