package com.ita.main;

import com.ita.controller.MediaPlayerController;
import com.ita.controller.PlaylistController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by itachi on 10/2/15.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ita/fxml/mediaplayer.fxml"));
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane, 650, 400);
        primaryStage.setScene(scene);
        MediaPlayerController controller = ((MediaPlayerController) loader.getController());
        // Load Playlist FXML and inject controller/root
        FXMLLoader playListLoader = new FXMLLoader(getClass().getResource("/com/ita/fxml/playlist.fxml"));
        playListLoader.load();
        controller.injectPlayListController((PlaylistController) playListLoader.getController());
        controller.injectPlayListRoot(playListLoader.getRoot());
        bindSize(controller, scene);
        controller.setStage(primaryStage);
        primaryStage.show();
        controller.applyDragAndDropFeatures(scene);
    }



    private void bindSize(MediaPlayerController controller, Scene scene){
        controller.timerSliderWidthProperty().bind(scene.widthProperty().subtract(500));
        controller.mediaViewWidthProperty().bind(scene.widthProperty());
        controller.mediaViewHeightProperty().bind(scene.heightProperty().subtract(70));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
