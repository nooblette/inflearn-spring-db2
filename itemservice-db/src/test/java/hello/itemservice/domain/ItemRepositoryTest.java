package hello.itemservice.domain;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import hello.itemservice.repository.memory.MemoryItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// @Commit // 테스트 이후 트랜잭션 커밋
@Transactional
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    /**
     * @Transactional 어노테이션으로 아래 코드를 대체한다.
     */
    // /**
    //  * 트랜잭션을 사용하기 위해 PlatformTransactionManager 의존관계 추가
    //  * 스프링 부트가 application.properties에 작성된 DB 설정 정보를 보고 자동으로 생성하고 주입해준다.
    //  */
    // @Autowired
    // PlatformTransactionManager transactionManager;
    // TransactionStatus transactionStatus; // 테스트 코드의 트랜잭션 상태 관리를 위함
    //
    // @BeforeEach
    // void beforeEach() {
    //     // 각각의 테스트 케이스 실행 전에 트랜잭션을 연다.
    //     transactionStatus = transactionManager.getTransaction(new DefaultTransactionAttribute());
    // }

    @AfterEach
    void afterEach() {
        // 메모리에 데이터를 저장하는 경우
        if (itemRepository instanceof MemoryItemRepository) {
            ((MemoryItemRepository) itemRepository).clearStore();
        }

        /**
         * @Transactional 어노테이션으로 아래 코드를 대체한다.
         */
        // 각각의 테스트 케이스 종료 후 트랜잭션 롤백, 테스트 케이스에서 사용한 데이터가 실제 DB에 저장되지 않는다.
        // transactionManager.rollback(transactionStatus);
    }

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();

        //when
        ItemUpdateDto updateParam = new ItemUpdateDto("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);

        //then
        Item findItem = itemRepository.findById(itemId).get();
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    @Test
    void findItems() {
        //given
        Item item1 = new Item("itemA-1", 10000, 10);
        Item item2 = new Item("itemA-2", 20000, 20);
        Item item3 = new Item("itemB-1", 30000, 30);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        //둘 다 없음 검증
        test(null, null, item1, item2, item3);
        test("", null, item1, item2, item3);

        //itemName 검증
        test("itemA", null, item1, item2);
        test("temA", null, item1, item2);
        test("itemB", null, item3);

        //maxPrice 검증
        test(null, 10000, item1);

        //둘 다 있음 검증
        test("itemA", 10000, item1);
    }

    // itemName과 maxPrice로 조회했을때 items가 결과로 조회되어야한다.
    void test(String itemName, Integer maxPrice, Item... items) {
        List<Item> result = itemRepository.findAll(new ItemSearchCond(itemName, maxPrice));

        // containsExactly() : 순서까지 동일해야 한다.
        assertThat(result).containsExactly(items);
    }
}
