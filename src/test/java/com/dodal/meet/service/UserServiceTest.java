package com.dodal.meet.service;

import com.dodal.meet.controller.request.user.UserSignInRequest;
import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.controller.response.user.UserSignInResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.fixture.UserEntityFixture;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.entity.TagEntity;
import com.dodal.meet.model.entity.TokenEntity;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.TagEntityRepository;
import com.dodal.meet.repository.TokenEntityRepository;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.repository.UserTagEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import java.util.Arrays;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private UserTagEntityRepository userTagEntityRepository;

    @MockBean
    private TokenEntityRepository tokenEntityRepository;

    @MockBean
    private TagEntityRepository tagEntityRepository;

    private static final String SOCIAL_ID ="123456789";
    private static final SocialType SOCIAL_TYPE = SocialType.KAKAO;

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {

        when(userEntityRepository.findBySocialIdAndSocialType(SOCIAL_ID, SOCIAL_TYPE)).thenReturn(Optional.empty());
        UserEntity userEntity = UserEntityFixture.getUserEntity(SOCIAL_ID, SOCIAL_TYPE);
        when(userEntityRepository.save(any())).thenReturn(userEntity);
        TagEntity tagEntity = TagEntity.builder().name("체중 관리").tagValue("001001").build();
        when(tagEntityRepository.findByTagValue(any())).thenReturn(Optional.of(tagEntity));
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
                .socialId(SOCIAL_ID)
                .socialType(SOCIAL_TYPE)
                .nickname("nickname")
                .profileUrl("imgUrl")
                .content("content")
                .email("sss@gmail.com")
                .tagList(Arrays.asList("001001"))
                .build();

        Assertions.assertDoesNotThrow(() ->userService.signUp(userSignUpRequest));
    }

    @Test
    void 이미_가입한_회원이_가입하는_경우_예외를_반환한다() {
        UserEntity fixture = UserEntityFixture.getUserEntity(SOCIAL_ID, SOCIAL_TYPE);

        when(userEntityRepository.findBySocialIdAndSocialType(SOCIAL_ID, SOCIAL_TYPE)).thenReturn(Optional.of(fixture));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
                .socialId(SOCIAL_ID)
                .socialType(SOCIAL_TYPE)
                .nickname("nickname")
                .profileUrl("imgUrl")
                .content("content")
                .email("sss@gmail.com")
                .tagList(Arrays.asList("001001"))
                .build();
        DodalApplicationException e = Assertions.assertThrows(DodalApplicationException.class, () -> userService.signUp(userSignUpRequest));

        Assertions.assertEquals(ErrorCode.INVALID_SIGNUP_REQUEST, e.getErrorCode());
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder().socialId(SOCIAL_ID).socialType(SOCIAL_TYPE).build();
        UserSignInRequest userSignInRequest = UserSignInRequest.builder().socialId(SOCIAL_ID).socialType(SOCIAL_TYPE).build();

        TokenEntity tokenEntity = TokenEntity.builder().fcmToken("token").refreshToken("token").build();

        UserEntity fixture = UserEntity.newInstance(userSignUpRequest, tokenEntity);

        when(tokenEntityRepository.findById(any())).thenReturn(Optional.of(tokenEntity));
        when(userEntityRepository.findBySocialIdAndSocialType(SOCIAL_ID, SOCIAL_TYPE)).thenReturn(Optional.of(fixture));
        UserSignInResponse userSignInResponse = userService.signIn(userSignInRequest);

        Assertions.assertDoesNotThrow(() -> userSignInResponse);
        Assertions.assertEquals(userSignInResponse.getIsSigned(), "true");
    }


    @Test
    void 로그인시_userName으로_회원가입한_유저가_없는_경우() {
        when(userEntityRepository.findBySocialIdAndSocialType(SOCIAL_ID, SOCIAL_TYPE)).thenReturn(Optional.empty());

        UserSignInRequest userSignInRequest = UserSignInRequest.builder().socialId(SOCIAL_ID).socialType(SOCIAL_TYPE).build();

        UserSignInResponse userSignInResponse = userService.signIn(userSignInRequest);
        Assertions.assertDoesNotThrow(() -> userSignInResponse);
        Assertions.assertEquals(userSignInResponse.getIsSigned(), "false");
    }
}