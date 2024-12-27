package com.example.demo.entity;

import com.example.demo.config.JPAConfiguration;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO: 8. 테스트코드 - Item Status Test
@DataJpaTest    // JPA와 관련된 Repository, EntityManager만 로드하여 테스트를 실행
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    // 내장된 DB가 아니라 실제 DB 사용하도록 설정
@Import(JPAConfiguration.class)   //  QueryDSL 관련 설정 import
public class ItemTests {
    @Autowired
    private ItemRepository itemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    //Item 엔티티의 status 필드가 NULL일 경우 예외 발생하는지 확인
    @Test
    void testStatusCannotBeNull() {
        // User 정보 hard coding으로 DB에 저장
        User owner = new User("admin", "asdf@naver.com", "OwnerUserNickname", "testpass");
        User manager = new User("user", "asdfuser@naver.com", "ManagerNickname", "testpass");
        userRepository.save(owner);
        userRepository.save(manager);


        assertThrows(PersistenceException.class, () -> {
            // EntityManager로 직접 SQL 쿼리 생성, status에 null을 넣고 실행
            entityManager.createNativeQuery(
                    "INSERT INTO item (name, description, owner_id, manager_id, status) VALUES ('ItemName', 'ItemDescription', 1, 2, NULL)"
            ).executeUpdate();
        });

    }
}
