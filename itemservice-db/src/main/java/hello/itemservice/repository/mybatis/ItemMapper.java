package hello.itemservice.repository.mybatis;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;

@Mapper // MyBatis Spring 모듈에서 @Mapper 어노테이션을 인식하고 ItemMapper 구현체를 만들어서 스프링 빈으로 등록한다.
public interface ItemMapper {
	void save(Item item);

	// 파라미터가 2개 이상 넘어가는 경우 @Param 어노테이션으로 파라미터의 이름을 지정해주어야한다.
	void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto itemUpdateDto);

	Optional<Item> findById(Long id);

	List<Item> findAll(ItemSearchCond itemSearchCond);
}
