package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreateNewUser() {

        User user = createUser(testName);

        Assert.assertNotNull(user);
        Assert.assertTrue(user.getId() != 0);
    }



//    @Test
//    public void testUpdatePassword() {
//
//        User user = createUser(testName);
//        String newPassword = UUID.randomUUID().toString();
//        userService.updatePassword(user.getId(), newPassword);
//
//        //TODO: how to compared passwords?
//        user = userRepository.findById(user.getId()).get();
//        String encryptedPassword = passwordEncoder.encode(newPassword);
//        Assert.assertEquals(encryptedPassword, user.getPassword());
//    }


}
