package com.dodal.meet.service;


import com.dodal.meet.controller.response.ResponseSuccess;
import com.dodal.meet.controller.response.feed.FeedResponse;
import com.dodal.meet.repository.ChallengeRoomEntityRepository;
import com.dodal.meet.repository.TokenEntityRepository;
import com.dodal.meet.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;


    @Transactional(readOnly = true)
    public List<FeedResponse> getFeeds() {
        return challengeRoomEntityRepository.getFeeds();
    }
}
