package hello.itemservice.repository.mybatis;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisItemRepository implements ItemRepository {
	// MyBatis Spring 모듈이 생성하고 스프링 빈으로 올린 ItemMapper 인터페이스의 구현체를 의존 관게 주입받는다.
	private final ItemMapper itemMapper;

	@Override
	public Item save(Item item) {
		log.info("itemMapper class={}", itemMapper.getClass());
		itemMapper.save(item);
		return item;
	}

	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		itemMapper.update(itemId, updateParam);
	}

	@Override
	public Optional<Item> findById(Long id) {
		return itemMapper.findById(id);
	}

	@Override
	public List<Item> findAll(ItemSearchCond cond) {
		return itemMapper.findAll(cond);
	}
}
