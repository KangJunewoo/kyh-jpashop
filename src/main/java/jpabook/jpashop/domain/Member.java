package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;

    @Embedded // 내장타입
    private Address address;

    @OneToMany(mappedBy = "member", cascade = ALL) // 나는 연관관계의 주인이 아니다!! 읽기 전용 선언. 값을 넣는다고 FK가 변경되지 않는다.
    private List<Order> orders = new ArrayList<>();


}
