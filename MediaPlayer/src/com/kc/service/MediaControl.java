package com.kc.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
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
    private SlidoBar timeSlider;
    private Label playTime;
    private SlidoBar volumeSlider;
    private Button playButton;
    private Button stopButton;
    private Button playListButton;
    public static ToggleButton volButton;
    private ToggleGroup group;
    private VBox listBox;
    private ListView<String> playList;
    private Button add;
    private Button remove;
    private MediaController mediaController;
    double prevVolStatus=1;
    String currentVedio="";
    private ObservableList<File> fileList = FXCollections
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
		remove = new Button("Remove");
		HBox box = new HBox(5);
		box.setPadding(new Insets(0, 5, 5, 5));
		box.getChildren().addAll(add,remove);
		box.setAlignment(Pos.CENTER);
		listBox.getChildren().addAll(playList,box);
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
        
        add.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent paramT) {
				
				playButton.setDisable(false);
				FileChooser chooser = new FileChooser();
				chooser.getExtensionFilters().addAll(
		                new FileChooser.ExtensionFilter("MP4 files", "*.mp4", "*.uvu", "*.m4v","*.mp3"));


				List<File> listOfFiles = new ArrayList<File>();
				listOfFiles=chooser.showOpenMultipleDialog(MediaController.primaryStage);
				if(listOfFiles!=null)
				{
	                for (File file : listOfFiles) {
	                	
	                    MediaController.tempList.add(file.getAbsolutePath());
	                }
	                playList.setItems( MediaController.tempList);
				}
			}
		});
        
        remove.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent paramT) {
				if(playList.getSelectionModel().getSelectedItem()!=null)
				{
					if(playList.getSelectionModel().getSelectedItem().equals(currentVedio))
					{
						mediaPlayer.stop();
						playButton.setId("play");
						playButton.setDisable(true);
					}
					 MediaController.tempList.remove(playList.getSelectionModel().getSelectedItem());
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
        timeLabel.setMinWidth(40);
        getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new SlidoBar();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.getSlider().valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.getSlider().isValueChanging()) {
                	if(null!=mediaPlayer)
                		// multiply duration by percentage calculated by slider position
                		mediaPlayer.seek(duration.multiply(timeSlider.getSlider().getValue() / 100.0));
                	else
                		timeSlider.getSlider().setValue(0);
                }
            }
        });
       
        /*timeSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent paramT) {
				
				mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
				System.out.println("hello");
				
			}
		});*/
        getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label(" 00:00:00/00:00:00");
        playTime.setPrefWidth(130);
        playTime.setMinWidth(100);
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
					mediaPlayer.setVolume(0.5);
		        }
				else if (newValue !=null)
				{
					mediaPlayer.setVolume(0);
				}
				
			}
		});

        // Add Volume slider
        volumeSlider = new SlidoBar();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(70);
        HBox.setHgrow(volumeSlider, Priority.ALWAYS);
        volumeSlider.getSlider().valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.getSlider().isValueChanging()) {
                	if(null!=mediaPlayer)
                	{
                		// multiply duration by percentage calculated by slider position
                		if(volumeSlider.getSlider().getValue()>0)
                		{
                			volButton.setSelected(false);
                		}
                		else if(volumeSlider.getSlider().getValue()==0)
                		{
                			volButton.setSelected(true);
                		}
                		mediaPlayer.setVolume(volumeSlider.getSlider().getValue() / 100.0);
                		prevVolStatus=volumeSlider.getSlider().getValue() / 100.0;
                	}
                	else
                		volumeSlider.getSlider().setValue(0);
                    
                }
            }
        });
       
        getChildren().add(volumeSlider);
       
        
        playList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent click) {
				
				if(click.getClickCount()==2)
				{
					if(playList.getSelectionModel().getSelectedItem()!=null)
					{
						playButton.setDisable(false);
						if(mediaPlayer!=null)
						{
							mediaPlayer.stop();
						}
						mediaController.playVideo("file:/"+ (playList.getSelectionModel().getSelectedItem())
								.replace("\\", "/").replace(" ", "%20"));
						volButton.setSelected(false);
						currentVedio=playList.getSelectionModel().getSelectedItem();
					}
				}
				
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
						 MediaController.tempList.add(file.getAbsolutePath());
						filePath = file.getAbsolutePath();
					}
					if(null!=mediaPlayer)
					{
						mediaPlayer.stop();
					}
					playList.setItems( MediaController.tempList);
					mediaController.playVideo("file:/"
							+ (filePath).replace("\\", "/").replace(" ",
									"%20"));
					volButton.setSelected(false);
					playButton.setDisable(false);
					currentVedio=filePath;
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
                    playTime.setText(" " + formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.getSlider().isValueChanging()) {
                        timeSlider.getSlider().setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.getSlider().isValueChanging()) {
                        volumeSlider.getSlider().setValue((int) Math.round(mediaPlayer.getVolume()
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
                    playButton.setId("pause");
                    //playButton.setText("||");
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setId("play");
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
                    playButton.setId("play");
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
	             playButton.setId("play");
				
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
    
    public SlidoBar getTimeSlider()
    {
    	return this.timeSlider;
    }

}