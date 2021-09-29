package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item", // 중간테이블
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="item_id")
    ) // 근데 실전에서 다대다를 안쓰는 이유는, 더 필드를 추가하는 경우가 불가능하기 때문. 하물며 등록날짜 등이라도.
    private List<Item> items = new ArrayList<>();

    // 대분류와 소분류 -> 자기자신으로 매핑
    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = ALL)
    private List<Category> child = new ArrayList<>();

    // == 양방향 연관관계 == //
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);

    }
}
