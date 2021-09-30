package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 각 테이블이 중요하다면 테이블을 나누어 persist를 여러번 하고
        // 별로 그렇지 않다면 cascade 해도 된다.

        /*
        createOrderItem, createOrder가 사실상 메인임.
        이렇듯 서비스 몰빵이 아닌 엔티티에서 핵심 비즈니스 로직을 처리하는 것을 도메인 모델 패턴이라고 함.
        그 반대로 서비스 계층에 모든 로직을 넣어놓는 것을 트랜잭션 스크립트 패턴이라고 함.
        상황에 맞게 잘 패턴을 선택하자. 한 프로젝트 안에서도 두 패턴이 양립함.
         */
        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // new OrderItem(); 한 뒤 setCount() 이런식으로 생성하는 것을 막아야함. 생성자를 protected로 놓으면 되겠지.

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem); // ... 붙였던거 기억나지? 여러 개 주문도 가능.

        // 주문 저장
        orderRepository.save(order); // cascade 옵션 덕분에 orderItem, order 모두 persist된다.

        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소 -> 도메인에 만들어놓았었지!
        // SQL을 직접 다룰땐 서비스계층이 거대해질수밖에 없음. jpa를 쓸 땐 더티체킹(변경감지) 후 알아서 촥촥 반영됨.
        order.cancel();
    }

    // 검색=
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }


}
