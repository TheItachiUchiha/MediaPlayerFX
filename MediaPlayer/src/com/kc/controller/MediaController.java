package com.kc.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.kc.service.MediaControl;
import com.kc.service.MediaControlHide;

public class MediaController extends Application implements Initializable {

	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	public static Stage primaryStage;
	private static Scene scene;

	// private static final String MEDIA_URL = "file:/F:/newmoon.mp4";
	public static ObservableList<String> tempList = FXCollections
			.observableArrayList();
	private ObservableList<File> fileList = FXCollections
			.observableArrayList();
	public static BorderPane root;
	public static VBox box = new VBox();
	public MenuBar menuBar;
	private static MediaControl mediaControl;
	private static Stage mediaControlStage;
	private static StackPane stackPane;
	private Executor executor;

	@Override
	public void initialize(URL paramURL, ResourceBundle paramResourceBundle) {

	}

	@Override
	public void start(final Stage primaryStage) throws Exception {

		try {
			MediaController.primaryStage = primaryStage;
			primaryStage.setTitle("Media Player");
			FXMLLoader loader = new FXMLLoader(
					MediaController.class
							.getResource("/com/kc/view/mediaControl.fxml"));
			root = (BorderPane) loader.load();
			menuBar = (MenuBar) root.getTop();
			// creating Space for media
			HBox rectangle = new HBox();
			rectangle.setPrefSize(600, 300);
			root.setCenter(rectangle);

			// Controls at Bottom
			mediaControl = new MediaControl(
					(MediaController) loader.getController());
			root.setBottom(mediaControl);

			final Scene scene = new Scene(root);
			MediaController.scene = scene;
			MediaController.scene.getStylesheets().add(MediaController.class.getResource("/com/kc/style/MediaPlayer.css").toExternalForm());
			MediaController.primaryStage.setScene(MediaController.scene);
			MediaController.primaryStage.show();
			
			executor = Executors.newCachedThreadPool();
			
			scene.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					if(null!=mediaView)
						mediaView.setFitWidth(newValue.doubleValue());
				}
			});

			scene.heightProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					// Subtracting height of MediaControl & Menu on top
					if(null!=mediaView)
					{
						if (!primaryStage.isFullScreen())
								mediaView.setFitHeight(newValue.doubleValue() - 25 - 40);
						else
								mediaView.setFitHeight(newValue.doubleValue());
					}
				}
			});

			scene.addEventFilter(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent mouseEvent) {

							if (mouseEvent.getButton().equals(
									MouseButton.PRIMARY)) {
								if (mouseEvent.getClickCount() == 2) {
									if (primaryStage.isFullScreen()) {
										root.setTop(menuBar);
										root.setBottom(mediaControl);
										stackPane.getChildren().clear();
										scene.setRoot(root);
										primaryStage.setFullScreen(false);
										//mediaControlStage = new Stage();
									} else {
										root.setTop(new VBox());
										root.setBottom(new HBox());
										stackPane = new StackPane();
										stackPane.getChildren().addAll(root, mediaControl);
										scene.setRoot(stackPane);
										primaryStage.setFullScreen(true);
										StackPane.setMargin(mediaControl, new Insets(scene.getHeight()- 35, 0, 10, 0));
										MediaControlHide command = new MediaControlHide(primaryStage, mediaControl);
										executor.execute(command);
									}
								}
							}
						}
					});

			scene.setOnDragOver(new EventHandler<DragEvent>() {
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
			scene.setOnDragDropped(new EventHandler<DragEvent>() {

				@Override
				public void handle(DragEvent event) {

					Dragboard db = event.getDragboard();
					if (db.hasFiles()) {
						String filePath = null;
						tempList.clear();
						for (File file : db.getFiles()) {
							tempList.add(file.getAbsolutePath());
							filePath = file.getAbsolutePath();
						}
						if(null!=mediaPlayer)
							mediaPlayer.stop();
						mediaControl.resetPlayList(tempList);
						playVideo("file:/"
								+ (filePath).replace("\\", "/").replace(" ",
										"%20"));
						System.out.println(filePath);
					}

				}
			});
			
			scene.addEventFilter(MouseEvent.ANY,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent arg0) {
							if(primaryStage.isFullScreen())
							{
								mediaControl.setOpacity(1.0);
							}
						}
			});
			
			
			scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			        @Override
			        public void handle(KeyEvent t) {
			          if(t.getCode()==KeyCode.ESCAPE)
			          {
			        	  if (((HBox)root.getBottom()).getChildren().size()==0) {
								root.setTop(menuBar);
								root.setBottom(mediaControl);
								stackPane.getChildren().clear();
								scene.setRoot(root);
								primaryStage.setFullScreen(false);
								mediaView.setFitHeight(scene.getHeight() - 25 - 40);
							}
			          }
			        }
			    });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void playVideo(String MEDIA_URL) {
		try {
			Media media = new Media(MEDIA_URL);
			// create media player
			mediaPlayer = new MediaPlayer(media);
			mediaControl.setMediaPlayer(mediaPlayer);
			mediaView = new MediaView(mediaPlayer);
			mediaControl.setMediaView(mediaView);
			mediaPlayer.setAutoPlay(true);
			mediaPlayer.play();
			mediaView.setPreserveRatio(false);
			mediaView.autosize();

			root.setCenter(mediaView);

			mediaView.setFitHeight(scene.getHeight() - 25 - 40);
			mediaView.setFitWidth(scene.getWidth());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openFile() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP4 files", "*.mp4", "*.uvu", "*.m4v"));
		
		fileList.clear();
		fileList.addAll(chooser.showOpenMultipleDialog(MediaController.primaryStage));
		if(fileList!=null)
		{
			tempList.clear();
            for (File file : fileList) {
            	
                tempList.add(file.getAbsolutePath());
            }
		}
			mediaControl.resetPlayList(tempList);
	}

	public void exitPlayer() {
		primaryStage.close();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
