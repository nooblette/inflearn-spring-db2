package hello.itemservice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity // JPA에서 사용할 객체를 입력(Item 객체는 ITEM 테이블과 매핑하여 관리하는 테이블이라는 것을 JPA에게 알려준다, 이 어노테이션이 있어야 JPA가 인식할 수 있다.)
// @Table(name = "item") // Item 객체와 매핑할 테이블 명 기입 , 두 이름이 동일한 경우 생략 가능하다.
public class Item {

    @Id // PK 컬럼 명시
    @GeneratedValue( // id 컬럼의 값은 JPA에서 생성한다
        strategy = GenerationType.IDENTITY // 이 떄 생성 전략을 IDENTITY(DB에서 값을 증가(e.g. auto increment))로 세팅한다.
    )
    private Long id;

    @Column(
        // name = "item_name", // 스프링 부트와 JPA를 통합하여 사용하면 카멜케이스와 언더스코에 변환은 자동으로 이루어진다.
        length = 10
    )
    private String itemName;

    // @Column(name = "price") // 필드명과 테이블 컬럼명이 동일하면 @Column 어노테이션은 생략 가능하다.
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
