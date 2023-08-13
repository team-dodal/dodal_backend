package com.dodal.meet.utils;

import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUtils {

    public static String serverUrl;

    @Value("${server.url}")
    public void setServerUrl(String value) {
        serverUrl = value;
    }
    public static String ROOM_STUDY_2X1 = "/study-2x1.png";
    public static String ROOM_STUDY_1X1 = "/study-1x1.png";
    public static String ROOM_HEALTH_2X1 = "/health-2x1.png";
    public static String ROOM_HEALTH_1X1 = "/health-1x1.png";
    public static String ROOM_HOBBY_2X1 = "/hobby-2x1.png";
    public static String ROOM_HOBBY_1X1 = "/hobby-1x1.png";
    public static String ROOM_ROUTINE_2X1 = "/routine-2x1.png";
    public static String ROOM_ROUTINE_1X1 = "/routine-1x1.png";
    public static String ROOM_ETC_2X1 = "/etc-2x1.png";
    public static String ROOM_ETC_1X1 = "/etc-1x1.png";

    public static String findByCategoryValueToRoomThumbnailImageUrl(String categoryValue) {
        String imagePath = serverUrl + "/image/room";
        if (categoryValue.equals(CategoryUtils.HEALTH)) {
            return imagePath + ROOM_HEALTH_1X1;
        } else if (categoryValue.equals(CategoryUtils.STUDY)) {
            return imagePath + ROOM_STUDY_1X1;
        } else if (categoryValue.equals(CategoryUtils.HOBBY)) {
            return imagePath + ROOM_HOBBY_1X1;
        } else if (categoryValue.equals(CategoryUtils.ROUTINE)) {
            return imagePath + ROOM_ROUTINE_1X1;
        } else if (categoryValue.equals(CategoryUtils.ETC)) {
            return imagePath + ROOM_ETC_1X1;
        } else {
            throw new DodalApplicationException(ErrorCode.NOT_FOUND_CATEGORY);
        }
    }
}
