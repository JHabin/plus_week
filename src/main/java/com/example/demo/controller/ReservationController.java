package com.example.demo.controller;

import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Status;
import com.example.demo.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// TODO: 7. 리팩토링 - (2) 컨트롤러 응답 데이터 타입 void
/**
 * void는 반환 결과를 알 수 없기 때문에 ResponseEntity를 적용하여 적절한 에러 코드와 반환 결과를 표현하는 것이 좋음.
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.ok(reservationService.createReservation(reservationRequestDto.getItemId(),
                reservationRequestDto.getUserId(),
                reservationRequestDto.getStatus(),
                reservationRequestDto.getStartAt(),
                reservationRequestDto.getEndAt()));
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<ReservationResponseDto> updateReservation(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    @GetMapping
    public List<ReservationResponseDto> findAll() {
        return reservationService.getReservations();
    }


    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponseDto>> searchAll(@RequestParam(required = false) Long userId,
                                                                  @RequestParam(required = false) Long itemId) {
        return ResponseEntity.ok(reservationService.searchAndConvertReservations(userId, itemId));
    }
}
