package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: 4. find or save 예제 개선
    /**
     * DB 접근 최소화 - for문으로 사용자 하나씩 접근하면 불필요한 DB 접근이 너무 많아짐
     *
     */
    @Transactional
    public void reportUsers(List<Long> userIds) {
        //for문으로 list의 각 원소 findById로 접근하는 코드 -> findAllById 한번에 처리하는 코드
        List<User> users = userRepository.findAllById(userIds);

        if (users.isEmpty()){
            throw new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다.");
        }
        // status를 한 번에 "APPROVED" -> "PENDING"으로 변경해주는 메서드
        userRepository.updatePendingStatus(userIds);
    }
}
