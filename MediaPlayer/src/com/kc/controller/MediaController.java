package com.kc.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import com.kc.service.MediaControl;

public class MediaController extends Application implements Initializable{
    
    private MediaPlayer mp;
    private MediaView mediaView;
    private Stage primaryStage;
    private Scene scene;
    public static VBox listBox;
    public static ListView<String> playList;
    public static Button add;
    
    private static final String MEDIA_URL = "file:/E:/ThalaivarSplit.mp4";
    private ObservableList<String> tempList = FXCollections.observableArrayList();
    public static BorderPane root;
    
    @Override
	public void initialize(URL paramURL, ResourceBundle paramResourceBundle) {
    	
    	
		
	}
    
    @Override
	public void start(final Stage primaryStage) throws Exception {

    	try{
	    	this.primaryStage = primaryStage;
	    	primaryStage.setTitle("Embedded Media Player");
	    	FXMLLoader loader = new FXMLLoader(
	    			MediaController.class.getResource("/com/kc/view/mediaControl.fxml"));
	    	root = (BorderPane)loader.load();
	    	HBox rectangle = new HBox();
	    	rectangle.setPrefSize(500, 300);
	        root.setCenter(rectangle);
	        Scene scene = new Scene(root);
	        this.scene = scene;
	        this.primaryStage.setScene(this.scene);
	        
	        listBox = new VBox(5);
	        playList = new ListView<String>();
	        add = new Button("Add");
	        listBox.getChildren().addAll(add,playList);
	        this.primaryStage.show();
	        playVideo(MEDIA_URL);
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
        
	}
    
    public void playVideo(String MEDIA_URL)
    {
    	try{
	    	Media media = new Media (MEDIA_URL);
	        // create media player
	        mp = new MediaPlayer(media);
	        mediaView = new MediaView(mp);
	        
	        mp.setAutoPlay(true);
	        mp.play();
	        mediaView.setPreserveRatio(false);
	        mediaView.autosize();
	        final MediaControl mediaControl = new MediaControl(mp, mediaView);
	        root.setCenter(mediaView);
	        root.setBottom(mediaControl);
	       
	       
	        mediaView.setFitHeight(scene.getHeight()-25-40);
	        mediaView.setFitWidth(scene.getWidth());
	        scene.widthProperty().addListener(new ChangeListener<Number>() {
	
				@Override
				public void changed(ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					mediaView.setFitWidth(newValue.doubleValue());
				}
			});
	        
	        scene.heightProperty().addListener(new ChangeListener<Number>() {
	
				@Override
				public void changed(ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					//Subtracting height of MediaControl & Menu on top
					if(!primaryStage.isFullScreen())
						 mediaView.setFitHeight(newValue.doubleValue()-25-40);
					else
						mediaView.setFitHeight(newValue.doubleValue());
				}
			});
	        
	        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent mouseEvent) {
	            	if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
	                    if(mouseEvent.getClickCount() == 2){
	                    	if(primaryStage.isFullScreen())
	                    	{
	                    		//scene.setRoot(root);
	                    		primaryStage.setFullScreen(false);
	                    	}
	                    	else
	                    	{
	                    		VBox box = new VBox();
	                    		box.getChildren().addAll(mediaView,mediaControl);
	                    		scene.setRoot(box);
	                    		primaryStage.setFullScreen(true);
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
	        	}
	        	else {
	        	event.consume();
	        	}
	        	}
	        	});
	        scene.setOnDragDropped(new EventHandler<DragEvent>() {

				@Override
				public void handle(DragEvent event) {
					
					Dragboard db = event.getDragboard();
					if(db.hasFiles())
					{
						String filePath = null;
						for (File file:db.getFiles())
						{
							tempList.add(file.getAbsolutePath());
							filePath = file.getAbsolutePath();
						}
						mp.stop();
						playList.setItems(tempList);
						playVideo("file:/"+(filePath).replace("\\", "/").replace(" ", "%20"));
						System.out.println(filePath);
					}
					
				}
			});
	        
	        playList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

				@Override
				public void changed(ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					
					mp.stop();
					playVideo("file:/"+(newValue).replace("\\", "/").replace(" ", "%20"));
					
				}
			});
	        playList.setOnDragOver(new EventHandler<DragEvent>() {
	        	@Override
	        	public void handle(DragEvent event) {
	        	Dragboard db = event.getDragboard();
	        	if (db.hasFiles()) {
	        	event.acceptTransferModes(TransferMode.COPY);
	        	}
	        	else {
	        	event.consume();
	        	}
	        	}
	        	});
	        playList.setOnDragDropped(new EventHandler<DragEvent>() {

				@Override
				public void handle(DragEvent event) {
					
					Dragboard db = event.getDragboard();
					if(db.hasFiles())
					{
						String filePath = null;
						for (File file:db.getFiles())
						{
							tempList.add(file.getAbsolutePath());
							filePath = file.getAbsolutePath();
						}
						mp.stop();
						playList.setItems(tempList);
						playVideo("file:/"+(filePath).replace("\\", "/").replace(" ", "%20"));
						System.out.println(filePath);
					}
					
				}
			});
	        
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static void main(String[] args) {
        launch(args);
    }

}
