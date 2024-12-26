package com.example.demo.repository;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // TODO: 7. 리팩토링 - (3) findById가 중복되어 사용 중
    default Item findItemById(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
    }
}
