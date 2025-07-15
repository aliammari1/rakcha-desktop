// package com.esprit.utils;

// import java.io.File;
// import java.io.FilenameFilter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.logging.Level;
// import java.util.logging.Logger;

// import org.opencv.core.*;
// import org.opencv.face.LBPHFaceRecognizer;
// import org.opencv.imgcodecs.Imgcodecs;
// import org.opencv.imgproc.Imgproc;

// /**
//  * Utility class providing helper methods for the RAKCHA application. Contains
//  * reusable functionality and common operations.
//  *
//  * @author RAKCHA Team
//  * @version 1.0.0
//  * @since 1.0.0
//  */
// public class FaceRecognition {
//     private static final Logger LOGGER = Logger.getLogger(FaceRecognition.class.getName());
//     private static final String FACES_DIR = "./faces";
//     private final LBPHFaceRecognizer faceRecognizer;
//     private boolean isInitialized = false;

//     static {
//         // Load OpenCV native library
//         System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//     }

//     /**
//      * Constructs a new FaceRecognition instance.
//      * Initializes the LBPH face recognizer.
//      */
//     public FaceRecognition() {
//         this.faceRecognizer = LBPHFaceRecognizer.create();
//     }

//     /**
//      * Initialize the face recognizer with training data
//      *
//      * @return true if initialization was successful
//      */
//     public boolean init() {
//         File root = new File(FACES_DIR);
//         if (!root.exists() || !root.isDirectory()) {
//             LOGGER.severe("Training directory not found: " + FACES_DIR);
//             return false;
//         }

//         FilenameFilter imgFilter = (dir, name) -> {
//             String lowercaseName = name.toLowerCase();
//             return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".png");
//         };

//         File[] imageFiles = root.listFiles(imgFilter);
//         if (imageFiles == null || imageFiles.length == 0) {
//             LOGGER.severe("No training images found in " + FACES_DIR);
//             return false;
//         }

//         List<Mat> images = new ArrayList<>();
//         List<Integer> labels = new ArrayList<>();

//         try {
//             for (File image : imageFiles) {
//                 Mat img = Imgcodecs.imread(image.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
//                 if (img.empty()) {
//                     LOGGER.warning("Could not read image: " + image.getName());
//                     continue;
//                 }

//                 // Extract label from filename (assumed format: label-name.jpg)
//                 int label = Integer.parseInt(image.getName().split("-")[0]);
//                 images.add(img);
//                 labels.add(label);
//             }

//             if (images.isEmpty()) {
//                 LOGGER.severe("No valid training images found");
//                 return false;
//             }

//             MatOfInt labelsMat = new MatOfInt();
//             labelsMat.fromList(labels);

//             this.faceRecognizer.train(images, labelsMat);
//             this.isInitialized = true;
//             LOGGER.info("Face recognition model trained successfully");
//             return true;

//         } catch (Exception e) {
//             LOGGER.log(Level.SEVERE, "Failed to initialize face recognition", e);
//             return false;
//         }
//     }

//     /**
//      * Recognize a face in the given image
//      *
//      * @param faceImage
//      *                  The image containing a face to recognize
//      * @return the recognized label, or -1 if not recognized or error
//      */
//     public int recognize(Mat faceImage) {
//         if (!isInitialized) {
//             LOGGER.severe("Face recognizer not initialized. Call init() first.");
//             return -1;
//         }

//         try {
//             Mat grayImage = new Mat();
//             if (faceImage.channels() > 1) {
//                 Imgproc.cvtColor(faceImage, grayImage, Imgproc.COLOR_BGR2GRAY);
//             } else {
//                 grayImage = faceImage;
//             }

//             int[] label = new int[1];
//             double[] confidence = new double[1];
//             this.faceRecognizer.predict(grayImage, label, confidence);

//             // Confidence threshold for recognition
//             if (confidence[0] > 60.0) {
//                 return -1; // Unknown face
//             }

//             return label[0];

//         } catch (Exception e) {
//             LOGGER.log(Level.SEVERE, "Error during face recognition", e);
//             return -1;
//         }
//     }
// }
