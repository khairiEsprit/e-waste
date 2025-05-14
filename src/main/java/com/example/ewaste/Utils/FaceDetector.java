package com.example.ewaste.Utils;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class FaceDetector {
    private CascadeClassifier faceCascade;
    public FaceDetector(String cascadePath) {
        this.faceCascade = new CascadeClassifier(cascadePath);
        if (faceCascade.empty()) {
            throw new RuntimeException("Failed to load Haar cascade: " + cascadePath);
        }
    }

    public Mat detectAndExtractFace(Mat frame) {
        Mat grayFrame = new Mat();
        opencv_imgproc.cvtColor(frame, grayFrame, opencv_imgproc.COLOR_BGR2GRAY);
        RectVector faces = new RectVector();
        faceCascade.detectMultiScale(grayFrame, faces);
        System.out.println("Detected faces: " + faces.size());
        if (faces.size() == 1) {
            Rect rect = faces.get(0);
            Mat face = new Mat(grayFrame, rect);
            Mat resized = new Mat();
            opencv_imgproc.resize(face, resized, new Size(100, 100));
            return resized;
        }
        return null; // Returns null if no faces or multiple faces
    }
}