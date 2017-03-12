package com.ruurdbijlsma.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gemaakt door ruurd op 10-3-2017.
 */

public class Camera {
    private final int LowerMsLimit = 1000 / 60;
    private final int UpperMsLimit = 1000 / 4;

    private CameraManager manager;
    private String[] ids;
    private Activity activity;
    private Surface surface;
    private CameraDevice device;
    private CameraCaptureSession currentSession;
    private Thread backgroundThread;

    public CameraState state = new CameraState() {
        @Override
        public void onChange() {
            if (isReady()) {
                backgroundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            restartPreview();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });
                backgroundThread.start();
            }
        }
    };

    private CameraDevice.StateCallback onCameraOpen = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull final CameraDevice camera) {
            try {
                startCapture(camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
        }
    };

    //Constructor -> open -> onCameraOpen -> startCapture -> startPreview
    public Camera(Activity activity, Surface surface) {
        this.activity = activity;
        this.surface = surface;
        try {
            open();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    boolean isReady() {
        return currentSession != null;
    }

    private boolean getPermission() {
        boolean hasPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
        if (!hasPermission && !shouldShowRationale)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
        return hasPermission;
    }

    private void open() throws CameraAccessException {
        manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        ids = manager.getCameraIdList();
        if (getPermission() && ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            manager.openCamera(ids[0], onCameraOpen, null);
    }

    private void startCapture(CameraDevice camera) throws CameraAccessException {
        device = camera;

        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera.getId());
        StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        assert configs != null;
        Size[] sizes = configs.getOutputSizes(SurfaceTexture.class);
        Size optimalSize = getOptimalPreviewSize(sizes, getScreenSize());

        ArrayList<Surface> surfaces = new ArrayList<>();
        surfaces.add(surface);

        CameraCaptureSession.StateCallback callback = new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull final CameraCaptureSession session) {
                try {
                    currentSession = session;
                    startPreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            }
        };

        if (isHardwareLevelSupported(characteristics, CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL))
            camera.createCaptureSession(surfaces, callback, null);
        else {
            CharSequence cs = "Camera2 not fully supported on this device";
            Toast.makeText(activity.getApplicationContext(), cs, Toast.LENGTH_LONG).show();
        }
    }

    private SingleTimer singleTimer = SingleTimer.getInstance();

    private void startPreview() throws CameraAccessException {
        final CaptureRequest captureRequest = getPreviewRequest();

        int msBetweenFrames = (int) (state.getExposureTime() * 1000);
        msBetweenFrames = Math.max(LowerMsLimit, msBetweenFrames);
        msBetweenFrames = Math.min(UpperMsLimit, msBetweenFrames);

        singleTimer.start(new TimerTask() {
            @Override
            public void run() {
                capture(captureRequest);
            }

            @Override
            public boolean cancel() {
                try {
                    currentSession.abortCaptures();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                return super.cancel();
            }
        }, msBetweenFrames);
    }

    private CaptureRequest getPreviewRequest() throws CameraAccessException {
        int type = CameraDevice.TEMPLATE_PREVIEW;
//        if (state.getExposureMode() == Mode.MANUAL || state.getFocusMode() == Mode.MANUAL)
//            type = CameraDevice.TEMPLATE_MANUAL;

        CaptureRequest.Builder captureRequestBuilder = device.createCaptureRequest(type);
        captureRequestBuilder.addTarget(surface);

        state.applyToRequestBuilder(captureRequestBuilder);

        return captureRequestBuilder.build();
    }

    private void restartPreview() throws CameraAccessException {
        startPreview();
    }


    private void capture(CaptureRequest request) {
        try {
            currentSession.capture(request, null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size getScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        return new Size(screenWidth, screenHeight);
    }

    private Size getOptimalPreviewSize(Size[] sizes, Size screenSize) {
        int h = screenSize.getHeight();
        int w = screenSize.getWidth();

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

    void permissionGranted() throws CameraAccessException {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            return;
        manager.openCamera(ids[0], onCameraOpen, null);
    }

    private boolean isHardwareLevelSupported(CameraCharacteristics c, int requiredLevel) {
        int deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
            return requiredLevel == deviceLevel;
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }
}