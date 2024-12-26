package com.example.demo.repository;

import com.example.demo.entity.QItem;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.QUser;
import com.example.demo.entity.Reservation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReservationRepositoryQueryImpl implements ReservationRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Reservation> searchReservations(Long userId, Long itemId) {
        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QItem item = QItem.item;

        List<Reservation> reservations = jpaQueryFactory
                .selectFrom(reservation)
                .join(reservation.user, user).fetchJoin()
                .join(reservation.item, item).fetchJoin()
                .where(
                        eqUserId(userId),
                        eqItemId(itemId)
                )
                .fetch();

        return reservations;
    }

    private BooleanExpression eqUserId(Long userId) {
        return userId != null ? QReservation.reservation.user.id.eq(userId) : null;
    }

    private BooleanExpression eqItemId(Long itemId) {
        return itemId != null ? QReservation.reservation.item.id.eq(itemId) : null;
    }
}
