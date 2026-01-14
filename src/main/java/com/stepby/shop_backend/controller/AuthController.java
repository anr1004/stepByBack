package com.stepby.shop_backend.controller;

import com.stepby.shop_backend.dto.auth.AuthResponse;
import com.stepby.shop_backend.dto.auth.LoginRequest;
import com.stepby.shop_backend.dto.auth.RegisterRequest;
import com.stepby.shop_backend.entity.Gender;
import com.stepby.shop_backend.entity.Role;
import com.stepby.shop_backend.entity.User;
import com.stepby.shop_backend.repository.UserRepository;
import com.stepby.shop_backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    //회원가입 API
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if ( userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: 이메일이 이미 사용 중입니다!");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        user.setRealName(registerRequest.getRealName());
        user.setBirthDate(LocalDate.parse(registerRequest.getBirthDate()));
        try{
            user.setGender(Gender.valueOf(registerRequest.getGender().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: 유효하지 않은 성별 값입니다.");
        }
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setZonecode(registerRequest.getZonecode());
        user.setAddress(registerRequest.getAddress());
        user.setDetailAddress(registerRequest.getDetailAddress());

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다.");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //JWT 토큰 생성
        String jwt = jwtUtil.generateJwtToken(authentication);

        //인증된 사용자 정보 가져오기
        User userDetails = (User) authentication.getPrincipal();

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwt)
                .userId(userDetails.getId())
                .email(userDetails.getEmail())
                .role(userDetails.getRole().name())
                .build());
    }
}
