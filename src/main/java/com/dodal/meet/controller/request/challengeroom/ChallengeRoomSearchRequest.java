package com.dodal.meet.controller.request.challengeroom;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChallengeRoomSearchRequest {

    private String word;
    private String conditionCode;
    private List<Integer> certCntList;
    private Pageable pageable;
}
