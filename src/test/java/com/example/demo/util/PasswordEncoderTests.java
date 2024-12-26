package com.example.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
// TODO: 8. 테스트코드 - PasswordEncoder Test
public class PasswordEncoderTests {

    @Test   // 테스트 메서드
    void testEncodePassword() {
        String rawPassword = "test123!@";
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        // 암호화된 비밀번호가 null인지 확인
        assertNotNull(encodedPassword, "암호화된 비밀번호는 null이 될 수 없습니다.");
        // 암호화된 비밀번호가 평문 비밀번호와 다른지 확인
        assertNotEquals(rawPassword, encodedPassword, "암호화된 비밀번호는 평문 비밀번호와 같을 수 없습니다.");
    }

    @Test
    void testEncodedPasswordMatchesRawPassword() {
        String rawPassword = "sdjgijstj!#$@3253";
        String encodedPassword = PasswordEncoder.encode(rawPassword);
        //평문 비밀번호와 암호화된 비밀번호가 정확하게 매칭되는지 확인 (제대로 암호화되었는지를 검증)
        assertTrue(PasswordEncoder.matches(rawPassword, encodedPassword),
                "평문 비밀번호는 암호화된 비밀번호와 matches로 비교했을 때 True를 리턴해야 합니다.");
    }

    @Test
    void testEncodedPasswordsAreDifferent() {
        String rawPassword = "sdjgijs_-~tj!#$@3253";
        String encodedPassword1 = PasswordEncoder.encode(rawPassword);
        String encodedPassword2 = PasswordEncoder.encode(rawPassword);
        // 같은 원본 비밀번호라도 매번 다른 결과를 생성해야 함.
        assertNotEquals(encodedPassword1, encodedPassword2, "암호화된 비밀번호는 서로 달라야 합니다.");
    }

    @Test
    void testPasswordDoesNotMatch() {
        String rawPassword = "asdfzxcv09832@";
        String wrongPassword = "SkDoClGm3425";

        String encodedPassword = PasswordEncoder.encode(rawPassword);
        // 암호화된 비밀번호는 원본 비밀번호와만 일치해야 함
        assertFalse(PasswordEncoder.matches(wrongPassword, encodedPassword),
                "wrongPassword는 encodedPassword와 matches로 비교했을 때 False를 리턴해야 합니다.");
    }
}
