package com.dodal.meet.repository;


import com.dodal.meet.model.entity.UserTagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class UserTagBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<UserTagEntity> userTagEntityList) {
        String sql = "INSERT INTO user_tag(tag_name, tag_value, user_id) values (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, userTagEntityList.get(i).getTagName());
                ps.setString(2, userTagEntityList.get(i).getTagValue());
                ps.setLong(3, userTagEntityList.get(i).getUserEntity().getId());
            }

            @Override
            public int getBatchSize() {
                return userTagEntityList.size();
            }
        });
    }

    public void deleteAll(List<UserTagEntity> userTagEntityList) {
        String sql = "DELETE FROM user_tag WHERE user_tag_id in (?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, userTagEntityList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return userTagEntityList.size();
            }
        });
    }
}
