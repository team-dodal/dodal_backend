package com.dodal.meet.controller.response.user;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class UserRoomCertInfo {

    private int maxContinueCertCnt;

    private int totalCertCnt;

}
