package com.dodal.meet.auth;

import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.User;
import com.dodal.meet.model.entity.ChallengeRoomEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.ChallengeRoomEntityRepository;
import com.dodal.meet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionHostCheckSupport<T> implements PermissionChecker<T>{
    private final ChallengeRoomEntityRepository challengeRoomEntityRepository;
    private final UserService userService;

    public boolean hasPermission(T t) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final User user = (User) authentication.getPrincipal();
        final UserEntity hostEntity = userService.getCachedUserEntity(user);
        final Integer roomId = (Integer) t;
        final ChallengeRoomEntity roomEntity = challengeRoomEntityRepository.findById(roomId).orElseThrow(() -> new DodalApplicationException(ErrorCode.NOT_FOUND_ROOM));
        return roomEntity.getHostId().equals(hostEntity.getId());
    }
}
