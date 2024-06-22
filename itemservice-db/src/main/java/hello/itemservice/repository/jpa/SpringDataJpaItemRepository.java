package hello.itemservice.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hello.itemservice.domain.Item;

public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {
	// JpaRepository 인터페이스가 제공하는 기본적인 CRUD 기능(e.g. findAll())들은 모두 따로 구현하지 않고 바로 사용할 수 있다.

	List<Item> findByItemNameLike(String itemName);

	List<Item> findByPriceLessThanEqual(Integer price);

	// 쿼리 메서드 - 메서드명이 너무 길어진다. (findItems 메서드와 동일한 기능 수행)
	List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer Price);

	// 쿼리 직접 실행 - 메서드 명을 간략하게 작성하고 JPQL을 개발자가 직접 작성한다.
	@Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
	List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);
}
