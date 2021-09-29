package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 내장 값타입. 생성 시점에 모든 값이 fix되어야 함.
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() { // 내부적으로 프록시 등에서 사용됨. jpa 스펙상 만든거고 손대지 말자. 상속할일 X
    }
}
