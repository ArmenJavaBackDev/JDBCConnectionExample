package spittr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import spittr.db.jdbc.JdbcSpittleRepository;
import spittr.domain.Spitter;
import spittr.domain.Spittle;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JdbcConfig.class)
public class JdbcSpittleRepositoryTest {

    @Autowired
    private JdbcSpittleRepository jdbcSpittleRepository;

    @Test
    public void count(){
        assertEquals(15,jdbcSpittleRepository.count());
    }

    @Test
    public void findRecent(){
        {
            List<Spittle> recent = jdbcSpittleRepository.findRecent();
            assertRecent(recent, 10);
        }
        {
            List<Spittle> recent=jdbcSpittleRepository.findRecent(5);
            assertRecent(recent,5);
        }
    }
    @Test
    public void findOne(){
        Spittle thirteen=jdbcSpittleRepository.findOne(13);
        assertEquals(13,thirteen.getId().longValue());
        assertEquals("Bonjour from Art!",thirteen.getMessage());
        assertEquals(1332682500000L,thirteen.getPostedTime().getTime());
        assertEquals(4,thirteen.getSpitter().getId().longValue());
        assertEquals("artnames",thirteen.getSpitter().getUsername());
        assertEquals("password",thirteen.getSpitter().getPassword());
        assertEquals("Art Names",thirteen.getSpitter().getFullName());
        assertEquals("art@habuma.com",thirteen.getSpitter().getEmail());
        assertTrue(thirteen.getSpitter().isUpdateByEmail());
    }

    @Test
    public void findBySpitter(){
        List<Spittle> spittles=jdbcSpittleRepository.findBySpitterId(4L);
        assertEquals(11,spittles.size());
        for (int i=0;i<11;i++){
            assertEquals(15-i,spittles.get(i).getId().longValue());
        }
    }
    @Test
    @Transactional
    public void save(){
        assertEquals(15,jdbcSpittleRepository.count());
        Spitter spitter=jdbcSpittleRepository.findOne(13).getSpitter();
        Spittle spittle=new Spittle(null, spitter,"Un Nuevo Spittle from Art",new Date());
        Spittle saved=jdbcSpittleRepository.save(spittle);
        assertEquals(16,jdbcSpittleRepository.count());
        assertNewSpittle(saved);
        assertNewSpittle(jdbcSpittleRepository.findOne(16L));
    }
    @Test
    @Transactional
    public void delete() {
        assertEquals(15, jdbcSpittleRepository.count());
        assertNotNull(jdbcSpittleRepository.findOne(13));
        jdbcSpittleRepository.delete(13L);
        assertEquals(14, jdbcSpittleRepository.count());
        assertNull(jdbcSpittleRepository.findOne(13));
    }
    private void assertRecent(List<Spittle> recent,int count){
        long[] recentIds=new long[]{3,2,1,15,14,13,12,11,10,9};
        assertEquals(count,recent.size());
        for (int i=0;i<count;i++){
            assertEquals(recentIds[i],recent.get(i).getId().longValue());
        }
    }
    private void assertNewSpittle(Spittle spittle){
        assertEquals(16,spittle.getId().longValue());
    }
}
