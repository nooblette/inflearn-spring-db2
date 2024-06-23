package hello.itemservice.repository.jpa;

import static hello.itemservice.domain.QItem.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;

@Repository
@Transactional // JPA를 사용하는 계층에선 반드시 @Transactional 어노테이션을 추가한다.(보통 서비스 계층에 추가하지만, 예제에선 코드 설명을 위해 리포지토리 계층에 추가)
public class JpaItemRepositoryV3 implements ItemRepository {
	private final EntityManager	entityManager;
	private final JPAQueryFactory queryFactory;

	public JpaItemRepositoryV3(EntityManager entityManager) {
		this.entityManager = entityManager;

		// JPAQueryFactory : Querydsl에서 제공하는 클래스, Querydsl은 JPAQueryFactory 통해서 JPQL을 생성한다.
		// Querydsl : JPA가 사용할 JPQL을 생성하는 빌더 역할을 하는 프레임워크
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public Item save(Item item) {
		// 저장은 JPA를 사용한다.
		entityManager.persist(item);
		return item;
	}

	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		// 업데이트도 JPA를 사용한다.
		Item findItem = findById(itemId).orElseThrow();
		findItem.setItemName(updateParam.getItemName());
		findItem.setPrice(updateParam.getPrice());
		findItem.setQuantity(updateParam.getQuantity());
	}

	@Override
	public Optional<Item> findById(Long id) {
		// PK를 조건으로 단건 조회(find One)를 할때도 JPA를 사용한다.
		Item item = entityManager.find(Item.class, id);
		return Optional.ofNullable(item);
	}

	public List<Item> findAllOld(ItemSearchCond cond) {
		// 다중 조회를 위해 Querydsl 사용, SQL과 유사하게 작성할 수 있다.
		String itemName = cond.getItemName();
		Integer maxPrice = cond.getMaxPrice();

		// 동적 쿼리 조건, Querydsl Builder로 작성한다.
		BooleanBuilder builder = new BooleanBuilder();
		if(StringUtils.hasText(itemName)){
			builder.and(item.itemName.like("%" + itemName + "%"));
		}
		if(maxPrice != null) {
			builder.and(item.price.loe(maxPrice));
		}

		List<Item> result = queryFactory
			.select(item)
			.from(item)
			.where(builder) // 조회 조건 (동적 쿼리를 간단하게 작성할 수 있다.)
			.fetch(); // fetch : 리스트 반환

		return result;
	}

	@Override
	public List<Item> findAll(ItemSearchCond cond) {
		String itemName = cond.getItemName();
		Integer maxPrice = cond.getMaxPrice();
		return queryFactory
			.select(item)
			.from(item)
			.where(likeItemName(itemName), maxPrice(maxPrice))
			.fetch();
	}

	private BooleanExpression likeItemName(String itemName) {
		if(StringUtils.hasText(itemName)){
			return item.itemName.like("%" + itemName + "%");
		}

		return null;
	}

	private BooleanExpression maxPrice(Integer maxPrice) {
		if (maxPrice != null) {
			return item.price.loe(maxPrice);
		}
		return null;
	}
}
