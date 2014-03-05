package com.kc.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import com.kc.service.MediaControl;
import com.sun.javafx.geom.Rectangle;

public class MediaController extends Application implements Initializable{
    
    private MediaPlayer mp;
    private MediaView mediaView;
    private Stage primaryStage;
    private Scene scene;
    
    private static final String MEDIA_URL = "file:/f:Jeff_Dunham.mp4";
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
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    }
	    public static void main(String[] args) {
	        launch(args);
	    }

}
