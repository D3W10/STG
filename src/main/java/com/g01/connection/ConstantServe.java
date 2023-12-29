package com.g01.connection;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;

import java.awt.*;
import java.time.format.TextStyle;
import java.util.Locale;

public class ConstantServe {
    public static DatePickerSettings getDatePickerSettings() {
        DatePickerSettings dps = new DatePickerSettings();

        dps.setSizeDatePanelMinimumHeight(200);
        dps.setFormatForDatesCommonEra("dd/MM/yyyy");
        dps.setHighlightPolicy(localDate -> {
            String dotw = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            boolean isWeekend = dotw.equals("Sat") || dotw.equals("Sun");

            return new HighlightInformation(isWeekend ? new Color(230, 230, 230) : new Color(255, 255, 255));
        });

        return dps;
    }

    public static TimePickerSettings getTimePickerSettings() {
        TimePickerSettings tps = new TimePickerSettings();

        tps.setFormatForDisplayTime("hh:mm");
        tps.use24HourClockFormat();

        return tps;
    }
}
