package com.example.demo.src.account.model;

import java.util.Properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoProfile {
    private Long id;
    private Properties properties;
    private KakaoAccont kakao_account;

    public class Properties{
        public String nickname;
    }

    public class KakaoAccont{
        public Boolean profile_nickname_needs_agreement;
        public Profile profile;
        public Boolean has_email;
        public Boolean email_needs_agreement;
        public Boolean is_email_valid;
        public Boolean is_email_verified;
        public String email;

        public class Profile{
            public String nickname;
        }
    }
}
