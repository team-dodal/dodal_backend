package com.dodal.meet.controller.response.user;

import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.utils.DateUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Builder
@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserCertPerWeek {

    private int dayCode;
    private String day;
    private String certImgUrl;


    @QueryProjection
    public UserCertPerWeek(String date, String certImgUrl) {
        this.dayCode = convertDateToCode(date);
        this.day = convertCodeToName(this.dayCode);
        this.certImgUrl = certImgUrl;
    }

    private int convertDateToCode(final String today) {
        String monday = DateUtils.getMonday();
        long endTime = convertToDate(today).getTime();
        long startTime = convertToDate(monday).getTime();
        int timeDiff =  (int) (endTime - startTime);
        return timeDiff / (24 * 60 * 60 * 1000);
    }

    private Date convertToDate(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST);
        }
    }

    private String convertCodeToName(final int dayCode) {
        switch (dayCode) {
            case 0 : return "월요일";
            case 1 : return "화요일";
            case 2 : return "수요일";
            case 3 : return "목요일";
            case 4 : return "금요일";
            case 5 : return "토요일";
            case 6 : return "일요일";
        }
        throw new DodalApplicationException(ErrorCode.INVALID_DAY_CODE);
    }
}
