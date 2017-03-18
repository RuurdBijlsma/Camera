package com.ruurdbijlsma.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.ruurdbijlsma.camera.Sliders.ExposureSlider;
import com.ruurdbijlsma.camera.Sliders.FocusSlider;
import com.ruurdbijlsma.camera.Sliders.ISOSlider;
import com.ruurdbijlsma.camera.Sliders.ValueSlider;
import com.ruurdbijlsma.camera.Sliders.WhiteBalanceSlider;

import java.util.Objects;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    ImageButton captureButton;
    ValueSlider[] sliders;
    FrameLayout sliderLayout;
    Camera camera;

    private void initialize() {
        sliders = createSliders();
        sliderLayout = (FrameLayout) findViewById(R.id.valueSlider);

        captureButton = (ImageButton) findViewById(R.id.capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (camera.isReady()) {

                }
            }
        });

        setActiveSlider(sliders[0]);
    }

    void setActiveSlider(ValueSlider slider) {
        if (sliderLayout.getChildAt(0) != slider) {
            sliderLayout.removeAllViews();
            sliderLayout.addView(slider);
        }
    }

    public ValueSlider[] createSliders() {
        return new ValueSlider[]{
                new ExposureSlider(this, camera),
                new FocusSlider(this, camera),
                new ISOSlider(this, camera),
                new WhiteBalanceSlider(this, camera),
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        final Activity context = this;

        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                camera = new Camera(context, holder.getSurface());
                initialize();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        };

        holder.addCallback(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Objects.equals(permissions[0], "android.permission.CAMERA")) {
            try {
                camera.permissionGranted();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
