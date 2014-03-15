package com.kc.service;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MediaControlHide implements Runnable {

	private Stage primaryStage;
	private MediaControl mediaControl;

	public MediaControlHide() {
	}

	public MediaControlHide(Stage primaryStage, MediaControl mediaControl) {
		this.primaryStage = primaryStage;
		this.mediaControl = mediaControl;
	}

	@Override
	public void run() {

		try {
			final FadeTransition ft = new FadeTransition(Duration.millis(1000),
					mediaControl);
			final AtomicBoolean control = new AtomicBoolean(true);
			while (primaryStage.isFullScreen()) {
				if(control.get())
				{
					if (mediaControl.getOpacity() == 1) {
						Thread.sleep(4000);
						ft.setFromValue(1.0);
						ft.setToValue(0.0);
						ft.setCycleCount(1);
						ft.play();
					}
					mediaControl.setOnMouseMoved(new  EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent event) {
							ft.stop();
							control.set(false);
							mediaControl.setOpacity(1.0);
						}
					});
				}
			}
			ft.stop();
			mediaControl.setOpacity(1.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public boolean mouseOnMediaControl() { final AtomicBoolean status = new
	 * AtomicBoolean(false); mediaControl.addEventFilter(MouseEvent.ANY, new
	 * EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent event) {
	 * System.out.println(event.getX());
	 * System.out.println(mediaControl.getLayoutX()); if(event.getX()>0 ||
	 * event.getY() >0) { mediaControl.setOpacity(1.0); status.set(true); } }
	 * }); return status.get(); }
	 */

}
