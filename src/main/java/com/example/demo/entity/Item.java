package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;


@Entity
@Getter
// TODO: 6. Dynamic Insert
/**
 * Dynamic Insert : INSERT 쿼리를 실행할 때, 내가 입력하는 필드(컬럼) 데이터만을 가지고 동적으로 SQL을 생성하는 방식
 * 즉, 값이 없는 필드(컬럼)는 INSERT 쿼리에서 제외되어 들어감.
 * 이 코드에서는 status 데이터가 안 들어가는데 이 status는 SQL 생성에서 제외됨.
 */
@DynamicInsert
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(nullable = false, columnDefinition = "varchar(20) default 'PENDING'")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Status status;

    public Item(String name, String description, User manager, User owner) {
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.owner = owner;
    }

    public Item() {}
}
