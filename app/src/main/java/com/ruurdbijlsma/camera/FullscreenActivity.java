package com.ruurdbijlsma.camera;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.Objects;

//// TODO: 11-3-2017 Een scrollview per valueslider instance maken zodat hij de positie onthoud. Die scrollview kan dan in framelayout ofzo. Als dit niet kan, scrollpositie onthouden en weer terug zetten als hij wordt geactiveerd

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    ImageButton captureButton;
    ValueSlider[] sliders;
    FrameLayout sliderLayout;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        createSliders();


        sliderLayout = (FrameLayout) findViewById(R.id.valueSlider);
        sliderLayout.addView(sliders[0].getScrollView());

        captureButton = (ImageButton) findViewById(R.id.capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (camera.isReady()) {

                }
            }
        });
    }

    public void createSliders() {
        sliders = new ValueSlider[1];
        sliders[0] = new ValueSlider(this, new String[]{
                "1/6000",
                "1/4000",
                "1/2000",
                "1/1000",
                "1/500",
                "1/250",
                "1/125",
                "1/60",
                "1/30",
                "1/15",
                "1/8",
                "1/4",
                "1/2",
                "1",
                "2",
                "4",
                "8",
                "15",
                "30",
        }) {
            @Override
            void onValueChange(String newValue, String oldValue) {
                super.onValueChange(newValue, oldValue);
            }

            @Override
            void onScrollEnd(float scrollPosition) {
                super.onScrollEnd(scrollPosition);
            }
        };
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
