package com.jjoe64.motiondetection.motiondetection;

import java.io.IOException;

public interface MotionDetectorCallback {
    void onMotionDetected(int[] img, int width, int height, byte[] bytes) throws IOException;
    void onTooDark();
}
