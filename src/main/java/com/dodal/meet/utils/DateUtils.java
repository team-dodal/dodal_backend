package com.dodal.meet.utils;

import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import org.springframework.util.ObjectUtils;
import java.sql.Timestamp;
import java.text.ParseException;
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

    public static Map<String, String> dayOfWeekDetailMap = new HashMap<>();
    public static Map<Integer, String> dayOfWeekNameMap = new HashMap<>();

    static {
        dayOfWeekNameMap.put(0, "월");
        dayOfWeekNameMap.put(1, "화");
        dayOfWeekNameMap.put(2, "수");
        dayOfWeekNameMap.put(3, "목");
        dayOfWeekNameMap.put(4, "금");
        dayOfWeekNameMap.put(5, "토");
        dayOfWeekNameMap.put(6, "일");
    }

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

    public static String getToday() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return today.format(formatter);
    }

    public static String getYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return yesterday.format(formatter);
    }

    public static String getMonth() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        return today.format(formatter);
    }

    public static void validDateYM(String dateYM) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
            dateFormat.setLenient(Boolean.FALSE);   // 입력 값 오류 시 에러 반환 설정
            dateFormat.parse(dateYM);
        } catch (ParseException e) {
            throw new DodalApplicationException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

    public static String convertToDayOfWeekDetail(String registeredDate) {

        final int year = Integer.parseInt(registeredDate.substring(0, 4));
        final int mon = Integer.parseInt(registeredDate.substring(4, 6));
        final int day = Integer.parseInt(registeredDate.substring(6, 8));

        if (ObjectUtils.isEmpty(dayOfWeekDetailMap.get(registeredDate))) {
            LocalDate date = LocalDate.of(year, mon, day);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayOfWeekName = dayOfWeekNameMap.get(dayOfWeek.getValue() - 1);
            dayOfWeekDetailMap.put(registeredDate, convertToDayFormatWithDot(registeredDate) + " ("+dayOfWeekName+")");
        }
        return dayOfWeekDetailMap.get(registeredDate);
    }

    private static String convertToDayFormatWithDot(String registerDate) {
        return new StringBuilder().append(registerDate.substring(0,4)).append(".")
                        .append(registerDate.substring(4,6)).append(".")
                        .append(registerDate.substring(6, 8)).toString();
    }

    public static String convertWeekCode(String registerDate) {
        Map<Integer, String> weekInfo = getWeekInfo();
        int code = 0;
        boolean finishFlag = false;
        for(int i= 0; i < 7; i++) {
            String day = weekInfo.get(i);
            if (day.equals(registerDate)) {
                code = i;
                finishFlag = true;
                break;
            }
        }
        if (!finishFlag) {
            throw new DodalApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return String.valueOf(code);
    }
    public static String parsingDay(String registerDate) {
        String day = registerDate.substring(6, 8);
        return String.valueOf(Integer.parseInt(day));
    }

    public static String convertToRegisterCode(Timestamp timestamp) {
        long currentTime = System.currentTimeMillis();
        long timestampTime = timestamp.getTime();

        long diff = currentTime - timestampTime;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        if (weeks > 0) {
            return weeks + "주 전";
        } else if (days > 6) {
            return "1주 전";
        } else if (days > 0) {
            return days + "일 전";
        } else if (hours > 0) {
            return hours + "시간 전";
        } else if (minutes > 0) {
            return minutes + "분 전";
        } else {
            return "방금 전";
        }
    }
}
