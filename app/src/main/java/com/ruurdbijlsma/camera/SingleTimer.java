package com.ruurdbijlsma.camera;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Gemaakt door ruurd op 12-3-2017.
 */

public class SingleTimer {
    private static final SingleTimer ourInstance = new SingleTimer();

    public synchronized static SingleTimer getInstance() {
        return ourInstance;
    }

    private Timer timer;
    private boolean isRunning = false;
    private TimerTask timerTask;

    private SingleTimer() {
    }

    public void start(TimerTask timerTask, int delay) {
        if(isRunning)
            stop();

        this.timerTask = timerTask;
        timer = new Timer();
        isRunning = true;
        timer.schedule(timerTask, 0, delay);
    }

    public void stop() {
        timer.cancel();
        timer.purge();
        timerTask.cancel();
        isRunning = false;
    }

    public void changeDelay(int newDelay) {
        start(timerTask, newDelay);
    }
}
