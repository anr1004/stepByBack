package com.stepby.shop_backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String realName;

    // 생년월일은 패턴 검증으로 형식 유효성 확인 (예: yyyy-MM-dd)
    @NotBlank(message = "생년월일은 필수 입력값입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일 형식이 올바르지 않습니다 (yyyy-MM-dd).")
    private String birthDate; // 프론트에서 넘어오는 문자열 그대로 받거나 LocalDate 타입으로 설정 가능

    @NotBlank(message = "성별은 필수 입력값입니다.")
    @Pattern(regexp = "남성|여성|선택안함|male|female|UNKNOWN", message = "성별은 '남성', '여성' 중 하나여야 합니다.") // enum이나 다른 방법으로 관리하면 더 좋음
    private String gender;

    @NotBlank(message = "휴대폰 번호는 필수 입력값입니다.")
    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotBlank(message = "우편번호는 필수 입력값입니다.")
    private String zonecode;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;

    @NotBlank(message = "상세 주소는 필수 입력값입니다.")
    private String detailAddress;
}
