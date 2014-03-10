package com.kc.service;

import javafx.animation.FadeTransition;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MediaControlHide implements Runnable {

	private Stage primaryStage;
	private MediaControl mediaControl;

	public MediaControlHide()
	{}
	
	public MediaControlHide(Stage primaryStage, MediaControl mediaControl) {
		this.primaryStage = primaryStage;
		this.mediaControl = mediaControl;
	}

	@Override
	public void run() {

		try {
			FadeTransition ft = new FadeTransition(Duration.millis(1000),
					mediaControl);
			while (primaryStage.isFullScreen()) {
				if(mediaControl.getOpacity() == 1)
				{
					System.out.println("This is it");
					Thread.sleep(2000);
					ft.setFromValue(1.0);
					ft.setToValue(0.0);
					ft.setCycleCount(1);
					ft.play();
				}
			}
			System.out.println("Exit FullScreen !");
			ft.stop();
			mediaControl.setOpacity(1.0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public boolean mouseOnMediaControl()
	{
		final AtomicBoolean status = new AtomicBoolean(false);
		mediaControl.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println(event.getX());
				System.out.println(mediaControl.getLayoutX());
				if(event.getX()>0 || event.getY() >0)
				{
					mediaControl.setOpacity(1.0);
					status.set(true);
				}
			}
		});
		return status.get();
	}*/

}
