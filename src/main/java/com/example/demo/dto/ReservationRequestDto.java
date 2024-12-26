package com.example.demo.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationRequestDto {
    private Long itemId;
    private Long userId;
    private String status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
