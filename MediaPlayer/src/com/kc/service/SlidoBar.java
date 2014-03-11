package com.kc.service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

public class SlidoBar extends StackPane {
	private Slider slider;
	private ProgressBar progressBar;

	public Slider getSlider() {
		return slider;
	}

	public SlidoBar() {
		slider = new Slider();
		progressBar = new ProgressBar(0);
		getStylesheets().add(
				this.getClass().getResource("/com/kc/style/slidoBar.css")
						.toExternalForm());
		progressBar.setPrefWidth(slider.getPrefWidth());
		progressBar.setMinWidth(slider.getMinWidth());
		progressBar.setMaxWidth(slider.getMinWidth());

		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
					ObservableValue<? extends Number> paramObservableValue,
					Number paramT1, Number paramT2) {
				progressBar.setMinWidth(paramT2.doubleValue());
				progressBar.setMaxWidth(paramT2.doubleValue());
				slider.setMinWidth(paramT2.doubleValue());
				slider.setMaxWidth(paramT2.doubleValue());
			}

		});

		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				progressBar.setProgress(new_val.doubleValue() / 100);
			}
		});

		getChildren().addAll(progressBar, slider);
		setPadding(new Insets(15));
	}

}
