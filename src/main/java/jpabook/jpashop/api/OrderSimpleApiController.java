package jpabook.jpashop.api;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    /*
    ManyToOne, OneToOne에서의 성능최적화는 어떻게 하는가?
     */
    private final OrderRepository orderRepository;

    @GetMapping("api/v1/simple-orders")
    public List<Order> ordersV1(){
        // 무한루프 출력됨. 오더 멤버 오더 멤버 오더 멤버 .......
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // lazy 강제 초기화
            order.getDelivery().getAddress();
        }
        return all;
    }





}
