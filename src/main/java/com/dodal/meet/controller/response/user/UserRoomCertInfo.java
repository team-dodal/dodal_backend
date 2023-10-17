package com.dodal.meet.controller.response.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserRoomCertInfo {

    private int maxContinueCertCnt;

    private int totalCertCnt;

}
