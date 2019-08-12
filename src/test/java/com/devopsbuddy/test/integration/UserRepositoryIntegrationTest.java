package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.enums.PlanEnum;
import com.devopsbuddy.enums.RolesEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Rule
    public TestName testName = new TestName();


    @Before
    public void init() {
        Assert.assertNotNull(userRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(planRepository);
    }

    @Test
    public void testCreateNewPlan() {
        Plan basicPlan = createPlan(PlanEnum.BASIC);
        planRepository.save(basicPlan);
        Plan retrievePlan = planRepository.findById(PlanEnum.BASIC.getId()).get();
        Assert.assertNotNull(retrievePlan);
    }

    @Test
    public void testCreateNewRole() {
        Role basicRole = createRole(RolesEnum.BASIC);
        roleRepository.save(basicRole);
        Role retrieveRole = roleRepository.findById(RolesEnum.BASIC.getId()).get();
        Assert.assertNotNull(retrieveRole);
    }

    @Test
    public void testCreateNewUser() {

        String username = testName.getMethodName();
        String email = String.format("%s@devopsbuddy.com", username);
        User basicUser = createUser(username, email);

        User newlyCreatedUser = userRepository.findById(basicUser.getId()).get();

        Assert.assertNotNull(newlyCreatedUser);
        Assert.assertTrue(newlyCreatedUser.getId() != 0);
        Assert.assertNotNull(newlyCreatedUser.getPlan());
        Set<UserRole> newlyCreatedUserUserRoles = newlyCreatedUser.getUserRoles();

        for (UserRole ur : newlyCreatedUserUserRoles) {
            Assert.assertNotNull(ur.getRole());
        }

    }

    @Test
    public void testDeleteUser() {

        String username = testName.getMethodName();
        String email = String.format("%s@devopsbuddy.com", username);
        User user = createUser(username, email);

        userRepository.deleteById(user.getId());
    }

    @Test
    public void testGetUserByEmail() {
        User user = createUser(testName);

        User newlyFoundUser = userRepository.findByEmail(user.getEmail());

        Assert.assertNotNull(newlyFoundUser);
        Assert.assertEquals(user, newlyFoundUser);
    }

    @Test
    public void testUpdateUserPassword() {

        User user = createUser(testName);

        String newPassword = UUID.randomUUID().toString();

        userRepository.updateUserPassword(user.getId(), newPassword);

        user = userRepository.findById(user.getId()).get();

        Assert.assertEquals(newPassword, user.getPassword());
    }
}
