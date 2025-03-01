package com.example.ewaste.Repository;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import java.io.*;
import java.nio.IntBuffer;
import java.util.*;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;

public class FaceRecognitionRepository {

    private final LBPHFaceRecognizer recognizer;
    private Map<Integer, Integer> labelToUserId = new HashMap<>();
    private static final String FACES_DIR = "C:/Users/User/Documents/e-waste/e-waste/faces/";
    private static final String MODEL_FILE = "C:/Users/User/Documents/e-waste/e-waste/model.xml";
    private static final String LABELS_FILE = "C:/Users/User/Documents/e-waste/e-waste/labels.txt";

    public FaceRecognitionRepository() {
        this.recognizer = LBPHFaceRecognizer.create();
        File modelFile = new File(MODEL_FILE);
        if (modelFile.exists()) {
            recognizer.read(MODEL_FILE);
            System.out.println("Model loaded from " + MODEL_FILE);
            loadLabels(); // Load labelToUserId from file
        } else {
            System.out.println("No model file found at " + MODEL_FILE);
        }
    }

    private void loadLabels() {
        File labelsFile = new File(LABELS_FILE);
        if (labelsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(labelsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        int label = Integer.parseInt(parts[0]);
                        int userId = Integer.parseInt(parts[1]);
                        labelToUserId.put(label, userId);
                    }
                }
                System.out.println("Loaded labels: " + labelToUserId);
            } catch (IOException e) {
                System.err.println("Failed to load labels: " + e.getMessage());
            }
        } else {
            System.out.println("No labels file found at " + LABELS_FILE);
        }
    }

    private void saveLabels() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LABELS_FILE))) {
            for (Map.Entry<Integer, Integer> entry : labelToUserId.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            System.out.println("Saved labels to " + LABELS_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save labels: " + e.getMessage());
        }
    }

    public void trainModel() {
        System.out.println("Starting model training...");
        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        File facesDir = new File(FACES_DIR);
        if (!facesDir.exists() || !facesDir.isDirectory()) {
            System.out.println("Faces directory not found: " + FACES_DIR);
            return;
        }

        File[] userDirs = facesDir.listFiles(File::isDirectory);
        if (userDirs == null) {
            System.out.println("No user directories found in " + FACES_DIR);
            return;
        }

        labelToUserId.clear();
        int label = 0;
        for (File userDir : userDirs) {
            try {
                Integer userId = Integer.parseInt(userDir.getName());
                File[] imageFiles = userDir.listFiles((d, name) -> name.endsWith(".jpg"));
                if (imageFiles != null && imageFiles.length > 0) {
                    labelToUserId.put(label, userId);
                    System.out.println("Training userId: " + userId + " with " + imageFiles.length + " images");
                    for (File imageFile : imageFiles) {
                        Mat img = opencv_imgcodecs.imread(imageFile.getPath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
                        if (!img.empty()) {
                            images.add(img);
                            labels.add(label);
                        }
                    }
                    label++;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid userId directory: " + userDir.getName());
            }
        }

        if (!images.isEmpty()) {
            System.out.println("Training with " + images.size() + " images");
            MatVector matVector = new MatVector(images.size());
            for (int i = 0; i < images.size(); i++) {
                matVector.put(i, images.get(i));
            }
            Mat labelsMat = new Mat(labels.size(), 1, CV_32SC1);
            IntBuffer buffer = labelsMat.createBuffer();
            for (int i = 0; i < labels.size(); i++) {
                buffer.put(i, labels.get(i));
            }
            recognizer.train(matVector, labelsMat);
            recognizer.write(MODEL_FILE);
            saveLabels(); // Save the label mapping
            System.out.println("Model training completed and saved to " + MODEL_FILE);
        } else {
            System.out.println("No images found for training");
        }
    }

    public void trainModelInBackground() {
        new Thread(this::trainModel).start();
    }

    public Integer recognizeFace(Mat faceImage) {
        if (labelToUserId.isEmpty()) {
            System.out.println("No training data available - labelToUserId is empty");
            return null;
        }
        int[] label = new int[1];
        double[] confidence = new double[1];
        recognizer.predict(faceImage, label, confidence);
        System.out.println("Prediction - Label: " + label[0] + ", Confidence: " + confidence[0]);
        System.out.println("Available labels: " + labelToUserId.keySet());

        // Adjust threshold to 100 (or tune based on your data)
        double CONFIDENCE_THRESHOLD = 80.0;
        if (confidence[0] < CONFIDENCE_THRESHOLD && label[0] >= 0 && labelToUserId.containsKey(label[0])) {
            Integer userId = labelToUserId.get(label[0]);
            System.out.println("Accepted - Recognized userId: " + userId + " with confidence: " + confidence[0]);
            return userId;
        }
        System.out.println("Rejected - Confidence (" + confidence[0] + ") >= " + CONFIDENCE_THRESHOLD + " or invalid label");
        return null;
    }
}