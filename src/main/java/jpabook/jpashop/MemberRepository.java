package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId(); // 저장 후 가급적이면, 전체 객체가 아닌 아이디를 리턴하자.
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
