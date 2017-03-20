package com.ruurdbijlsma.camera.CameraManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.RggbChannelVector;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;
import android.widget.ImageButton;

import com.ruurdbijlsma.camera.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Gemaakt door ruurd op 10-3-2017.
 */

public class Camera {
    private CameraManager manager;
    private String[] ids;
    private Activity activity;
    private Surface surface;
    private CameraDevice device;
    private CameraCaptureSession currentSession;
    public CameraStateManager state = new CameraStateManager() {
        @Override
        public void onChange() {
            if (isReady()) {
                try {
                    startPreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Semaphore cameraLock = new Semaphore(1);
    private ImageReader imageReader;
    private CameraDevice.StateCallback onCameraOpen = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull final CameraDevice camera) {
            try {
                cameraLock.release();
                startCapture(camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraLock.release();
            device.close();
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraLock.release();
            device.close();
            device = null;
            if (null != activity) {
                activity.finish();
            }
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

    public boolean isReady() {
        return currentSession != null;
    }

    private boolean getCameraPermission() {
        boolean hasPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
        if (!hasPermission && !shouldShowRationale)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
        return hasPermission;
    }

    private boolean getStoragePermission() {
        boolean hasPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!hasPermission && !shouldShowRationale)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        return hasPermission;
    }

    public void open() throws CameraAccessException {
        manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        ids = manager.getCameraIdList();
        if (getCameraPermission() && ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                if (!cameraLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Time out waiting to lock camera opening.");
                }
                manager.openCamera(ids[0], onCameraOpen, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        getStoragePermission();
    }

    public void close() {
        try {
            cameraLock.acquire();
            if (null != currentSession) {
                currentSession.close();
                currentSession = null;
            }
            if (null != device) {
                device.close();
                device = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            cameraLock.release();
        }
    }

    private void startCapture(CameraDevice camera) throws CameraAccessException {
        device = camera;

        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(device.getId());

//        float[] a= characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
//        float[] b= characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
//        Range<Long> c= characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
//        int[] d= characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
//        int e= characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
//
//        int m1=CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_FULL;
//        int m2=CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_OFF;
//        int m3=CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE;
        Integer range = characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY);

        StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        assert configs != null;
        Size[] sizes = configs.getOutputSizes(SurfaceTexture.class);
        Size optimalSize = getOptimalPreviewSize(sizes, getScreenSize());


        //Setup image reader
        StreamConfigurationMap map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size largest = Collections.max(
                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                new CompareSizesByArea());
        imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(ImageSaver.onImageAvailableListener, ImageSaver.backgroundHandler);


        ArrayList<Surface> surfaces = new ArrayList<>();
        surfaces.add(surface);
        surfaces.add(imageReader.getSurface());

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

        if (isHardwareLevelSupported(characteristics, CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)) {
            camera.createCaptureSession(surfaces, callback, null);
        } else {
            CharSequence cs = "Camera2 not fully supported on this device";
            Snackbar.make(activity.findViewById(R.id.surfaceView), cs, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void startPreview() throws CameraAccessException {
        CaptureRequest request = state.getPreviewRequest(device, surface);
        final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                onPreviewCaptureResult(session, request, result);
            }
        };

        currentSession.setRepeatingRequest(request, captureCallback, new Handler());
    }

    private void onPreviewCaptureResult(CameraCaptureSession session, CaptureRequest request, CaptureResult result) {
        float expTime = (float) result.get(CaptureResult.SENSOR_EXPOSURE_TIME) / 1000000000;
        float focusDistance = result.get(CaptureResult.LENS_FOCUS_DISTANCE);
        int ISO = result.get(CaptureResult.SENSOR_SENSITIVITY);
        float aperture = result.get(CaptureResult.LENS_APERTURE);
        RggbChannelVector colorCorrectionGains = result.get(CaptureResult.COLOR_CORRECTION_GAINS);

        state.autoState.colorCorrection = colorCorrectionGains;
        state.autoState.ISO = ISO;
        state.autoState.focusDistance = focusDistance;
        state.autoState.exposureTime = expTime;
        state.autoState.aperture = aperture;
    }

    public void captureStillImage() {
        final ImageButton captureButton = (ImageButton) activity.findViewById(R.id.capture);
        captureButton.setImageResource(R.mipmap.captureactive);
        try {
            if (null == activity || null == device) {
                return;
            }
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    captureButton.setImageResource(R.mipmap.capture);
                    try {
                        startPreview();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
//                    unlockFocus();
                }
            };

            currentSession.stopRepeating();
            currentSession.capture(state.getCaptureRequest(device, imageReader.getSurface()), captureCallback, null);
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

    public void permissionGranted() throws CameraAccessException {
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