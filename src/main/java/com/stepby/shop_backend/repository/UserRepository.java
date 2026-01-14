package com.stepby.shop_backend.repository;

import com.stepby.shop_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 찾기
    boolean existsByEmail(String email); // 이메일 존재 여부 확인

}
