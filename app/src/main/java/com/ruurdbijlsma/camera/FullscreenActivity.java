package com.ruurdbijlsma.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

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

    private SurfaceHolder holder;
    private CameraManager cameraManager;
    private String[] cameraIds;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();

        final Activity context = this;
        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    cameraIds = cameraManager.getCameraIdList();
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            System.out.println("HOI");
                        } else
                            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
                    } else {
                        //Permission is already granted
                        cameraManager.openCamera(cameraIds[0], onCameraOpen, null);
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                System.out.println("Wat een kutcode");
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                return;
            try {
                cameraManager.openCamera(cameraIds[0], onCameraOpen, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private CameraDevice.StateCallback onCameraOpen = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(final CameraDevice camera) {
            try {
                final CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(camera.getId());
                StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert configs != null;

//                Get optimal preview size
                Size[] sizes = configs.getOutputSizes(SurfaceTexture.class);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels;
                int screenWidth = displayMetrics.widthPixels;
                Size optimalSize = getOptimalPreviewSize(sizes, screenWidth, screenHeight);


                ArrayList<Surface> surfaces = new ArrayList<>();
                surfaces.add(holder.getSurface());

                CameraCaptureSession.StateCallback callback = new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(final CameraCaptureSession session) {
                        try {

                            CaptureRequest.Builder captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                            captureRequestBuilder.addTarget(holder.getSurface());

                            //turn on manual controls
                            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, 0);
                            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, 0);

                            //set exposure time
                            long exposureTime = (long) (0.01 * 1000000000);
                            captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
                            long expTime = captureRequestBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
                            long frameDuration = captureRequestBuilder.get(CaptureRequest.SENSOR_FRAME_DURATION);
                            final CaptureRequest captureRequest = captureRequestBuilder.build();

                            Timer t = new Timer();
                            t.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        session.capture(captureRequest, null, null);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 0, 1000 / 60);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {
                        System.out.println("niet CONFUGIFE");
                    }
                };

                if (isHardwareLevelSupported(characteristics, CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)) {
                    camera.createCaptureSession(surfaces, callback, null);
                } else {
                    System.out.println("BAD PHONE");
                }
                System.out.println("OPEN");
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            System.out.println("Disconnect");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            System.out.println("Error");
        }
    };

    boolean isHardwareLevelSupported(CameraCharacteristics c, int requiredLevel) {
        int deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    private Size getOptimalPreviewSize(Size[] sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Size size : sizes) {
            double ratio = (double) size.getWidth() / size.getHeight();
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.getHeight() - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.getHeight() - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.getHeight() - h);
                }
            }
        }
        return optimalSize;
    }
}
