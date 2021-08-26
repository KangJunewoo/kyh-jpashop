package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    
    @Test
    @Transactional // TODO: 왜 트랜잭셔널을 써야 하는거지? + 테스트에 이거 붙어있으면 끝나고도 롤백해버린다고 함.
    @Rollback(false) // 그럼에도 불구하고 롤백을 끄고싶다면!
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        /**
         * 이거 같게 나온다.
         * 기본편을 충실히 공부했다면 알 수 있다고 함.
         * 같은 부분이면 영속성 컨텍스트가 같기..때문. 1차캐시..
         * 아 다시 기본편 봐야겠다.
         * findMember == member
         */
        Assertions.assertThat(findMember).isEqualTo(member);

    }

}