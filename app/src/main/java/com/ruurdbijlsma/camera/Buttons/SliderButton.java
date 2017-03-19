package com.ruurdbijlsma.camera.Buttons;

import android.widget.ImageButton;

import com.ruurdbijlsma.camera.Sliders.ValueSlider;

/**
 * Gemaakt door ruurd op 19-3-2017.
 */

public class SliderButton extends Button {
    private ValueSlider slider;
    private ButtonManager manager;

    public SliderButton(ImageButton view, int inactiveResourceId, int activeResourceId, ValueSlider slider, ButtonManager manager) {
        super(view, inactiveResourceId, activeResourceId);
        this.slider = slider;
        this.manager = manager;
    }

    @Override
    public void activate() {
        manager.deactivateAllSliderButtons();
        manager.setActiveSlider(slider);
        super.activate();
    }

    @Override
    public void deactivate() {
        manager.setActiveSlider(null);
        super.deactivate();
    }
}
