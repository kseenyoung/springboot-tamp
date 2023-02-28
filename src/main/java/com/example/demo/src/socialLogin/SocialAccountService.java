package com.example.demo.src.socialLogin;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.src.account.model.*;
import com.example.demo.src.socialLogin.model.SocialAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocialAccountService {
    // private final PasswordEncoder passwordEncoder;
    // private final UserRepository userRepository;

    public SocialAccount socialLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        SocialAccount socialAccount = getSocialAccount(accessToken);

        // 3. 카카오ID로 회원가입 처리
        Account socialUser = registerSocialAccountIfNeed(socialAccount);

        // 4. 강제 로그인 처리
        Authentication authentication = forceLogin(socialUser);

        // 5. response Header에 JWT 토큰 추가
        socialUsersAuthorizationInput(authentication, response);
        return socialAccount;
    }

    // 3. 카카오ID로 회원가입 처리
    private Account registerSocialAccountIfNeed(SocialAccount socialAccount) {
        // DB 에 중복된 email이 있는지 확인
        String kakaoEmail = socialAccount.getAccountEmail();
        // String nickname = socialAccount.getNickname();
        // Account kakaoUser = socialAccount.findByUserEmail(kakaoEmail)
        //         .orElse(null);

        if (kakaoEmail == null) {
            // 회원가입
            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            String profile = "https://ossack.s3.ap-northeast-2.amazonaws.com/basicprofile.png";

            kakaoUser = new User(kakaoEmail, nickname, profile, encodedPassword);
            userRepository.save(kakaoUser);

        }
        return kakaoUser;
    }

    // 2. 토큰으로 카카오 API 호출
	private SocialAccount getSocialAccount(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

         // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
             "https://kapi.kakao.com/v2/user/me",
             HttpMethod.POST,
             kakaoUserInfoRequest,
             String.class
         );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        return new SocialUserInfoDto(id, nickname, email);
    };

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "9cefa6a9dfbb156f28ccd0a7b1adbd0a"); //REST API 키
        body.add("redirect_uri", "http://localhost:9000/oauth");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }
}