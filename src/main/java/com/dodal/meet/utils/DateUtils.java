package com.dodal.meet.utils;

import com.dodal.meet.controller.response.user.UserCertPerWeek;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtils {

    public static final int MON = 0;
    public static final int TUE = 1;
    public static final int WED = 2;
    public static final int THU = 3;
    public static final int FRI = 4;
    public static final int SAR = 5;
    public static final int SUN = 6;


    public static String parsingTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(timestamp);
    }

    public static String parsingString(String date) {
        String[] dateArr = date.split(" ");
        StringBuilder sb = new StringBuilder();
        if (dateArr[2].equals("PM")) {
            sb.append(dateArr[0]).append(" ").append("오후").append(" ").append(dateArr[1]);
        } else {
            sb.append(dateArr[0]).append(" ").append("오전").append(" ").append(dateArr[1]);
        }
        return sb.toString();
    }

    public static Map<Integer, String> getWeekInfo() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate tuesday = today.with(DayOfWeek.TUESDAY);
        LocalDate wednesday = today.with(DayOfWeek.WEDNESDAY);
        LocalDate thursday = today.with(DayOfWeek.THURSDAY);
        LocalDate friday = today.with(DayOfWeek.FRIDAY);
        LocalDate saturday = today.with(DayOfWeek.SATURDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        Map<Integer, String> dateMaps = new HashMap();
        dateMaps.put(MON, monday.format(formatter));
        dateMaps.put(TUE, tuesday.format(formatter));
        dateMaps.put(WED, wednesday.format(formatter));
        dateMaps.put(THU, thursday.format(formatter));
        dateMaps.put(FRI, friday.format(formatter));
        dateMaps.put(SAR, saturday.format(formatter));
        dateMaps.put(SUN, sunday.format(formatter));
        return dateMaps;
    }

    public static String getMonday() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return monday.format(formatter);
    }
}
