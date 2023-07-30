package com.dodal.meet.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateUtils {
    public static String parsingTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(timestamp);
    }

}
