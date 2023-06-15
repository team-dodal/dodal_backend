package com.dodal.meet.service;


import com.dodal.meet.controller.request.user.UserLoginRequest;
import com.dodal.meet.controller.response.user.UserLoginResponse;
import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.model.entity.UserEntity;
import com.dodal.meet.repository.UserEntityRepository;
import com.dodal.meet.utils.JwtTokenUtils;
import com.dodal.meet.utils.RestTemplateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialService {
    private final String kakaoUrl = "https://kapi.kakao.com/v2/user/me";
    private final UserEntityRepository userEntityRepository;

    @Value("${jwt.secret-key}")
    private String key;

    public UserLoginResponse kakaoLogin(UserLoginRequest request) {
        RestTemplate restTemplate = RestTemplateUtils.getRestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        header.setBearerAuth(request.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(header);

//        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoUrl)
//                .queryParam("id_token", request.getAccessToken())
//                .build();
        try {
            ResponseEntity<String> result = restTemplate.exchange(
                kakaoUrl, HttpMethod.GET, entity, String.class);
            JSONParser jsonParser = new JSONParser();
            JSONObject data = (JSONObject) jsonParser.parse(result.getBody());

            String email = (String) ((JSONObject) data.get("kakao_account")).get("email");
            String accessToken = JwtTokenUtils.generateAccessToken(email, key);
            String refreshToken = JwtTokenUtils.generateRefreshToken(email, key);
            UserEntity user = UserEntity.of(email, request.getSocialType(), refreshToken);
            userEntityRepository.save(user);

            return UserLoginResponse.builder()
                    .socialType(request.getSocialType())
                    .email(email)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new DodalApplicationException(ErrorCode.INVALID_TOKEN);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
