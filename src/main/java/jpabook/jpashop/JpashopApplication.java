package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import jpabook.jpashop.domain.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module() {
		// 사실 어차피 엔티티를 그대로 노출할 일이 없기 때문에.ㅋ 이 방법은 그냥 이런게 있구나~ 정도로만 하고 넘어가자.
		// 강제로 지연로딩 걸려면 다음을 적용하면 됨. 다만 필요없는 것까지 다 찔러오는 문제가 있음.
		// 아니면 lazy를 eager로 바꾸거나 ㅇㅇ. 하지만 이건 성능최적화를 아예 막아버리는 방법이지.
		// 이거보단 그냥 지연로딩으로, 수동으로 필요한 것만 찌르는 게 낫다.
		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}



}
