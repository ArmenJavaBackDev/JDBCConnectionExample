package spittr;


import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import spittr.db.jdbc.JdbcSpitterRepository;
import spittr.domain.Spitter;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JdbcConfig.class)
public class JdbcSpitterRepositoryTest {

    @Autowired
    private JdbcSpitterRepository jdbcSpitterRepository;

    @Test
    public void count(){
       assertEquals(4,jdbcSpitterRepository.count());
   }
    @Test
    @Transactional
    public void findAll(){
        List<Spitter> spitters=jdbcSpitterRepository.findAll();
        assertEquals(4,spitters.size());
        assertSpitter(0,spitters.get(0));
        assertSpitter(1,spitters.get(1));
        assertSpitter(2,spitters.get(2));
        assertSpitter(3,spitters.get(3));
    }
    @Test
    @Transactional
    public void findByUsername(){
        assertSpitter(0,jdbcSpitterRepository.findByUsername("habuma"));
        assertSpitter(1,jdbcSpitterRepository.findByUsername("mwalls"));
        assertSpitter(2,jdbcSpitterRepository.findByUsername("chuck"));
        assertSpitter(3,jdbcSpitterRepository.findByUsername("artnames"));
    }

    @Test
    @Transactional
    public void findOne(){
        assertSpitter(0,jdbcSpitterRepository.findOne(1L));
        assertSpitter(1,jdbcSpitterRepository.findOne(2L));
        assertSpitter(2,jdbcSpitterRepository.findOne(3L));
        assertSpitter(3,jdbcSpitterRepository.findOne(4L));
    }

    @Test
    @Transactional
    public void save_newSpitter() {
        assertEquals(4, jdbcSpitterRepository.count());
        Spitter spitter = new Spitter(null, "newbee", "letmein", "New Bee",
                "newbee@habuma.com", false);
        Spitter saved = jdbcSpitterRepository.save(spitter);
        assertEquals(5, jdbcSpitterRepository.count());
        assertSpitter(4, saved);
        assertSpitter(4, jdbcSpitterRepository.findOne(5L));
    }

    @Test
    @Transactional
    public void save_existingSpitter() {
        assertEquals(4, jdbcSpitterRepository.count());
        Spitter spitter = new Spitter(4L,"arthur","letmein","Arthur Names",
                "arthur@habuma.com",false);
        Spitter saved = jdbcSpitterRepository.save(spitter);
        assertSpitter(5, saved);
        assertEquals(4, jdbcSpitterRepository.count());
        Spitter updated = jdbcSpitterRepository.findOne(4L);
        assertSpitter(5, updated);
    }

    private static void assertSpitter(int expectedSpitterIndex,Spitter actual){
        assertSpitter(expectedSpitterIndex,actual,"Newbie");

    }

    private static void assertSpitter(int expectedSpitterIndex, Spitter actual, String expectedStatus){
        Spitter expected=SPITTERS[expectedSpitterIndex];
        assertEquals(expected.getId(),actual.getId());
        assertEquals(expected.getUsername(),actual.getUsername());
        assertEquals(expected.getPassword(),actual.getPassword());
        assertEquals(expected.getFullName(),actual.getFullName());
        assertEquals(expected.getEmail(),actual.getEmail());
        assertEquals(expected.isUpdateByEmail(),actual.isUpdateByEmail());
    }

    private static Spitter[] SPITTERS=new Spitter[6];

    @BeforeClass
    public static void  before(){
        SPITTERS[0]=new Spitter(1L,"habuma","password","Craig Walls",
                "craig@habuma.com",false );
        SPITTERS[1]=new Spitter(2L,"mwalls","password","Michael Walls",
                "mwalls@habuma.com",true );
        SPITTERS[2]=new Spitter(3L,"chuck","password","Chuck Wagon",
                "chuck@habuma.com",false );
        SPITTERS[3]=new Spitter(4L,"artnames","password","Art Names",
                "art@habuma.com",true );
        SPITTERS[4]=new Spitter(5L, "newbee", "letmein", "New Bee",
                "newbee@habuma.com", false );
        SPITTERS[5]=new Spitter(4L,"arthur","letmein","Arthur Names",
                "arthur@habuma.com",false );
        //Spitter spitter = new Spitter(4L,"arthur","letmein","Arthur Names",
        //                "arthur@habuma.com",false);
    }
}