package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.RentalLog;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;

    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
    }

    // TODO: 1. 트랜잭션 이해

    /**
     * 정의 : 프록시 객채라는 가상의 객체를 생성해서 클래스/메서드 작업을 수행하고 하나라도 예외가 발생하면 rollback, 없을 경우 commit
     * 용도 : 회원가입, 로그인, 수정, 삭제 등 데이터가 변경되는 메서드나 클래스에 사용
     * 조회용일 때에는 readonly = true 표기
     * @Transactional 없이 실행할 경우, reservation save와 rentallog save가 묶여 있지 않기 때문에
     * reservation save는 작동되지만 rentalLog에 save는 되지 않음.
     * @Transactional 있을 경우, 두 메서드가 묶여 있어서 하나의 메서드에 문제 발생 시 함께 rollback 됨.
     */
    @Transactional
    public void createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
        if(!haveReservations.isEmpty()) {
            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        Reservation reservation = new Reservation(item, user, "PENDING", startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog);
    }

    // TODO: 3. N+1 문제

    /**
     * n+1개의 쿼리 : 1(처음에 원하는 대상 조회하는 쿼리) + n(그 대상과 연관된 데이터를 가져오기 위해 각 인스턴스마다 쿼리 발생)
     * repository에 findAllWithUserAndItem() 메서드를 생성해서 reservation에 연관된 User와 item을 fetch join을 통해 조회
     */
    public List<ReservationResponseDto> getReservations() {
        List<Reservation> reservations = reservationRepository.findAllWithUserAndItem();

        return reservations.stream().map(reservation -> {
            User user = reservation.getUser();
            Item item = reservation.getItem();

            return new ReservationResponseDto(
                    reservation.getId(),
                    user.getNickname(),
                    item.getName(),
                    reservation.getStartAt(),
                    reservation.getEndAt()
            );
        }).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    /**
     * 동적 쿼리 : 입력값에 따라 조건에 의해 쿼리가 자동으로 달라지는 것
     * 목적 : 각 조건마다 다른 메서드 호출하는 코드 중복을 제거하기 위함. 만약 파라미터가 추가될 경우 조건이 너무 많아져서 발생하는 확장성 문제를 해결하기 위함.
     * 1. JPAConfiguration 클래스 생성 - JPAQueryFactory 에 entityManager 를 주입해서 Bean 으로 등록
     * 2. 책임 분리를 위해 JPAQueryFactory를 적용하는 Repository를 추가하여 그 안에 QueryDSL을 적용할 메서드(searchReservations)를 구현
     * 3. searchReservations 메서드를 JPAQueryFactory의 기능을 적용하여 쿼리 전용 클래스(Q클래스)로 재구성함.
     */
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {

        List<Reservation> reservations = reservationRepository.searchReservations(userId, itemId);

        return convertToDto(reservations);
    }

//    public List<Reservation> searchReservations(Long userId, Long itemId) {
//
//        if (userId != null && itemId != null) {
//            return reservationRepository.findByUserIdAndItemId(userId, itemId);
//        } else if (userId != null) {
//            return reservationRepository.findByUserId(userId);
//        } else if (itemId != null) {
//            return reservationRepository.findByItemId(itemId);
//        } else {
//            return reservationRepository.findAll();
//        }
//    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    /**
     * 1. updateStatus 의 if-else 과다하게 사용 중
     * updateStatus의 중복은 각 조건문에 다 동일하게 있기 때문에 조건문 뒤에 하나만 작성해줘도 됨.
     * if 내부의 If 문은 결국 두 조건을 만족할 경우라는 의미이므로 &&로 합쳐주는 게 가독성에 좋음
     * else문은 굳이 필요하지 않다면 없애는 게 좋음
     */
    @Transactional
    public void updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 데이터가 존재하지 않습니다."));

        if ("APPROVED".equals(status) && !Status.PENDING.equals(reservationStatus)) {
            throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");

            //reservation.updateStatus("APPROVED");
        }
        if ("BLOCKED".equals(status) && Status.EXPIRED.equals(reservationStatus)) {
            throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");

            //reservation.updateStatus("CANCELED");
        }
        if ("EXPIRED".equals(status) && !Status.PENDING.equals(reservationStatus)) {
            throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");

            //reservation.updateStatus("EXPIRED");
        }
//        else {
//            throw new IllegalArgumentException("올바르지 않은 상태: " + status);
//        }
        reservation.updateStatus(status);
    }
}
