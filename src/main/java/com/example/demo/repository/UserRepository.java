package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    // @Modifying 어노테이션으로 JPA가 SELECT 쿼리가 아닌 UPDATE 쿼리임을 인식
    @Modifying
    @Query("UPDATE User u SET u.status = 'PENDING' WHERE u.status = 'APPROVED' AND u.id IN :userIds")
    void updatePendingStatus(@Param("userIds") List<Long> userIds);
}
