package edu.lmu.cs.wutup.ws.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import edu.lmu.cs.wutup.ws.exception.NoSuchUserException;
import edu.lmu.cs.wutup.ws.exception.UserExistsException;

import edu.lmu.cs.wutup.ws.model.User;

@Repository
public class UserDaoJdbcImpl implements UserDao {
    
    private static final String CREATE_SQL = "insert into user(id, firstName, lastName, email, nickname) values(?, ?, ?, ?, ?);";
    private static final String UPDATE_SQL = "update user set firstName=?, lastName=?, email=?, nickname=? where id=?;";
    private static final String DELETE_SQL = "delete from user where id=?;";
    private static final String FIND_BY_ID_SQL = "select * from user where id=?;";
    private static final String FIND_MAX_VALUE_SQL = "select max(id) from user;";
    //private static final String FIND_MAX_VALUE_SQL = "select max(?) from user;";
    private static final String COUNT_SQL = "select count(*) from user;";
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void createUser(User u) {
        try {
            jdbcTemplate.update(CREATE_SQL, u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getNickname());
        } catch (DuplicateKeyException ex) { // TODO go over exception UIDs with Dr. Toal
            throw new UserExistsException();
        }
    }

    @Override
    public User findUserById(int id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, new Object[]{id}, userRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NoSuchUserException();
        }
    }

    @Override
    public void updateUser(User u) {
        int rowsUpdated = jdbcTemplate.update(
                UPDATE_SQL, u.getFirstName(), u.getLastName(), u.getEmail(), u.getNickname(), u.getId());
        
        if (rowsUpdated == 0) {
            throw new NoSuchUserException();
        }
    }
    
    @Override
    public int findNumberOfUsers() {
        return jdbcTemplate.queryForInt(COUNT_SQL);
    }
    
    @Override
    public void deleteUser(User u) {
        int rowsUpdated = jdbcTemplate.update(DELETE_SQL, u.getId());
        if (rowsUpdated == 0) {
            throw new NoSuchUserException();
        }
    }
    
    @Override
    public int getMaxValueFromColumn() {
        // Need to check on what happens if column is empty
        return jdbcTemplate.queryForInt(FIND_MAX_VALUE_SQL);
    }
    
    private static RowMapper<User> userRowMapper = new RowMapper<User>() {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getInt("id"), 
                    rs.getString("firstName"), rs.getString("lastName"), rs.getString("email"), rs.getString("nickname"));
        }
    };

}