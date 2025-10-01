package com.groupgrow.groupgrow.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.groupgrow.groupgrow.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
