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
import android.widget.TextView;

import com.ruurdbijlsma.camera.Buttons.ButtonManager;
import com.ruurdbijlsma.camera.CameraManager.Camera;
import com.ruurdbijlsma.camera.Converters.ColorTemperatureConverter;
import com.ruurdbijlsma.camera.Converters.ExposureTimeConverter;
import com.ruurdbijlsma.camera.Sliders.CameraValueSlider;
import com.ruurdbijlsma.camera.Sliders.ColorCorrectionSlider;
import com.ruurdbijlsma.camera.Sliders.ExposureCompensationSlider;
import com.ruurdbijlsma.camera.Sliders.ExposureSlider;
import com.ruurdbijlsma.camera.Sliders.FocusSlider;
import com.ruurdbijlsma.camera.Sliders.ISOSlider;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

///Todo:
///Elke button binden naar actie
///Foto maak functionaliteit maken
///Als shutter time > 0.25s is niet preview updaten met nieuwe shutter time
///Als shutter time > 0.25s is pizzatje op de shutter knop laten zien om te zien hoe lang de capture nog duurt bij het maken van een foto

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    CameraValueSlider[] sliders;
    FrameLayout sliderLayout;
    ButtonManager buttonManager;
    Camera camera;
    private final int infoUpdateDelay = 500;
    private TextView whiteBalanceInfo;
    private TextView focusInfo;
    private TextView isoInfo;
    private TextView shutterInfo;
    private TextView apertureInfo;

    private void initialize() {
        sliders = createSliders();
        sliderLayout = (FrameLayout) findViewById(R.id.valueSlider);
        buttonManager = new ButtonManager(this, sliders, sliderLayout) {
            @Override
            public void unlockAe() {
                super.unlockAe();
                if (camera.isReady())
                    camera.state.setExposureMode(Mode.AUTO);
            }

            @Override
            public void lockAe() {
                super.lockAe();
                if (camera.isReady())
                    camera.state.setExposureMode(Mode.MANUAL);
            }
        };

        whiteBalanceInfo = (TextView) findViewById(R.id.wbInfo);
        focusInfo = (TextView) findViewById(R.id.focusInfo);
        isoInfo = (TextView) findViewById(R.id.isoInfo);
        shutterInfo = (TextView) findViewById(R.id.expInfo);
        apertureInfo = (TextView) findViewById(R.id.apertureInfo);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                int k = ColorTemperatureConverter.rgbNormalizedToKelvin(camera.state.autoState.colorCorrection);
                final int kelvin = Math.round(k / 100) * 100;
                final int focusDistance = (int) (100 / camera.state.autoState.focusDistance);
                final int iso = Math.round(camera.state.autoState.ISO / 50) * 50;
                final String expTime = ExposureTimeConverter.secondsToFraction(camera.state.autoState.exposureTime);
                final float aperture = camera.state.autoState.aperture;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        whiteBalanceInfo.setText(kelvin + "K");
                        focusInfo.setText(focusDistance + "cm");
                        isoInfo.setText("ISO" + iso);
                        shutterInfo.setText(expTime + "s");
                        apertureInfo.setText("F" + aperture);
                    }
                });
            }
        }, 0, infoUpdateDelay);

        setOnClickListeners();
    }

    public CameraValueSlider[] createSliders() {
        return new CameraValueSlider[]{
                new ColorCorrectionSlider(this, camera),
                new FocusSlider(this, camera),
                new ExposureCompensationSlider(this, camera),
                new ISOSlider(this, camera),
                new ExposureSlider(this, camera),
        };
    }

    void setOnClickListeners() {
        //Capture Button
        ImageButton captureButton = (ImageButton) findViewById(R.id.capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (camera.isReady()) {

                }
            }
        });
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
