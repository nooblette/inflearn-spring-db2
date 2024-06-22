package hello.itemservice.repository.jpa;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Transactional // JPA에서 일어나는 모든 데이터 변경은 트랜잭션(Transaction) 내부에서 발생한다. 따라서 꼭 @Transactional 어노테이션을 넣어준다.
@RequiredArgsConstructor
public class JpaItemRepositoryV2 implements ItemRepository {
	private final SpringDataJpaItemRepository repository;

	@Override
	public Item save(Item item) {
		return repository.save(item);
	}

	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		Item findItem = repository.findById(itemId).orElseThrow();

		// JPA에서 update는 쿼리를 날리거나 어떤 메서드를 호출하지 않고, 엔티티(Entity) 객체를 변경(setter 호출)하기만 하면 된다.
		findItem.setItemName(updateParam.getItemName());
		findItem.setPrice(updateParam.getPrice());
		findItem.setQuantity(updateParam.getQuantity());
	}

	@Override
	public Optional<Item> findById(Long id) {
		return repository.findById(id);
	}

	@Override
	public List<Item> findAll(ItemSearchCond cond) {

		String itemName = cond.getItemName();
		Integer maxPrice = cond.getMaxPrice();

		// 실무에서는 동적 쿼리와 Querydsl을 사용한다. (조건 2개정도만 있다면 분기 처리를 하겠지만) 이렇게 지저분한 방식으로 구현하지 않는다.
		if (StringUtils.hasText(itemName) && maxPrice != null) {
			// 쿼리 메서드 호출 방식
			//return repository.findByItemNameLikeAndPriceLessThanEqual("%" + itemName +"%", maxPrice);

			// @Query 어노테이션으로 구현한 메서드 호출 방식
			return repository.findItems("%" + itemName + "%", maxPrice);
		} else if (StringUtils.hasText(itemName)) {
			return repository.findByItemNameLike("%" + itemName + "%");
		} else if (maxPrice != null) {
			return repository.findByPriceLessThanEqual(maxPrice);
		} else {
			return repository.findAll();
		}
	}
}
