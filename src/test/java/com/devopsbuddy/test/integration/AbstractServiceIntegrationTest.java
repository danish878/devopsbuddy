package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.PlanEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class AbstractServiceIntegrationTest {

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    protected User createUser(TestName testName) {
        String username = testName.getMethodName();
        String email = String.format("%s@devopsbuddy.com", username);

        User user = UserUtils.createBasicUser(username, email);
        user = userService.createUser(user, RolesEnum.BASIC, PlanEnum.BASIC);
        return user;
    }
}
