package com.ita.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** replace this with Dialogs in Java 8u40 **/
public class WarningDialog
{
	private final Stage dialog = new Stage(StageStyle.TRANSPARENT);

	public WarningDialog(final Stage primaryStage) {
		dialog.initOwner(primaryStage);
		createDialog();
	}

	private void createDialog() {
		dialog.initModality(Modality.WINDOW_MODAL);
		final VBox box = new VBox(10);
		box.setAlignment(Pos.CENTER);
		final Label label = new Label("Please use a supported type !");
		final Button button = new Button("OK");
		button.setOnAction((e) -> {
			dialog.getOwner().getScene().getRoot().setEffect(null);
			dialog.close();
		});
		box.getChildren().addAll(label, button);
		final Scene scene = new Scene(box);
		scene.getStylesheets()
				.add(getClass().getResource("/com/ita/style/modal-dialog.css")
						.toExternalForm());
		dialog.setScene(scene);
	}

	public void show() {
		dialog.show();
	}
}