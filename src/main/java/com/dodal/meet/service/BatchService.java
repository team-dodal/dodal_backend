package com.dodal.meet.service;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BatchService {

    private final ChallengeRoomService challengeRoomService;


    // cron : 초 분 시 일 월 요일 (년)
    @Scheduled(cron = "0 0 1 * * *")
    public void updateChallengeUserCertCnt() {
        challengeRoomService.updateChallengeUserCertCnt();
    }
}
