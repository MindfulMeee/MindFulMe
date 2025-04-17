package com.mindful.domain.user.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("유효한 이메일 형식이면 Email 객체가 생성된다")
    void createEmailWithValidFormat() {

        // given
        String validEmailStr = "user@example.com";

        // when
        Email email = new Email(validEmailStr);

        // then
        assertThat(email.getValue()).isEqualTo(validEmailStr);
    }

    @Test
    @DisplayName("이메일 형식이 유효하지 않으면 예외가 발생한다")
    void throwExceptionForInvalidEmailFormat() {
        // given 잘못된 이메일 형식 전달
        String invalidEmailStr = "invalid-email";

        //when & then
        assertThatThrownBy(() -> new Email(invalidEmailStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 이메일 형식입니다");
    }

    @Test
    @DisplayName("이메일 값이 null이면 예외가 발생한다")
    void throwExceptionForNullEmail() {
        // given
        String nullEmail = null;

        // when & then
        assertThatThrownBy(() -> new Email(nullEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일은 필수 값입니다");
    }

    @Test
    @DisplayName("동일한 값을 가진 Email 객체는 동등하다")
    void emailEquality() {
        // given
        Email email1 = new Email("same@example.com");
        Email email2 = new Email("same@example.com");

        // when & then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());

    }
}