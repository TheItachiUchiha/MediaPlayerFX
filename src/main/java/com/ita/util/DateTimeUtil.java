package com.ita.util;

import javafx.util.Duration;

/**
 * Created by Abhi on 14-02-2015.
 */
public final class DateTimeUtil {

    public static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        int elapsedMinutes = intElapsed / 60 - elapsedHours * 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            int durationMinutes = intDuration / 60 - durationHours * 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds, durationHours,
                        durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes,
                        elapsedSeconds, durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
}
