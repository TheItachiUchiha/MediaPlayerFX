package com.ita.controller;

import com.ita.ui.AboutDialog;
import com.ita.ui.SliderBar;
import com.ita.ui.WarningDialog;
import com.ita.util.DateTimeUtil;
import com.ita.util.FileUtils;
import com.ita.util.PropertiesUtils;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

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
    private HBox mediaControl;

    @FXML
    private MediaView mediaView;

    @FXML
    private BorderPane root;

    private ObservableList playListFiles =FXCollections.observableArrayList();
    private ObjectProperty<Path> selectedMedia = new SimpleObjectProperty<>();
    private ObjectProperty<Path> deletedMedia = new SimpleObjectProperty<>();
    private Stage stage;
    private int previousValue;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private PlaylistController playlistController;
    private Scene playlistScene;
    private FadeTransition ft;

    @FXML
    void playAction(ActionEvent event) {
        MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
        if(null != mediaPlayer) {
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
        } else {
            event.consume();
        }
    }

    @FXML
    void stopAction(ActionEvent event) {
        MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
        if(null != mediaPlayer) {
            mediaPlayer.stop();
            play.setId("play");
        } else {
            event.consume();
        }
    }

    @FXML
    void openPlaylist(ActionEvent event) {
        Stage stage = new Stage();
        stage.setScene(playlistScene);
        stage.initOwner(((Button) event.getSource()).getScene().getWindow());
        stage.show();
        Bindings.bindContentBidirectional(playListFiles, playlistController.listViewItems());
        selectedMedia.bind(playlistController.selectedFile());
        deletedMedia.bind(playlistController.deletedFile());
    }

    @FXML
    void muteUnmute(ActionEvent event) {
        if(volumeSlider.sliderValueProperty().intValue() == 0){
            volumeSlider.sliderValueProperty().setValue(previousValue);
        } else {
            previousValue = volumeSlider.sliderValueProperty().intValue();
            volumeSlider.sliderValueProperty().setValue(0);
        }
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

			Path newFile;
            newFile = chooser.showOpenDialog(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow()).toPath();
			if (newFile != null) {
                if(!playListFiles.contains(newFile)) {
                    playListFiles.add(newFile);
                    playVideo(newFile.toString());
                } else {
                    playVideo(newFile.toString());
                }
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

        ft = new FadeTransition(Duration.millis(2000), mediaControl);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1);

        selectedMedia.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                playVideo(newValue.toString());
            }
        });

        deletedMedia.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                stopAction(null);
            }
        });
    }

    private void playVideo(String MEDIA_URL) {
        try {
            String MEDIA_URL_FOR_MEDIA = URLEncoder.encode(MEDIA_URL, "UTF-8");
            MEDIA_URL_FOR_MEDIA = "file:/"
                    + (MEDIA_URL_FOR_MEDIA).replace("\\", "/").replace("+", "%20");
            Media media = new Media(MEDIA_URL_FOR_MEDIA);
            // create media player
            checkAndStopMediaPlayer();
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            bindMediaPlayerControls(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            ((Stage)mediaView.getScene().getWindow()).setTitle(Paths.get(MEDIA_URL).getFileName().toString());
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
            } else {
                volumeSlider.sliderValueProperty().setValue(0);
            }
        });

        onFullScreenHideControl((Stage) mediaView.getScene().getWindow());
    }

    private void updateValues(MediaPlayer mediaPlayer) {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                playTime.setText(" " + DateTimeUtil.formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isTheValueChanging()) {
                    timeSlider
                            .sliderValueProperty()
                            .setValue(
                                    currentTime.divide(duration.toMillis()).toMillis() * 100.0);
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

    private void checkAndStopMediaPlayer(){
        if(null!=mediaView.getMediaPlayer()){
            stopAction(null);
            mediaView.setMediaPlayer(null);
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

    public void injectPlayListController(PlaylistController playlistController) {
        this.playlistController = playlistController;
    }

    public void injectPlayListRoot(Parent playlistRoot) {
        playlistScene = new Scene(playlistRoot);
    }

    public void applyDragAndDropFeatures(Scene scene) {
        try{
            applyControlHiding(mediaControl);

            scene.setOnDragOver((dragEvent) -> {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasFiles()) {
                    dragEvent.acceptTransferModes(TransferMode.COPY);
                } else {
                    dragEvent.consume();
                }
            });

            scene.setOnDragDropped((dragEvent) -> {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasFiles()) {
                    for (Path filePath : FileUtils.convertListFiletoListPath(db.getFiles())) {
                        try {
                            if(PropertiesUtils.readFormats().contains("*" + filePath.toAbsolutePath().toString().substring(filePath.toAbsolutePath().toString().length() - 4))) {
                                if (null != mediaView.getMediaPlayer())
                                    mediaView.getMediaPlayer().stop();
                                playListFiles.add(filePath);
                                playVideo(filePath.toAbsolutePath().toString());
                            }
                            else {
                                WarningDialog warningDialog = new WarningDialog((Stage)scene.getWindow());
                                warningDialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            scene.addEventFilter(KeyEvent.KEY_PRESSED, (keyEvent) -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    ((Stage)scene.getWindow()).setFullScreen(false);
                }
            });

            mediaView.addEventFilter(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
                if (mouseEvent.getButton().equals(
                        MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        if (((Stage)scene.getWindow()).isFullScreen()) {
                            ((Stage)scene.getWindow()).setFullScreen(false);
                        } else {
                            ((Stage)scene.getWindow()).setFullScreen(true);
                        }
                    }
                }
            });

            scene.addEventFilter(MouseEvent.MOUSE_MOVED, (mouseEvent) -> {
                if(stage.isFullScreen()) {
                    showTempMediaControlBar();
                } else {
                    showConstantMediaControlBar();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyControlHiding(Node node) {
        if(node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().stream().forEach(this::applyControlHiding);
        }
        node.setOnMouseMoved(mouseEvent -> {
            if(mouseEvent.getX() > 0) {
                showConstantMediaControlBar();
            }
        });
    }

    private void onFullScreenHideControl(Stage stage) {
        try {
            stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue) {
                    showTempMediaControlBar();
                } else {
                    showConstantMediaControlBar();
                }
            });
        } catch (Exception iep) {
            iep.printStackTrace();
        }
    }

    private void showTempMediaControlBar(){
        menuBar.setOpacity(0);
        mediaControl.setOpacity(1.0);
        ft.play();
    }

    private void showConstantMediaControlBar(){
        menuBar.setOpacity(1);
        ft.stop();
        mediaControl.setOpacity(1.0);
    }
}