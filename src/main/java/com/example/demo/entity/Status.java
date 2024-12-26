package com.example.demo.entity;

import lombok.Getter;

// TODO: 7. 리팩토링 - (4) status Enum 구현
/**
 * enum을 사용하여 코드 가독성과 타입 안정성 보장
 * 기존의 String status 를 모두 Status status로 수정
 */
@Getter
public enum Status {
    PENDING,
    APPROVED,
    BLOCKED,
    EXPIRED
}
