package com.dodal.meet.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateUtils {
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
}
