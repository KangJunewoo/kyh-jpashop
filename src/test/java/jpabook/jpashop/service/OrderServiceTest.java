package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/*
사실 좋은 테스트라고 할 순 없다.
순수하게 메서드를 단위테스트부터 해나가는게 좋음.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        // given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);
        assertThat(OrderStatus.ORDER).isEqualTo(getOrder.getStatus()); // 주문상태
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1); // 주문상품종류 수
        assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount); // 주문가격
        assertThat(book.getStockQuantity()).isEqualTo(8); // 재고 정상감소


    }


    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = createMember();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);


        // when
        orderService.cancelOrder(orderId);


        // then
        Order getOrder = orderRepository.findOne(orderId);
        assertThat(OrderStatus.CANCEL).isEqualTo(getOrder.getStatus());
        assertThat(10).isEqualTo(item.getStockQuantity());

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        // given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        // when
        try {
            orderService.order(member.getId(), item.getId(), orderCount);
        } catch (NotEnoughStockException e) {
            return;
        }


        // then
        fail("재고수량부족예외가 발생해야 한다.");

    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);
        return member;
    }
}