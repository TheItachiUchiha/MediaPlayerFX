package com.ita.ui;

import com.ita.util.PropertiesUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutDialog {
    final Stage stage = new Stage();
    final Button closeButton = new Button();
    final Hyperlink link = new Hyperlink();

    public AboutDialog(Stage primaryStage){
        prepareStage(primaryStage);
        addListeners();
        stage.setScene(prepareScene());
    }

    public void showAbout() {
        stage.show();
    }

    private void prepareStage(Stage primaryStage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
    }

    private Scene prepareScene() {
        VBox stageBox = new VBox(10);
        stageBox.setId("about");
        stageBox.setPadding(new Insets(0, 0, 0, 10));
        HBox closeBox = new HBox();
        closeButton.setId("close");
        closeBox.setAlignment(Pos.TOP_RIGHT);
        closeBox.getChildren().add(closeButton);
        Label name = new Label(PropertiesUtils.readDetails().get("name"));
        name.setId("header1");
        Label version = new Label(PropertiesUtils.readDetails().get("version"));
        version.setId("version");
        link.setText("Click here to visit us");
        link.setId("link");
        stageBox.getChildren().addAll(closeBox, name, version, link);
        Scene scene = new Scene(stageBox, 400, 150);
        scene.getStylesheets().add(
                getClass().getResource(
                        "/com/ita/style/MediaPlayer.css").toExternalForm());
        return scene;
    }

    private void addListeners() {
        closeButton.setOnAction((e) -> stage.close());

        link.setOnAction((e) -> {
            try {
                Desktop.getDesktop().browse(new URI(PropertiesUtils.readDetails().get("link")));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        });
    }

}
