package com.dodal.meet.utils;

import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
            dateFormat.setLenient(false);   // 입력 값 오류 시 에러 반환 설정
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

    public static void main(String[] args) {
        Map<Integer, String> weekInfo = getWeekInfo();
        String monday = weekInfo.get(DateUtils.MON);
        System.out.println(monday);

        LocalDate date1 = LocalDate.of(2021, 10, 30);
        LocalDate date2 = LocalDate.of(2021, 11, 5);

        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        long monthsBetween = ChronoUnit.MONTHS.between(date1, date2);
        long yearsBetween = ChronoUnit.YEARS.between(date1, date2);

        System.out.println("Days between: " + daysBetween);
        System.out.println("Months between: " + monthsBetween);
        System.out.println("Years between: " + yearsBetween);
    }
}
