package jpabook.jpashop.api;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    /*
    ManyToOne, OneToOne에서의 성능최적화는 어떻게 하는가?

    사실 v2가 이상적인 것처럼 보이지만,
    lazy loading으로 인해

    order에서 sql 한번으로 인해 두 개의 주문이 나온다.
    그리고 stream()되어있는걸로 루프가 돌겠지.
    첫 번째 멤버를 찾고, 그에 해당하는 딜리버리를 찾는다.
    두 번째 멤버를 찾고, 그에 해당하는 딜리버리를 찾는다.
    ...

    N+1 문제인 것이다. (사실상 1+N이 더 이해하기 편함)
    쿼리 한번으로 될거 여러번으로 바뀌어짐. (이 경우엔 5번)
    이미 찌른건 영속성 컨텍스트에 남아있기 떄문에 더 줄어들 여지는 있다만
    최악의 경우를 상정하는게 맞다. 좋은 설계는 아님.


     */
    private final OrderRepository orderRepository;

    @GetMapping("api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 무한루프 출력됨. 오더 멤버 오더 멤버 오더 멤버 .......
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // lazy 강제 초기화
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }



    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화

        }
    }
}
