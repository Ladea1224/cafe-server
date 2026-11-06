package com.example.cafediary.cafe;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CafeRepository {
	private static final String SELECT_COLUMNS = "id, name, address, rating, visited_at, memo, photo_url";
	private final JdbcTemplate jdbcTemplate;

	public CafeRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Cafe> findAll() {
		return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM cafes ORDER BY visited_at DESC, id DESC", ROW_MAPPER);
	}

	public Optional<Cafe> findById(long id) {
		return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM cafes WHERE id = ?", ROW_MAPPER, id)
				.stream().findFirst();
	}

	public Optional<String> findPhotoKeyById(long id) {
		return jdbcTemplate.query("SELECT photo_key FROM cafes WHERE id = ? AND photo_key IS NOT NULL",
				(rs, rowNum) -> rs.getString("photo_key"), id)
				.stream().findFirst();
	}

	public long insert(CafeRequest cafe, String photoUrl, String photoKey) {
		jdbcTemplate.update("INSERT INTO cafes (name, address, rating, visited_at, memo, photo_url, photo_key) VALUES (?, ?, ?, ?, ?, ?, ?)",
				cafe.name(), cafe.address(), cafe.rating(), cafe.visitedAt(), cafe.memo(), photoUrl, photoKey);
		return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
	}

	public boolean update(long id, CafeRequest cafe, String photoUrl, String photoKey) {
		return jdbcTemplate.update("UPDATE cafes SET name = ?, address = ?, rating = ?, visited_at = ?, memo = ?, photo_url = ?, photo_key = ? WHERE id = ?",
				cafe.name(), cafe.address(), cafe.rating(), cafe.visitedAt(), cafe.memo(), photoUrl, photoKey, id) > 0;
	}

	public boolean delete(long id) {
		return jdbcTemplate.update("DELETE FROM cafes WHERE id = ?", id) > 0;
	}

	private static final RowMapper<Cafe> ROW_MAPPER = CafeRepository::mapCafe;

	private static Cafe mapCafe(ResultSet rs, int rowNum) throws SQLException {
		return new Cafe(rs.getLong("id"), rs.getString("name"), rs.getString("address"), rs.getBigDecimal("rating"),
				rs.getDate("visited_at").toLocalDate(), rs.getString("memo"), rs.getString("photo_url"));
	}
}
