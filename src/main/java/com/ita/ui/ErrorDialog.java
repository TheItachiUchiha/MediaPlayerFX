package com.ita.ui;

import javafx.scene.control.Alert;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ErrorDialog {

    private final Alert alert = new Alert(Alert.AlertType.ERROR);

    public ErrorDialog(Throwable error) {
        alert.setTitle("Oops, there was an error...");
        alert.setHeaderText(null);
        alert.setContentText("Error: " + error);
    }

    public void show() {
        alert.show();
    }
}
