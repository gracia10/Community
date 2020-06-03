package com.community;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import com.community.config.JpaConfig;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfig.class})
public class JPAMultipleDBTest {

//	@Autowired
//	private UserRepository userRepository;
	
    @Test
    public void whenCreatingUser_thenCreated() {
       // user = userRepository.save(user);
       // assertTrue(userRepository.findById(user.getId()).isPresent());
    }
}
