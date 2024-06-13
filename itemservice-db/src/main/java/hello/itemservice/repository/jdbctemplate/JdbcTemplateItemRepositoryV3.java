package hello.itemservice.repository.jdbctemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;

/***
 * SimpleJdbInsert 사용
 * */
@Slf4j
public class JdbcTemplateItemRepositoryV3 implements ItemRepository {
	private final SimpleJdbcInsert simpleJdbcInsert;
	// 쿼리의 파라미터 바인딩을 순서가 아닌 이름으로 수행
	private final NamedParameterJdbcTemplate template;

	public JdbcTemplateItemRepositoryV3(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);

		this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
			.withTableName("item")
			.usingGeneratedKeyColumns("id");
			// 데이터베이스 메타데이터를 통해 테이블 이름만 알면 어떤 컬럼이 있는지 자동으로 알 수 있어 생략 가능하다.
			//.usingColumns("item_name", "price", "quantity"); // 생략 가능
	}

	@Override
	public Item save(Item item) {
		// 컬럼에 매칭되는 값(value)를 Item 객체로 넘겨준다.
		SqlParameterSource param = new BeanPropertySqlParameterSource(item);
		Number key = simpleJdbcInsert.executeAndReturnKey(param);
		item.setId(key.longValue());
		return item;
	}

	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		String sql = "update item " +
			"set item_name = :itemName, price = :price, quantity = :quantity " +
			"where id = :itemId";

		// MapSqlParameterSource : 이름 기반의 파라미터 생성, 값들을 기반으로 파라미터를 생성한다.
		MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("itemName", updateParam.getItemName())
			.addValue("price", updateParam.getPrice())
			.addValue("quantity", updateParam.getQuantity())
			.addValue("itemId", itemId);

		template.update(sql, params);
	}

	@Override
	public Optional<Item> findById(Long id) {
		String sql = "select id, item_name, price, quantity from item where id = :id";

		try {
			// Map 클래스로 이름 기반의 파라미터를 생성하는 방법
			Map<String, Long> params = Map.of("id", id);
			Item item = template.queryForObject(sql, params, itemRowMapper());

			// of : 타깃 객체가 반드시 값이 있어야한다. / ofNullable : 타깃 객체가 null 일 수 있다.
			return Optional.of(item);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<Item> findAll(ItemSearchCond cond) {
		String itemName = cond.getItemName();
		Integer maxPrice = cond.getMaxPrice();

		// BeanPropertySqlParameterSource : 이름 기반의 파라미터 생성, 전달받은 cond 객체를 기반으로 파라미터를 생성한다.
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(cond);

		String sql = "select id, item_name, price, quantity from item"; //동적 쿼리
		if (StringUtils.hasText(itemName) || maxPrice != null) {
			sql += " where";
		}
		boolean andFlag = false;
		if (StringUtils.hasText(itemName)) {
			sql += " item_name like concat('%',:itemName,'%')";
			andFlag = true;
		}
		if (maxPrice != null) {
			if (andFlag) {
				sql += " and";
			}
			sql += " price <= :maxPrice";
		}

		log.info("sql={}", sql);

		// query : 쿼리 결과를 리스트로 조회한다.
		return template.query(sql, params, itemRowMapper());
	}

	private RowMapper<Item> itemRowMapper() {
		// ResultSet에 담겨있는 조회 row들은 Item 클래스로 매핑해준다.
		return BeanPropertyRowMapper.newInstance(Item.class);
	}
}
