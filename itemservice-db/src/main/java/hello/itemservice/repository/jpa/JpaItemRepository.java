package hello.itemservice.repository.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
public class JpaItemRepository implements ItemRepository {

	private final EntityManager entityManager; // EntityManager : JPA의 모든 동작은 이 엔티티 매니저를 통해서 이루어진다.
	@Override
	public Item save(Item item) {
		// persist() : Item 객체의 매핑 정보를 통해서 INSERT SQL을 DB에 날리고 데이터를 저장한다, 이 때 @GeneratedValue를 보고 ID 값도 생성해서 넣어준다.
		entityManager.persist(item);
		return item;
	}

	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		Item findItem = entityManager.find(Item.class, itemId);

		// 트랜잭션이 커밋되는 시점에 엔티티에서 아래 변경한 값들에 대해서 UPDATE SQL을 생성하고 데이터베이스에 날려서 수정사항을 반영한다.
		// 개발자는 java 컬렉션(Collection)처럼 엔티티(Entity)를 사용하면 된다
		findItem.setItemName(updateParam.getItemName());
		findItem.setPrice(updateParam.getPrice());
		findItem.setQuantity(updateParam.getQuantity());
	}

	@Override
	public Optional<Item> findById(Long id) {
		Item item = entityManager.find(Item.class, id);
		return Optional.ofNullable(item);
	}

	@Override
	public List<Item> findAll(ItemSearchCond cond) {
		// 복잡한 조건으로 JPA를 사용하는 경우 JPQL을 작성하여 사용한다.
		// JPQL : 객체 쿼리 언어, SQL과 거의 유사하나 테이블 대상이 아닌 엔티티를 대상으로 쿼리를 수행한다고 생각하면 된다.
		String jpql = "select i from Item i";

		// JPA를 단독으로만 사용하는 경우 동적쿼리 작성이 어렵다.
		Integer maxPrice = cond.getMaxPrice();
		String itemName = cond.getItemName();
		if (StringUtils.hasText(itemName) || maxPrice != null) {
			jpql += " where";
		}
		boolean andFlag = false;
		if (StringUtils.hasText(itemName)) {
			jpql += " i.itemName like concat('%',:itemName,'%')";
			andFlag = true;
		}
		if (maxPrice != null) {
			if (andFlag) {
				jpql += " and";
			}
			jpql += " i.price <= :maxPrice";
		}
		log.info("jpql={}", jpql);
		TypedQuery<Item> query = entityManager.createQuery(jpql, Item.class);
		if (StringUtils.hasText(itemName)) {
			query.setParameter("itemName", itemName);
		}
		if (maxPrice != null) {
			query.setParameter("maxPrice", maxPrice);
		}

		return query.getResultList();
	}
}
