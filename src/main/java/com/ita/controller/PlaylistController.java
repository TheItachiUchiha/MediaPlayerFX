package com.ita.controller;

import com.ita.util.FileUtils;
import com.ita.util.PropertiesUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {

    @FXML
    private Button add;

    @FXML
    private Button delete;

    @FXML
    private ListView playList;

    private ObservableList playListFiles =FXCollections.observableArrayList();
    private ObjectProperty<Path> selectedMedia = new SimpleObjectProperty<>();
    private ObjectProperty<Path> deletedMedia = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playList.setOnMouseClicked((click) -> {
            if (click.getClickCount() == 2) {
                if (playList.getSelectionModel().getSelectedItem() != null) {
                    selectedMedia.setValue((Path) playList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    @FXML
    void add(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Files",
                        PropertiesUtils.readFormats()));
        List<Path> listOfFiles = new ArrayList<Path>();
        listOfFiles = FileUtils.convertListFiletoListPath(chooser.showOpenMultipleDialog(((Button) event.getSource()).getScene().getWindow()));
        if (listOfFiles != null) {
            listOfFiles.stream().forEach(System.out::println);
            listOfFiles.stream().forEach(playListFiles::add);
            playListFiles.stream().forEach(System.out::println);
            playList.setItems(playListFiles);
        }
    }

    @FXML
    void delete(ActionEvent event) {
        if (playList.getSelectionModel().getSelectedItem() != null) {
            if(null!=playListFiles || !playListFiles.isEmpty()) {
                deletedMedia.setValue((Path) playList.getSelectionModel().getSelectedItem());
                playListFiles.remove(playList.getSelectionModel().getSelectedItem());
                playList.setItems(playListFiles);
            }
        }
    }

    public ObservableList listViewItems(){
       return playListFiles;
    }

    public ObjectProperty<Path> selectedFile(){
        return selectedMedia;
    }

    public ObjectProperty<Path> deletedFile() {
        return deletedMedia;
    }
}
