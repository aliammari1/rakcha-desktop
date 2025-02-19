package com.esprit.utils;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FaceDetector implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(FaceDetector.class.getName());
    private static final String FRONTAL_FACE_CASCADE = "/haarcascades/haarcascade_frontalface_alt.xml";
    private static final String EYE_CASCADE = "/haarcascades/haarcascade_eye.xml";

    private final CascadeClassifier faceCascade;
    private final CascadeClassifier eyeCascade;
    private final VideoCapture videoCapture;
    private final AtomicBoolean running;
    private final FaceDetectionCallback callback;

    public interface FaceDetectionCallback {
        void onFaceDetected(Mat face);

        void onFrameProcessed(Mat frame);
    }

    public FaceDetector(FaceDetectionCallback callback) {
        this.callback = callback;
        this.running = new AtomicBoolean(false);

        // Initialize OpenCV classifiers
        this.faceCascade = new CascadeClassifier();
        this.eyeCascade = new CascadeClassifier();

        // Load the cascades from resources
        String faceCascadePath = getClass().getResource(FRONTAL_FACE_CASCADE).getPath();
        String eyeCascadePath = getClass().getResource(EYE_CASCADE).getPath();

        if (!faceCascade.load(faceCascadePath) || !eyeCascade.load(eyeCascadePath)) {
            throw new RuntimeException("Error loading cascade classifiers");
        }

        // Initialize video capture
        this.videoCapture = new VideoCapture(0);
        if (!this.videoCapture.isOpened()) {
            throw new RuntimeException("Error opening video capture device");
        }
    }

    @Override
    public void run() {
        running.set(true);
        Mat frame = new Mat();
        Mat gray = new Mat();

        while (running.get() && videoCapture.isOpened()) {
            try {
                if (videoCapture.read(frame)) {
                    // Convert to grayscale for detection
                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

                    // Detect faces
                    MatOfRect faces = new MatOfRect();
                    faceCascade.detectMultiScale(gray, faces, 1.1, 3, 0,
                            new Size(30, 30), new Size());

                    // Process detected faces
                    for (Rect faceRect : faces.toArray()) {
                        // Draw rectangle around face
                        Imgproc.rectangle(frame, faceRect, new Scalar(0, 255, 0), 2);

                        // Extract face region
                        Mat faceROI = new Mat(gray, faceRect);

                        // Detect eyes in face region
                        MatOfRect eyes = new MatOfRect();
                        eyeCascade.detectMultiScale(faceROI, eyes);

                        // If eyes are detected, consider it a valid face
                        if (eyes.toArray().length > 0) {
                            callback.onFaceDetected(faceROI);
                        }
                    }

                    callback.onFrameProcessed(frame);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing video frame", e);
            }
        }

        cleanup();
    }

    public void stop() {
        running.set(false);
    }

    private void cleanup() {
        if (videoCapture != null && videoCapture.isOpened()) {
            videoCapture.release();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        cleanup();
        super.finalize();
    }
}
