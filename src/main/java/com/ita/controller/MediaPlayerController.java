package com.ita.controller;

import com.ita.ui.AboutDialog;
import com.ita.ui.SliderBar;
import com.ita.util.DateTimeUtil;
import com.ita.util.FileUtils;
import com.ita.util.PropertiesUtils;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by itachi on 13/2/15.
 */
public class MediaPlayerController implements Initializable {

    @FXML
    private Button play;

    @FXML
    private ToggleButton volume;

    @FXML
    private ToggleGroup group;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button stop;

    @FXML
    private Button playlist;

    @FXML
    private Label playTime;

    @FXML
    private SliderBar timeSlider;

    @FXML
    private SliderBar volumeSlider;

    @FXML
    private MediaView mediaView;

    private ListView<Path> playListFiles = new ListView<>();
    private Stage stage;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;

    @FXML
    void playAction(ActionEvent event) {

    }

    @FXML
    void stopAction(ActionEvent event) {

    }

    @FXML
    void openPlaylist(ActionEvent event) {

    }

    @FXML
    void muteUnmute(ActionEvent event) {

    }

    @FXML
    void openFile(ActionEvent event) {
        ObservableList<Path> tempList = FXCollections
                .observableArrayList();
        try {
			FileChooser chooser = new FileChooser();
			chooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("Files", PropertiesUtils
							.readFormats()));

			List<Path> listOfFiles = new ArrayList<Path>();
			listOfFiles = FileUtils.convertListFiletoListPath(chooser.showOpenMultipleDialog(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow()));
			if (listOfFiles != null) {
				tempList.clear();
                listOfFiles.stream().forEach(tempList::add);
                playListFiles.setItems(tempList);
				playVideo(listOfFiles.get(0).toAbsolutePath().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void exitPlayer(ActionEvent event) {
        stage.close();
    }

    @FXML
    void about(ActionEvent event) {
        AboutDialog aboutDialog = new AboutDialog(stage);
        aboutDialog.showAbout();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void playVideo(String MEDIA_URL) {
        try {
            MEDIA_URL = URLEncoder.encode(MEDIA_URL, "UTF-8");
            MEDIA_URL = "file:/"
                    + (MEDIA_URL).replace("\\", "/").replace("+", "%20");
            Media media = new Media(MEDIA_URL);
            // create media player
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            bindMediaPlayerControls(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.play();
            mediaView.setPreserveRatio(false);
            mediaView.autosize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindMediaPlayerControls(final MediaPlayer mediaPlayer) {

        mediaPlayer.currentTimeProperty().addListener((ov) -> {
            updateValues(mediaPlayer);
        });

        mediaPlayer.setOnPlaying(() -> {
            if (stopRequested) {
                mediaPlayer.pause();
                stopRequested = false;
            } else {
                play.setId("pause");
            }
        });

        mediaPlayer.setOnPaused(() -> {
            play.setId("play");
        });

        mediaPlayer.setOnReady(() ->  {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues(mediaPlayer);
        });

        play.setOnAction((e) -> {
            MediaPlayer.Status status = mediaPlayer.getStatus();

            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                // don't do anything in these states
                return;
            }

            if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY
                    || status == MediaPlayer.Status.STOPPED) {
                // rewind the movie if we're sitting at the end
                if (atEndOfMedia) {
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    atEndOfMedia = false;
                }
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();
            }
        });

        stop.setOnAction((e) -> {
            mediaPlayer.stop();
            play.setId("play");
        });

        timeSlider.sliderValueProperty().addListener((ov) -> {
            if (timeSlider.isTheValueChanging()) {
                if (null != mediaPlayer)
                    // multiply duration by percentage calculated by
                    // slider position
                    mediaPlayer.seek(duration.multiply(timeSlider
                            .sliderValueProperty().getValue() / 100.0));
                else
                    timeSlider.sliderValueProperty().setValue(0);
            }
        });

        volumeSlider.sliderValueProperty().addListener((ov) -> {
            if (volumeSlider.isTheValueChanging()) {
                if (null != mediaPlayer) {
                    // multiply duration by percentage calculated by
                    // slider position
                    if (volumeSlider.sliderValueProperty().getValue() > 0) {
                        volume.setSelected(false);
                    } else if (volumeSlider.sliderValueProperty().getValue() == 0) {
                        volume.setSelected(true);
                    }
                    mediaPlayer.setVolume(volumeSlider.sliderValueProperty()
                            .getValue() / 100.0);
                } else
                    volumeSlider.sliderValueProperty().setValue(0);

            }
        });
    }

    private void updateValues(MediaPlayer mediaPlayer) {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                System.out.println(currentTime.toSeconds());
                playTime.setText(" " + DateTimeUtil.formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isTheValueChanging()) {
                    timeSlider
                            .sliderValueProperty()
                            .setValue(
                                    currentTime.divide(duration).toMillis() * 100.0);
                }
                if (!volumeSlider.isTheValueChanging()) {
                    volumeSlider.sliderValueProperty()
                            .setValue(
                                    (int) Math.round(mediaPlayer
                                            .getVolume() * 100));
                }
            });
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public DoubleProperty timerSliderWidthProperty() {
        return timeSlider.prefWidthProperty();
    }

    public DoubleProperty mediaViewHeightProperty() {
        return mediaView.fitHeightProperty();
    }

    public DoubleProperty mediaViewWidthProperty() {
        return mediaView.fitWidthProperty();
    }
}