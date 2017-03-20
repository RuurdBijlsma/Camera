package com.ruurdbijlsma.camera.CameraManager;

import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ImageSaver implements Runnable {
    /**
     * The JPEG image
     */
    private final Image mImage;
    /**
     * The file we save the image into.
     */
    private final File mFile;

    public ImageSaver(Image image, File file) {
        mImage = image;
        mFile = file;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                    Log.d("CAPTURE", "Saved image to " + mFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    public static int getOrientation(int rotation) {
        return 0;
    }

    public static void onSaved() {
    }

    public static File createFile(String directory) {
        Calendar c = Calendar.getInstance();
        String minute = String.valueOf(c.get(Calendar.MINUTE));
        if (minute.length() == 1)
            minute = "0" + minute;
        String time = c.get(Calendar.HOUR_OF_DAY) + minute;

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());

        String fileName = "IMG_" + formattedDate + "_" + time;
        File outputFolder = new File(imageFolder, directory);

        File file = new File(outputFolder, fileName + ".jpg");
        int i = 1;
        while (file.exists()) {
            file = new File(outputFolder, fileName + "_" + i++ + ".jpg");
        }

        lastTakenImage = file;
        return file;
    }

    public static File lastTakenImage;

    public static File imageFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

    public static final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            backgroundHandler.post(new ImageSaver(reader.acquireNextImage(), createFile("Camera")));
        }
    };


    private static HandlerThread backgroundThread;
    public static Handler backgroundHandler;

    /**
     * Starts a background thread and its {@link Handler}.
     */
    public static void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    public static void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}