package com.esprit.controllers.series;

import com.esprit.models.series.Episode;
import com.esprit.models.series.Serie;
import com.esprit.services.series.IServiceEpisode;
import com.esprit.services.series.IServiceEpisodeImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


    public class EpisodeClientController implements Initializable {
        @FXML
        private Button uploadButton;
        @FXML
        private Label uploadSuccessLabel;

        @FXML
        private ListView<Episode> ListEpisode;

        @FXML
        private ImageView imgsrie;

        @FXML
        private MediaView midiavideo;

        @FXML
        private Label nomlbl;

        @FXML
        private Label payslbl;

        @FXML
        private Label resumelbl;

        @FXML
        private Label rirecteurslbl;
        @FXML
        private Button arreterbtn;
        @FXML
        private Button jouerbtn;
        @FXML
        private Button pausebtn;
        private Serie selectedSerie;
        private IServiceEpisode iServiceEpisode=new IServiceEpisodeImpl();
        private List<Episode> episodes=new ArrayList<>();
        

        public void initialize(Serie selectedSerie) {
            this.selectedSerie = selectedSerie;
            double imageWidth = 142; // Largeur fixe souhaitée
            double imageHeight = 140; // Hauteur fixe souhaitée
            String img =selectedSerie.getImage();
            File file = new File(img);
            Image image = new Image(file.toURI().toString());
            imgsrie.setImage(image);
            imgsrie.setFitWidth(imageWidth);
            imgsrie.setFitHeight(imageHeight);
            imgsrie.setPreserveRatio(true);
            nomlbl.setText(selectedSerie.getNom());
            resumelbl.setText(selectedSerie.getResume());
            rirecteurslbl.setText(selectedSerie.getDirecteur());
            payslbl.setText(selectedSerie.getPays());
            try {
                episodes=iServiceEpisode.recupuerselonSerie(selectedSerie.getIdserie());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ListEpisode.getItems().addAll(episodes);
            ListEpisode.setCellFactory(param -> new ListCell<Episode>() {
                @Override
                protected void updateItem(Episode item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        double imageWidth = 50; // Largeur fixe souhaitée
                        double imageHeight = 90; // Hauteur fixe souhaitée
                        String img =item.getImage();
                        File file = new File(img);
                        Image image = new Image(file.toURI().toString());
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(imageWidth);
                        imageView.setFitHeight(imageHeight);
                        imageView.setPreserveRatio(true);
                        setText("\n   Title :"+item.getTitre()+"\n  Number: "+item.getNumeroepisode()+ "\n   Season : "+item.getSaison());
                        setGraphic(imageView);
                    }
                }
            });
            ListEpisode.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.intValue() >= 0) {
                    Episode selectedepisode = ListEpisode.getItems().get(newValue.intValue());
                    String vd =selectedepisode.getVideo();
                    File file1 = new File(vd);
                    Media video=new Media(file1.toURI().toString());
                    MediaPlayer m1=new MediaPlayer(video);
                    midiavideo.setMediaPlayer(m1);
                    MediaPlayer mediaPlayer = new MediaPlayer(video);
                    midiavideo.setMediaPlayer(mediaPlayer);
                    jouerbtn.setOnAction(event -> mediaPlayer.play());
                    pausebtn.setOnAction(event -> mediaPlayer.pause());
                    arreterbtn.setOnAction(event -> mediaPlayer.stop());

                }
            });
        }













        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {

        }


    }