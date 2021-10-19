## 엔티티 설계 시 주의점
- 가급적이면 setter 사용하지 말 것
  - 변경 포인트가 너무 많아 나중에 디버깅하기 어려워짐.
- **모든 연관관계는 lazy loading으로 설정할것**
  - 예측이 어렵고 어떤 sql이 실행될지 추적하기 어려움
  - JPQL 실행시 N+1 문제 발생함.
    - `select o from order o` 날리면
    - 뭐 오더 100개를 가져오겠지. (쿼리 한 번)
    - 근데 멤버가 이거로딩으로 되어있네?
    - 그만큼 멤버를 가져오기 위해서 멤버를 조회하는 쿼리가 또 날라감. (쿼리 한 번 * 100 => 쿼리 100번)
    - 쿼리 한 번 날리려다가 101번 날라가는 것.
  - 만약 함께 조회해야 하면, fetch join 혹은 엔티티 그래프를 활용할것.
  - 항상 XToOne은 기본이 EAGER니까 싹 다 LAZY로 바꿔주자. XToMany는 기본이 LAZY라서 괜찮음.
  - 트랜잭션 실행시 레이지로딩이 문제를 일으키는 경우가 있는데, 이 또한 fetch-join을 포함해 다 대안이 있다. 절대 EAGER 쓰지 말자.
- 컬렉션은 필드에서 초기화하자.
  - OneToMany 관계에서 `머시기 = new ArrayList<>();` 식으로.
  - null 문제에서 안전하고, hibernate가 원하는 best practice에 해당함.
  - 그냥.. 조회는 하되 변경하지 않는다고 생각하는게 속편하다.
- 네이밍 컨벤션은 SpringPhysicalNamingStrategy를 따라가는게 좋다. 다만 사내용 기준이 있는 경우 적절히 override해주면 됨.
- xToOne에서 cascade는 ALL로 해주자.
  - persist를 전파해줌.
- 양방향 관계에선 연관관계 편의 메서드를 넣어주자.

## 진행하지 않을 사항
- 로그인과 권한 관리
- 카테고리, 배송정보 사용 X, 상품은 도서만 사용
- 파라미터 검증, 예외처리는 최소화 

## 개발 순서
- [x] 엔티티
- [x] 도메인
- [x] 예외
- [x] 리포지토리
- [x] 서비스
- [x] 웹
- [ ] API
- [ ] 성능 최적화

## tips
- 엔티티는 최대한 순수하게 유지해야 한다. 화면이나 api에 맞는 값은 dto를 사용하자. 
- 템플릿엔진에선 엔티티를 그대로 넘겨줘도 괜찮은데, 엔티티에선 무조건 dto를 넘겨줘야 함.
- 대부분의 성능문제는 조회에서 나온다. 제일 빈번하게 일어나기 때문.
- 프록시 관련해선 jpa북 보자.


## api 개발 시 고려사항 맛보기
- 엔티티를 그대로 반환하지 말자
- 페치 조인으로 쿼리 수를 최적화하자
- 컬렉션은 페치 조인시 페이징이 불가능한데, 이 문제를 어떻게 해결할 것인가
  - ToOne관계는 페치조인으로 쿼리 수를 최적화해주고
  - 컬렉션은 지연로딩을 유지하고, hibernate.default_batch_fetch_size, @BatchSize로 최적화해주자.
- JPA -> DTO로 직접 조회하는 경우 컬렉션 조회, 플랫 데이터 최적
- 권장 순서
  - 엔티티 조회 방식으로 우선 접근
    - ToOne관계의 경우 페치조인 사용
    - 그 외 + 페이징이 들어간다면 default_batch_fetch_size 옵션걸어 적용.
      - 100~1000 추천. 정답은 없고 WAS랑 DB 순간부하 걱정이 없다면 1000. 있다면 100.
      - 뭘로 잡든 WAS에서의 메모리 사용은 전체를 땡겨와야 하니같다고 함. 애매하면 500.
  - 엔티티 조회로 해결이 언되면 DTO 조회방식 사용
  - 위로도 해결이 안되면 NativeSQL or 스프링 JdbcTemplate
  - 위로도 해결 안되면, 캐시를 두던가 서버를 확장하던가. 거의 대부분은 페치조인으로 해결이 됨.
    - 엔티티는 직접 캐싱을 하면 안됨.
    - 무조건 DTO를 캐싱하자.
- 개발자는 성능 최적화와 코드 복잡도 사이에서 줄타기를 해야함.