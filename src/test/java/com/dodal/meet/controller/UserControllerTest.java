package com.dodal.meet.controller;

import com.dodal.meet.controller.request.user.UserSignInRequest;
import com.dodal.meet.controller.request.user.UserSignUpRequest;
import com.dodal.meet.controller.request.user.UserUpdateRequest;
import com.dodal.meet.controller.response.user.UserInfoResponse;
import com.dodal.meet.controller.response.user.UserSignInResponse;
import com.dodal.meet.controller.response.user.UserSignUpResponse;
import com.dodal.meet.custom.WithMockCustomUser;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.SocialType;
import com.dodal.meet.model.User;
import com.dodal.meet.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockCustomUser
class UserControllerTest {

 /*
    @SpringBootTest + @AutoConfigureMockMvc는 통합테스트 적용할 때 사용한다.
    @WebMvcTest는 MVC쪽만 슬라이스(slice) 테스트

 */
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void 회원가입() throws Exception {
        SocialType socialType = SocialType.KAKAO;
        String socialId = "231595151";
        String email = "sasca37@naver.com";
        String nickname = "노래하는 어피치";
        String profileUrl = "https://aws-s3.com";
        String content = "안녕하세요";
        List<String> tagList = Arrays.asList("001001", "001002");
        UserSignUpRequest request =  UserSignUpRequest
                .builder()
                .socialType(socialType)
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .profileUrl(profileUrl)
                .content(content)
                .tagList(tagList)
                .build();
        when(userService.signUp(request)).thenReturn(mock(UserSignUpResponse.class));

        mockMvc.perform(post("/api/v1/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        ).andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void 로그인() throws Exception {
        SocialType socialType = SocialType.KAKAO;
        String socialId = "231595151";
        UserSignInRequest request = UserSignInRequest
                .builder()
                .socialType(socialType)
                .socialId(socialId)
                .build();

        when(userService.signIn(request)).thenReturn(mock(UserSignInResponse.class));

        mockMvc.perform(post("/api/v1/users/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void 회원정보_수정() throws Exception {
        String profileUrl = "https://aws-s3.com";
        String content = "안녕하세요";
        List<String> tagList = Arrays.asList("001001", "001002");
        UserUpdateRequest request = UserUpdateRequest
                .builder()
                .nickname("updateNickname")
                .content(content)
                .tagList(tagList)
                .profileUrl(profileUrl)
                .build();

        mockMvc.perform(patch("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 회원정보_수정시_유효하지_않은_사용자() throws Exception {
        String profileUrl = "https://aws-s3.com";
        String content = "안녕하세요";
        List<String> tagList = Arrays.asList("001001", "001002");
        UserUpdateRequest request = UserUpdateRequest
                .builder()
                .nickname("updateNickname")
                .content(content)
                .tagList(tagList)
                .profileUrl(profileUrl)
                .build();

        mockMvc.perform(patch("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        ).andDo(print()).andExpect(status().isUnauthorized());
    }

    @Test
    void 회원탈퇴() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me"))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void 회원탈퇴_유효하지_않은_사용자() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me"))
                .andDo(print()).andExpect(status().isUnauthorized());
    }
}