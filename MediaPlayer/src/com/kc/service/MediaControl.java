package com.kc.service;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.kc.controller.MediaController;

public class MediaControl extends HBox {

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private Button playButton;
    private Button stopButton;
    private Button playListButton;
    private ToggleButton volButton;
    private ToggleGroup group;
    private VBox listBox;
    private ListView<String> playList;
    private Button add;
    private MediaController mediaController;
    double prevVolStatus=1;
    private ObservableList<String> tempList = FXCollections
			.observableArrayList();

    

    public MediaControl(final MediaController mediaController) {
    	
    	this.mediaController = mediaController;
        setStyle("-fx-background-color: #3E3E3E;");
        setId("control-bar");
        setPrefHeight(50);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(5, 10, 5, 10));
       
        playButton = new Button();
        playButton.setId("play");
        stopButton = new Button();
        stopButton.setId("stop");
        playListButton = new Button();
        playListButton.setId("playlist");
        listBox = new VBox(5);
		playList = new ListView<String>();
		add = new Button("Add");
		listBox.getChildren().addAll(add, playList);
		final Scene scene = new Scene(listBox);
        
        playListButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				try
				{
					Platform.runLater(new Runnable() {
						public void run() {
							
							Stage stage = new Stage();
							stage.setScene(scene);
							stage.show();
							
						}
					});
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
        

        getChildren().add(playButton);
        getChildren().add(stopButton);
        // Add spacer
        /*Label spacer = new Label("   ");
        getChildren().add(spacer);*/
        
        
        
        /*Label spacer2 = new Label("   ");
        getChildren().add(spacer2);*/

        // Add Time label
        Label timeLabel = new Label("Time: ");
        getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                	if(null!=mediaPlayer)
                		// multiply duration by percentage calculated by slider position
                		mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                	else
                		timeSlider.setValue(0);
                }
            }
        });
        getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label("00:00:00/00:00:00");
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        getChildren().add(playTime);

        //Add the Playlist
        getChildren().add(playListButton);
        
        // Add the volume label
        group = new ToggleGroup();
        volButton = new ToggleButton();
        volButton.setToggleGroup(group);
        volButton.setId("volume");
        getChildren().add(volButton);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable,
					Toggle oldValue, Toggle newValue) {
				
				if (oldValue !=null)
		        {
					mediaPlayer.setVolume(prevVolStatus);
		        }
				else if (newValue !=null)
				{
					mediaPlayer.setVolume(0);
				}
				
			}
		});

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                	if(null!=mediaPlayer)
                	{
                		// multiply duration by percentage calculated by slider position
                		mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                		prevVolStatus=volumeSlider.getValue() / 100.0;
                	}
                	else
                		volumeSlider.setValue(0);
                    
                }
            }
        });
        getChildren().add(volumeSlider);

		playList.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {

			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				mediaPlayer.stop();
				mediaController.playVideo("file:/"
						+ (newValue).replace("\\", "/").replace(
								" ", "%20"));

			}
		});
		playList.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
		});
		playList.setOnDragDropped(new EventHandler<DragEvent>() {
		
			@Override
			public void handle(DragEvent event) {
		
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					String filePath = null;
					for (File file : db.getFiles()) {
						tempList.add(file.getAbsolutePath());
						filePath = file.getAbsolutePath();
					}
					if(null!=mediaPlayer)
					{
						mediaPlayer.stop();
					}
					playList.setItems(tempList);
					mediaController.playVideo("file:/"
							+ (filePath).replace("\\", "/").replace(" ",
									"%20"));
					System.out.println(filePath);
				}
		
			}
		});
		    }
    

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        /*if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }*/
        int elapsedMinutes = intElapsed / 60 - elapsedHours * 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            /*if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }*/
            int durationMinutes = intDuration / 60 - durationHours * 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
    
    public void setMediaPlayer(final MediaPlayer mediaPlayer)
    {
    	this.mediaPlayer = mediaPlayer;
    	mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                	Image imageOk = new Image(getClass().getResourceAsStream("/com/kc/style/pause-icon.png"));
                    playButton.setGraphic(new ImageView(imageOk));
                    //playButton.setText("||");
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                Image imageOk = new Image(getClass().getResourceAsStream("/com/kc/style/play-icon.png"));
                playButton.setGraphic(new ImageView(imageOk));
                //playButton.setText(">");
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            public void run() {
                duration = mediaPlayer.getMedia().getDuration();
                updateValues();
            }
        });

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                	Image imageOk = new Image(getClass().getResourceAsStream("/com/kc/style/play-icon.png"));
                    playButton.setGraphic(new ImageView(imageOk));
                    //playButton.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });
        
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mediaPlayer.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }
            }
        });
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
					
				mediaPlayer.stop();
				 Image imageOk = new Image(getClass().getResourceAsStream("/com/kc/style/play-icon.png"));
	             playButton.setGraphic(new ImageView(imageOk));
				
			}
		});
       
    } 
    public void setMediaView(MediaView mediaView)
    {
    	this.mediaView = mediaView;
    }
    
    public void resetPlayList(ObservableList<String> list)
    {
    	playList.setItems(list);
    }

}