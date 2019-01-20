package spittr.db.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import spittr.db.SpittleRepository;
import spittr.domain.Spitter;
import spittr.domain.Spittle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcSpittleRepository implements SpittleRepository {

    private static final String SELECT_SPITTLE="SELECT sp.id, s.id as spitterId, s.username, s.password, s.fullname,"+
            " s.email, s.updateByEmail, sp.message, sp.postedTime "+
            "FROM spittle sp, spitter s WHERE sp.spitterId=s.id ";
    private static final  String SELECT_SPITTLE_BY_ID=SELECT_SPITTLE+"AND sp.id=?";
    private static final String SELECT_SPITTLES_BY_SPITTER_ID=SELECT_SPITTLE + "AND s.id=? ORDER BY sp.postedTime DESC";
    private static final String SELECT_RECENT_SPITTLES=SELECT_SPITTLE + "ORDER BY sp.postedTime desc limit ?";


    private JdbcOperations jdbcTemplete;

    public JdbcSpittleRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplete=jdbcTemplate;
    }

    public long count() {
        return jdbcTemplete.queryForObject("SELECT COUNT(id) FROM spittle",Long.class);
    }

    public List<Spittle> findRecent() {
        return findRecent(10);
    }

    public List<Spittle> findRecent(int count) {
        return jdbcTemplete.query(SELECT_RECENT_SPITTLES,new SpittleRowMapper(),count);
    }

    public Spittle findOne(long id) {
        try {
            return jdbcTemplete.queryForObject(SELECT_SPITTLE_BY_ID, new SpittleRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Spittle save(Spittle spittle) {
        long spittleId=insertSpittleAndReturnId(spittle);
        return new Spittle(spittleId,spittle.getSpitter(),spittle.getMessage(),spittle.getPostedTime());
    }
    private long insertSpittleAndReturnId(Spittle spittle){
        SimpleJdbcInsert simpleJdbcInsert=new SimpleJdbcInsert((JdbcTemplate) jdbcTemplete).withTableName("spittle");
        simpleJdbcInsert.setGeneratedKeyName("id");
        Map<String,Object> args=new HashMap<String, Object>();
        args.put("spitterId",spittle.getSpitter().getId());
        args.put("message",spittle.getMessage());
        args.put("postedTime",spittle.getPostedTime());
        long spittleId=simpleJdbcInsert.executeAndReturnKey(args).longValue();
        return spittleId;
    }

    public List<Spittle> findBySpitterId(long spitterId) {
        return jdbcTemplete.query(SELECT_SPITTLES_BY_SPITTER_ID,new SpittleRowMapper(),spitterId);
    }

    public void delete(long id) {
        jdbcTemplete.update("DELETE FROM spittle WHERE id=?",id);
    }
    private static final class SpittleRowMapper implements RowMapper<Spittle>{

        public Spittle mapRow(ResultSet resultSet, int i) throws SQLException {
            long id=resultSet.getLong("id");
            String message=resultSet.getString("message");
            Date postedTime=resultSet.getTimestamp("postedTime");
            long spitterId=resultSet.getLong("spitterId");
            String username=resultSet.getString("username");
            String password=resultSet.getString("password");
            String fullName=resultSet.getString("fullname");
            String email=resultSet.getString("email");
            boolean updateByEmail=resultSet.getBoolean("updateByEmail");
            Spitter spitter=new Spitter(spitterId,username,password,fullName,email,updateByEmail);
            return new Spittle(id,spitter,message,postedTime);
        }
    }
}
