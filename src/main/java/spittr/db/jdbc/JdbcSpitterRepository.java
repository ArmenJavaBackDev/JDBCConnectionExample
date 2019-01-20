package spittr.db.jdbc;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import spittr.db.SpitterRepository;
import spittr.domain.Spitter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcSpitterRepository implements SpitterRepository {

    private static final String INSERT_SPITTER="INSERT INTO spitter (username,password,fullname,email,updateByEmail)"
            +" VALUES(?,?,?,?,?)";

    private static final String SELECT_SPITTER="SELECT id,username,password,fullname,email,updateByEmail FROM spitter";
    private JdbcOperations jdbc;

    public JdbcSpitterRepository(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public long count() {
        return jdbc.queryForObject("SELECT count(id) FROM spitter",Long.class);
    }

    @SuppressWarnings("unused")
    public void insertSpitter(Spitter spitter){
        jdbc.update(INSERT_SPITTER,
                spitter.getUsername(),
                spitter.getPassword(),
                spitter.getFullName(),
                spitter.getEmail(),
                spitter.isUpdateByEmail());
    }

    public Spitter save(Spitter spitter) {
        Long id=spitter.getId();
        if (id==null){
            long spitterId=insertSpitterAndReturnId(spitter);
            return new Spitter(spitterId,
                    spitter.getUsername(),
                    spitter.getPassword(),
                    spitter.getFullName(),
                    spitter.getEmail(),
                    spitter.isUpdateByEmail());
        }else {
            jdbc.update("UPDATE spitter SET username=?, password=?,fullname=?, email=?, updateByEmail=?  WHERE id=?",
                    spitter.getUsername(),
                    spitter.getPassword(),
                    spitter.getFullName(),
                    spitter.getEmail(),
                    spitter.isUpdateByEmail(),
                    id);
        }
        return spitter;
    }

    private long insertSpitterAndReturnId(Spitter spitter) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert((JdbcTemplate) jdbc).withTableName("Spitter");
        jdbcInsert.setGeneratedKeyName("id");
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("username", spitter.getUsername());
        args.put("password", spitter.getPassword());
        args.put("fullname", spitter.getFullName());
        args.put("email", spitter.getEmail());
        args.put("updateByEmail", spitter.isUpdateByEmail());
        long spitterId = jdbcInsert.executeAndReturnKey(args).longValue();
        return spitterId;
    }

    public Spitter findOne(long id) {
        return jdbc.queryForObject(SELECT_SPITTER+" WHERE id=?",new SpitterRowMapper(),id);
    }

    public Spitter findByUsername(String username) {
        Spitter spitter=jdbc.queryForObject("SELECT id,username,password,fullname,email,updateByEmail FROM spitter WHERE username=?",
                new SpitterRowMapper(),
                username);
        return spitter;
    }

    public List<Spitter> findAll() {
        return jdbc.query("SELECT id,username,password,fullname,email,updateByEmail FROM spitter ORDER BY id",
                new SpitterRowMapper());
    }

    private static final class SpitterRowMapper implements RowMapper<Spitter>{

        public Spitter mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            long id=resultSet.getLong("id");
            String username=resultSet.getString("username");
            String password=resultSet.getString("password");
            String fullName=resultSet.getString("fullname");
            String email=resultSet.getString("email");
            boolean updateByEmail=resultSet.getBoolean("updateByEmail");
            return new Spitter(id,username,password,fullName,email,updateByEmail);
        }
    }
}
