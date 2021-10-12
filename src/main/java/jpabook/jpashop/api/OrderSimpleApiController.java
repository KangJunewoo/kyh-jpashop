package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<OrderSimpleQueryDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderSimpleQueryDto> result = orders.stream()
                .map(o -> new OrderSimpleQueryDto(o))
                .collect(Collectors.toList());

        return result;
    }


    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<OrderSimpleQueryDto> result = orders.stream()
                .map(o -> new OrderSimpleQueryDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /*
    V4는 직접 쿼리를 짜서 dto를 조회하는 방식.
    조금 번거롭긴 해도 select *이 아닌 내가 원하는 칼럼만 가져올 수 있다는 장점이 있다.
    v3과 v4는 좀 우열을 가리기 어렵다.
    v3 : 재사용성 굳.
    v4 : 재사용성 똥. 조금 더 성능최적화 면에서 나음. api 스펙이 바뀌면 고쳐야함.
    생각보다 성능에 별로 차이는 없다.
    쿼리에서 성능먹는 부분은 from 이후부터이다.

    보통 인덱스 잘못잡혔거나 하는곳에서 성능을 갉아먹지

    v4는 엄청난 트래픽을 받는 api에만 고민해볼 가치가 있겠다.
     */
//    @GetMapping("/api/v4/simple-orders")
//    public List<OrderSimpleQueryDto> ordersV4() {
//        return orderRepository.findOrderDtos(); // 엔티티가 아닌 dto 직접 조회
//    }






}
