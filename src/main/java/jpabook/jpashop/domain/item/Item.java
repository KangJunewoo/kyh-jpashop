package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 싱글테이블 : 한테이블에 때려박기, 테이블퍼클래스 : 세개의 테이블만, 조인드 : 정규화
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //    @JoinTable(name="cateogry_item") 다대다에서 joinTable name만 잡아주지 않는 이유 알겠지?
    @ManyToMany(mappedBy="items")
    private List<Category> categories = new ArrayList<Category>();


}
