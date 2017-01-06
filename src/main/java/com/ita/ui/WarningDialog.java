package com.ita.ui;

import javafx.scene.control.Alert;

public class WarningDialog {

	private final Alert alert = new Alert(Alert.AlertType.WARNING);

	public WarningDialog(String message) {
	    alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
	}

	public void show() {
        alert.show();
	}
}