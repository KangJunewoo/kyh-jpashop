package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch=LAZY, mappedBy = "delivery") // 연관관계의 주인 잘 따져야함. 보통 오더로 딜리버리를 조회하니 딜리버리를 연관관계의 주인으로 하자.
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // 오디널 vs 스트링. 왜 스트링 써야하는진 알지?
    private DeliveryStatus status; // READY, COMP

}
