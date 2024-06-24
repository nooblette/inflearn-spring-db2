package hello.itemservice.repository.v2;

import static hello.itemservice.domain.QItem.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;

@Repository
public class ItemQueryRepositoryV2{
	private final JPAQueryFactory query;

	public ItemQueryRepositoryV2(EntityManager entityManager) {
		this.query = new JPAQueryFactory(entityManager);
	}

	public List<Item> finaAll(ItemSearchCond cond){
		return query.select(item)
			.from(item)
			.where(
				likeItemName(cond.getItemName()),
				maxPrice(cond.getMaxPrice()))
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
