package com.ruurdbijlsma.camera.Buttons;

import android.view.View;
import android.widget.ImageButton;

/**
 * Gemaakt door ruurd op 19-3-2017.
 */

public class Button {
    private ImageButton view;
    private int activeResourceId;
    private int inactiveResourceId;
    private boolean isActive;

    public Button(ImageButton view, int inactiveResourceId, int activeResourceId) {
        this.view = view;
        this.activeResourceId = activeResourceId;
        this.inactiveResourceId = inactiveResourceId;
        isActive = false;


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnClick();
            }
        });
    }

    public void handleOnClick() {
        toggle();
    }

    public void toggle() {
        if (isActive)
            deactivate();
        else
            activate();
    }

    public void activate() {
        view.setImageResource(activeResourceId);
        isActive = true;
    }

    public void deactivate() {
        view.setImageResource(inactiveResourceId);
        isActive = false;
    }
}
