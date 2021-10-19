package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        // 검색조건값들이 다 있다는 가정 하에서는 다음과 같이 정적쿼리로 나타낼 수 있음.
        // 하지만 값들이 띄엄띄엄 있다면 동적쿼리가 들어가야 되는데..
//        return em.createQuery("select o from Order o join o.member m" +
//                        " where o.status = :status " +
//                        " and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) // 최대 1000건
//                .getResultList(); // 페이징도 가능.

        // == 수레기 코드 == => 절대 이렇게 하지 말자. 코드도 길어지고 실수할 여지도 높아지고 디버깅도 어려워진다.
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        if (orderSearch.getOrderStatus() != null) {
            if (!isFirstCondition) {
                jpql = " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += "where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();


    }

    /**
     * JPA Criteria -> 요것도 권장되는 방법은 아님. 유지보수성이 0에 가까운게, 무슨 쿼리가 생성될지 가늠이 안되기 때문.
     * 표준스펙이긴 하지만, 실무에 적용하기엔 괴리가 있는 코드.
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();

    }


    // 페치조인. 사실상 join fetch. fetch라는 명령은 sql에 없다.
    // 페치조인은 jpa 책을 통해 완벽하게 이해해야 함. 실무 성능문제 90%는 여기에 있다.
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class

        ).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        // 오더아이템은 자동으로 땡겨와짐.
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
    }

    public List<Order> findAllWithItem() {
        // jpa disinct : db distinct + 스마트한 자동 엔티티 중복제거
        // jpa join fetch : db inner join + select문에 칼럼 자동 추가
        // 실무에서는 주로 이것보단 QueryDSL 많이 쓴다고 함.
        // 일대다 페치조인 단점 : 페이징 불가. 일대다에서 다가 기준이 되기 때문. 이 경우 그냥 메모리단에서 처리를 하는데 이는 메모리를 너무 많이 잡아먹을 수 있음.
        // 컬렉션 페치조인(여기선 orderItems)은 하나만 사용할 수 있음. 데이터가 부정확하게 조회될 수 있기 때문.
        return em.createQuery(
                        "select distinct o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i", Order.class)
                .getResultList();

    }
}
