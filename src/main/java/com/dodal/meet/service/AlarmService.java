package com.dodal.meet.service;

import com.dodal.meet.controller.response.alarm.AlarmHistResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.entity.AlarmHistEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.AlarmHistEntityRepository;
import com.dodal.meet.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final UserEntityRepository userEntityRepository;

    private final AlarmHistEntityRepository alarmHistEntityRepository;

    @Transactional
    public void saveAlarmHist(final AlarmHistResponse response) {
        AlarmHistEntity entity = AlarmHistEntity.alarmHistResponseToEntity(response);
        alarmHistEntityRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<AlarmHistResponse> getAlarmHists(Long userId) {
        userEntityRepository.findById(userId).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        List<AlarmHistEntity> alarmHists = alarmHistEntityRepository.findAllByUserId(userId);
        return AlarmHistEntity.entityToAlarmHistResponseList(alarmHists);
    }

    @Transactional
    public void delAlarmHists(Long userId) {
        userEntityRepository.findById(userId).orElseThrow(() -> new DodalApplicationException(ErrorCode.INVALID_USER_REQUEST));
        alarmHistEntityRepository.deleteAllByUserId(userId);
    }
}
