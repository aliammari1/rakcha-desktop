package com.esprit.controllers.users;
//
////
////
public class FaceRecognitionController {
    // @FXML
    // ClientSideBarController clientSideBarController;
    // Client client;
    //// // FXML buttons
    //// @FXML
    //// private Button cameraButton;
    //// // the FXML area for showing the current frame
    //// @FXML
    //// private ImageView originalFrame;
    //// // checkboxes for enabling/disabling a classifier
    //// @FXML
    //// private CheckBox haarClassifier;
    //// @FXML
    //// private CheckBox lbpClassifier;
    ////
    //// // a timer for acquiring the video stream
    //// private ScheduledExecutorService timer;
    //// // the OpenCV object that performs the video capture
    //// private VideoCapture capture;
    //// // a flag to change the button behavior
    //// private boolean cameraActive;
    ////
    //// // face cascade classifier
    //// private CascadeClassifier faceCascade;
    //// private int absoluteFaceSize;
    ////
    ////
    //// protected void init() {
    //// this.capture = new VideoCapture();
    ////
    ////
    //// this.faceCascade = new CascadeClassifier();
    //// this.absoluteFaceSize = 0;
    //// }
    ////
    ////
    //// @FXML
    //// protected void startCamera() {
    //// // set a fixed width for the frame
    //// originalFrame.setFitWidth(600);
    //// // preserve image ratio
    //// originalFrame.setPreserveRatio(true);
    ////
    //// if (!this.cameraActive) {
    //// // disable setting checkboxes
    //// this.haarClassifier.setDisable(true);
    //// this.lbpClassifier.setDisable(true);
    ////
    //// // start the video capture
    //// this.capture.open(0);
    ////
    //// // is the video stream available?
    //// if (this.capture.isOpened()) {
    //// this.cameraActive = true;
    ////
    //// // grab a frame every 33 ms (30 frames/sec)
    //// Runnable frameGrabber = new Runnable() {
    ////
    //// @Override
    //// public void run() {
    //// Image imageToShow = grabFrame();
    //// originalFrame.setImage(imageToShow);
    //// }
    //// };
    ////
    //// this.timer = Executors.newSingleThreadScheduledExecutor();
    //// this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
    ////
    //// // update the button content
    //// this.cameraButton.setText("Stop Camera");
    //// } else {
    //// // log the error
    //// System.err.println("Failed to open the camera connection...");
    //// }
    //// } else {
    //// // the camera is not active at this point
    //// this.cameraActive = false;
    //// // update again the button content
    //// this.cameraButton.setText("Start Camera");
    //// // enable classifiers checkboxes
    //// this.haarClassifier.setDisable(false);
    //// this.lbpClassifier.setDisable(false);
    ////
    //// // stop the timer
    //// try {
    //// this.timer.shutdown();
    //// this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
    //// } catch (InterruptedException e) {
    //// // log the exception
    //// System.err.println("Exception in stopping the frame capture, trying to
    // release the camera now... " + e);
    //// }
    ////
    //// // release the camera
    //// this.capture.release();
    //// // clean the frame
    //// this.originalFrame.setImage(null);
    //// }
    //// }
    ////
    //// /**
    //// * Get a frame from the opened video stream (if any)
    //// *
    //// * @return the {@link Image} to show
    //// */
    //// private Image grabFrame() {
    //// // init everything
    //// Image imageToShow = null;
    //// Mat frame = new Mat();
    ////
    //// // check if the capture is open
    //// if (this.capture.isOpened()) {
    //// try {
    //// // read the current frame
    //// this.capture.read(frame);
    ////
    //// // if the frame is not empty, process it
    //// if (!frame.empty()) {
    //// // face detection
    //// this.detectAndDisplay(frame);
    ////
    //// // convert the Mat object (OpenCV) to Image (JavaFX)
    //// imageToShow = mat2Image(frame);
    //// }
    ////
    //// } catch (Exception e) {
    //// // log the (full) error
    //// System.err.println("ERROR: " + e);
    //// }
    //// }
    ////
    //// return imageToShow;
    //// }
    ////
    //// /**
    //// * Method for face detection and tracking
    //// *
    //// * @param frame it looks for faces in this frame
    //// */
    //// private void detectAndDisplay(Mat frame) {
    //// MatOfRect faces = new MatOfRect();
    //// Mat grayFrame = new Mat();
    ////
    //// // convert the frame in gray scale
    //// Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
    //// // equalize the frame histogram to improve the result
    //// Imgproc.equalizeHist(grayFrame, grayFrame);
    ////
    //// // compute minimum face size (20% of the frame height, in our case)
    //// if (this.absoluteFaceSize == 0) {
    //// int height = grayFrame.rows();
    //// if (Math.round(height * 0.2f) > 0) {
    //// this.absoluteFaceSize = Math.round(height * 0.2f);
    //// }
    //// }
    ////
    //// // detect faces
    //// this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 |
    // Objdetect.CASCADE_SCALE_IMAGE,
    //// new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
    ////
    //// // each rectangle in faces is a face: draw them!
    //// Rect[] facesArray = faces.toArray();
    //// for (int i = 0; i < facesArray.length; i++) {
    //// Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new
    // Scalar(7, 255, 90), 4);
    //// System.out.println(facesArray[i].tl());
    //// System.out.println(facesArray[i].br());
    //// }
    ////
    ////
    //// }
    ////
    ////
    //// @FXML
    //// protected void haarSelected(Event event) {
    //// // check whether the lpb checkbox is selected and deselect it
    //// if (this.lbpClassifier.isSelected())
    //// this.lbpClassifier.setSelected(false);
    ////
    //// this.checkboxSelection("resources/haarcascades/haarcascade_frontalcatface.xml");
    //// }
    ////
    ////
    //// @FXML
    //// protected void lbpSelected(Event event) {
    //// // check whether the haar checkbox is selected and deselect it
    //// if (this.haarClassifier.isSelected())
    //// this.haarClassifier.setSelected(false);
    ////
    //// this.checkboxSelection("resources/lbpcascades/lbpcascade_frontalface.xml");
    //// }
    ////
    //// /**
    //// * Method for loading a classifier trained set from disk
    //// *
    //// * @param classifierPath the path on disk where a classifier trained set is
    // located
    //// */
    ////
    //// private void checkboxSelection(String classifierPath) {
    //// // load the classifier(s)
    //// this.faceCascade.load(classifierPath);
    ////
    //// // now the video capture can start
    //// this.cameraButton.setDisable(false);
    //// }
    ////
    //// /**
    //// * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
    //// *
    //// * @param frame the {@link Mat} representing the current frame
    //// * @return the {@link Image} to show
    //// */
    //// private Image mat2Image(Mat frame) {
    //// // create a temporary buffer
    //// MatOfByte buffer = new MatOfByte();
    //// // encode the frame in the buffer, according to the PNG format
    //// Imgcodecs.imencode(".png", frame, buffer);
    //// // build and return an Image created from the image encoded in the
    //// // buffer
    //// return new Image(new ByteArrayInputStream(buffer.toArray()));
    //// }
    ////
    //// }
    //
    //
    // import com.esprit.utils.FaceDetector;
    // import javafx.collections.FXCollections;
    // import javafx.collections.ObservableList;
    // import javafx.fxml.FXML;
    // import javafx.geometry.Insets;
    // import javafx.scene.control.Button;
    // import javafx.scene.control.Label;
    // import javafx.scene.control.TextField;
    // import javafx.scene.control.*;
    // import javafx.scene.image.Image;
    // import javafx.scene.image.ImageView;
    // import javafx.scene.layout.AnchorPane;
    // import javafx.scene.layout.TilePane;
    // import javafx.scene.text.Text;
    // import javafx.scene.text.TextFlow;
    //
    // import java.awt.*;
    // import java.io.File;
    // import java.io.FileInputStream;
    // import java.io.FileNotFoundException;
    // import java.sql.SQLException;
    // import java.time.Instant;
    // import java.util.ArrayList;
    //
    // public class SampleController {
    // @FXML
    // ClientSideBarController clientSideBarController;
    // Client client;
    //
    // public void setData(Client client) {
    // this.client = client;
    // }
    //
    // public void setData(Client client) {
    // this.client = client;
    // }
    //
    // //**********************************************************************************************
    // //Mention The file location path where the face will be saved & retrieved
    //
    // public static ObservableList<String> event =
    // FXCollections.observableArrayList();
    // public static ObservableList<String> outEvent =
    // FXCollections.observableArrayList();
    // public String filePath = "./faces";
    // @FXML
    // public ListView<String> logList;
    // @FXML
    // public ListView<String> output;
    // @FXML
    // public ProgressIndicator pb;
    // @FXML
    // public Label savedLabel;
    // @FXML
    // public Label warning;
    // @FXML
    // public Label title;
    // @FXML
    // public TilePane tile;
    // @FXML
    // public TextFlow ocr;
    // public boolean enabled = false;
    // public boolean isDBready = false;
    // //**********************************************************************************************
    // FaceDetector faceDetect = new FaceDetector(); //Creating Face detector object
    // //ColoredObjectTracker cot = new ColoredObjectTracker(); //Creating Color
    // Object Tracker object
    // //Database database = new Database(); //Creating Database object
    // //OCR ocrObj = new OCR();
    // ArrayList<String> user = new ArrayList<String>();
    // ImageView imageView1;
    // int count = 0;
    // //**********************************************************************************************
    // @FXML
    // private Button startCam;
    // @FXML
    // private Button stopBtn;
    // @FXML
    // private Button motionBtn;
    // @FXML
    // private Button eyeBtn;
    // @FXML
    // private Button shapeBtn;
    // @FXML
    // private Button upperBodyBtn;
    // @FXML
    // private Button fullBodyBtn;
    // @FXML
    // private Button smileBtn;
    // @FXML
    // private Button gesture;
    // @FXML
    // private Button gestureStop;
    // @FXML
    // private Button saveBtn;
    // @FXML
    // private Button ocrBtn;
    // @FXML
    // private Button capBtn;
    // @FXML
    // private Button recogniseBtn;
    // @FXML
    // private Button stopRecBtn;
    // @FXML
    // private ImageView frame;
    // @FXML
    // private ImageView motionView;
    // @FXML
    // private AnchorPane pdPane;
    // @FXML
    // private TitledPane dataPane;
    // @FXML
    // private TextField fname;
    // @FXML
    // private TextField lname;
    // @FXML
    // private TextField code;
    // @FXML
    // private TextField reg;
    // @FXML
    // private TextField sec;
    // @FXML
    // private TextField age;
    //
    // //**********************************************************************************************
    // public void putOnLog(String data) {
    //
    // Instant now = Instant.now();
    //
    // String logs = now.toString() + ":\n" + data;
    //
    // event.add(logs);
    //
    // logList.setItems(event);
    //
    // }
    //
    // @FXML
    // protected void startCamera() throws SQLException {
    //
    // //*******************************************************************************************
    // //initializing objects from start camera button event
    // faceDetect.init();
    //
    // faceDetect.setFrame(frame);
    //
    // faceDetect.start();
    //
    // if (!database.init()) {
    //
    // putOnLog("Error: Database Connection Failed ! ");
    //
    // } else {
    // isDBready = true;
    // putOnLog("Success: Database Connection Succesful ! ");
    // }
    //
    // //*******************************************************************************************
    // //Activating other buttons
    // startCam.setVisible(false);
    // eyeBtn.setDisable(false);
    // stopBtn.setVisible(true);
    // //ocrBtn.setDisable(false);
    // capBtn.setDisable(false);
    // motionBtn.setDisable(false);
    // gesture.setDisable(false);
    // saveBtn.setDisable(false);
    //
    // if (isDBready) {
    // recogniseBtn.setDisable(false);
    // }
    //
    // dataPane.setDisable(false);
    // // shapeBtn.setDisable(false);
    // smileBtn.setDisable(false);
    // fullBodyBtn.setDisable(false);
    // upperBodyBtn.setDisable(false);
    //
    // if (stopRecBtn.isDisable()) {
    // stopRecBtn.setDisable(false);
    // }
    // //*******************************************************************************************
    //
    //
    // tile.setPadding(new Insets(15, 15, 55, 15));
    // tile.setHgap(30);
    //
    // //**********************************************************************************************
    // //Picture Gallary
    //
    // String path = filePath;
    //
    // File folder = new File(path);
    // File[] listOfFiles = folder.listFiles();
    //
    // //Image reader from the mentioned folder
    // for (final File file : listOfFiles) {
    //
    // imageView1 = createImageView(file);
    // tile.getChildren().addAll(imageView1);
    // }
    // putOnLog(" Real Time WebCam Stream Started !");
    //
    // //**********************************************************************************************
    // }
    //
    // @FXML
    // protected void faceRecognise() {
    //
    //
    // faceDetect.setIsRecFace(true);
    // // printOutput(faceDetect.getOutput());
    //
    // recogniseBtn.setText("Get Face Data");
    //
    // //Getting detected faces
    // user = faceDetect.getOutput();
    //
    // if (count > 0) {
    //
    // //Retrieved data will be shown in Fetched Data pane
    // String t = "********* Face Data: " + user.get(1) + " " + user.get(2) + "
    // *********";
    //
    // outEvent.add(t);
    //
    // String n1 = "First Name\t\t:\t" + user.get(1);
    //
    // outEvent.add(n1);
    //
    // output.setItems(outEvent);
    //
    // String n2 = "Last Name\t\t:\t" + user.get(2);
    //
    // outEvent.add(n2);
    //
    // output.setItems(outEvent);
    //
    // String fc = "Face Code\t\t:\t" + user.get(0);
    //
    // outEvent.add(fc);
    //
    // output.setItems(outEvent);
    //
    // String r = "Reg no\t\t\t:\t" + user.get(3);
    //
    // outEvent.add(r);
    //
    // output.setItems(outEvent);
    //
    // String a = "Age \t\t\t\t:\t" + user.get(4);
    //
    // outEvent.add(a);
    //
    // output.setItems(outEvent);
    // String s = "Section\t\t\t:\t" + user.get(5);
    //
    // outEvent.add(s);
    //
    // output.setItems(outEvent);
    //
    // }
    //
    // count++;
    //
    // putOnLog("Face Recognition Activated !");
    //
    // stopRecBtn.setDisable(false);
    //
    // }
    //
    // @FXML
    // protected void stopRecognise() {
    //
    // faceDetect.setIsRecFace(false);
    // faceDetect.clearOutput();
    //
    // this.user.clear();
    //
    // recogniseBtn.setText("Recognise Face");
    //
    // stopRecBtn.setDisable(true);
    //
    // putOnLog("Face Recognition Deactivated !");
    //
    // }
    //
    // @FXML
    // protected void startMotion() {
    //
    // faceDetect.setMotion(true);
    // putOnLog("motion Detector Activated !");
    //
    // }
    //
    // @FXML
    // protected void saveFace() throws SQLException {
    //
    // //Input Validation
    // if (fname.getText().trim().isEmpty() || reg.getText().trim().isEmpty() ||
    // code.getText().trim().isEmpty()) {
    //
    // new Thread(() -> {
    //
    // try {
    // warning.setVisible(true);
    //
    // Thread.sleep(2000);
    //
    // warning.setVisible(false);
    //
    // } catch (InterruptedException ex) {
    // }
    //
    // }).start();
    //
    // } else {
    // //Progressbar
    // pb.setVisible(true);
    //
    // savedLabel.setVisible(true);
    //
    // new Thread(() -> {
    //
    // try {
    //
    // faceDetect.setFname(fname.getText());
    //
    // faceDetect.setFname(fname.getText());
    // faceDetect.setLname(lname.getText());
    // faceDetect.setAge(Integer.parseInt(age.getText()));
    // faceDetect.setCode(Integer.parseInt(code.getText()));
    // faceDetect.setSec(sec.getText());
    // faceDetect.setReg(Integer.parseInt(reg.getText()));
    //
    // database.setFname(fname.getText());
    // database.setLname(lname.getText());
    // database.setAge(Integer.parseInt(age.getText()));
    // database.setCode(Integer.parseInt(code.getText()));
    // database.setSec(sec.getText());
    // database.setReg(Integer.parseInt(reg.getText()));
    //
    // database.insert();
    //
    // javafx.application.Platform.runLater(new Runnable() {
    //
    // @Override
    // public void run() {
    // pb.setProgress(100);
    // }
    // });
    //
    //
    // savedLabel.setVisible(true);
    // Thread.sleep(2000);
    //
    // javafx.application.Platform.runLater(new Runnable() {
    //
    // @Override
    // public void run() {
    // pb.setVisible(false);
    // }
    // });
    //
    //
    // javafx.application.Platform.runLater(new Runnable() {
    //
    // @Override
    // public void run() {
    // savedLabel.setVisible(false);
    // }
    // });
    //
    // } catch (InterruptedException ex) {
    // }
    //
    // }).start();
    //
    // faceDetect.setSaveFace(true);
    //
    // }
    //
    // }
    //
    // @FXML
    // protected void stopCam() throws SQLException {
    //
    // faceDetect.stop();
    //
    // startCam.setVisible(true);
    // stopBtn.setVisible(false);
    //
    // /* this.saveFace=true; */
    //
    // putOnLog("Cam Stream Stopped!");
    //
    // recogniseBtn.setDisable(true);
    // saveBtn.setDisable(true);
    // dataPane.setDisable(true);
    // stopRecBtn.setDisable(true);
    // eyeBtn.setDisable(true);
    // smileBtn.setDisable(true);
    // fullBodyBtn.setDisable(true);
    // upperBodyBtn.setDisable(true);
    //
    // database.db_close();
    // putOnLog("Database Connection Closed");
    // isDBready = false;
    // }
    //
    // @FXML
    // protected void ocrStart() {
    //
    // try {
    //
    // Text text1 = new Text(ocrObj.init());
    //
    // text1.setStyle("-fx-font-size: 14; -fx-fill: blue;");
    //
    // ocr.getChildren().add(text1);
    //
    // } catch (FontFormatException e) {
    //
    // e.printStackTrace();
    // }
    //
    // }
    //
    // @FXML
    // protected void capture() {
    //
    // faceDetect.setOcrMode(true);
    //
    // }
    //
    // @FXML
    // protected void startGesture() {
    //
    // faceDetect.stop();
    // cot.init();
    //
    // Thread th = new Thread(cot);
    // th.start();
    //
    // gesture.setVisible(false);
    // gestureStop.setVisible(true);
    //
    // }
    //
    // @FXML
    // protected void startEyeDetect() {
    //
    // faceDetect.setEyeDetection(true);
    // eyeBtn.setDisable(true);
    //
    // }
    //
    // @FXML
    // protected void upperBodyStart() {
    //
    // faceDetect.setUpperBody(true);
    // upperBodyBtn.setDisable(true);
    //
    // }
    //
    // @FXML
    // protected void fullBodyStart() {
    //
    // faceDetect.setFullBody(true);
    // fullBodyBtn.setDisable(true);
    //
    // }
    //
    // @FXML
    // protected void smileStart() {
    //
    // faceDetect.setSmile(true);
    // smileBtn.setDisable(true);
    //
    // }
    //
    // @FXML
    // protected void stopGesture() {
    //
    // cot.stop();
    // faceDetect.start();
    //
    // gesture.setVisible(true);
    // gestureStop.setVisible(false);
    //
    // }
    //
    // @FXML
    // protected void shapeStart() {
    //
    // // faceDetect.stop();
    //
    // SquareDetector shapeFrame = new SquareDetector();
    // shapeFrame.loop();
    //
    // }
    //
    // private ImageView createImageView(final File imageFile) {
    //
    // try {
    // final javafx.scene.image.Image img = new Image(new
    // FileInputStream(imageFile), 120, 0, true, true);
    // imageView1 = new ImageView(img);
    //
    // imageView1.setStyle("-fx-background-color: BLACK");
    // imageView1.setFitHeight(120);
    //
    // imageView1.setPreserveRatio(true);
    // imageView1.setSmooth(true);
    // imageView1.setCache(true);
    //
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // }
    //
    // return imageView1;
    // }
    //
}
