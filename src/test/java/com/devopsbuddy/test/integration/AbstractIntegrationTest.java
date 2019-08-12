package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlanEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractIntegrationTest {

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PlanRepository planRepository;

    protected Role createRole(RolesEnum rolesEnum) {
        return new Role(rolesEnum);
    }

    protected Plan createPlan(PlanEnum planEnum) {
        return new Plan(planEnum.BASIC);
    }

    protected User createUser(String username, String email) {
        Plan basicPlan = createPlan(PlanEnum.BASIC);
        planRepository.save(basicPlan);

        User basicUser = UserUtils.createBasicUser(username, email);
        basicUser.setPlan(basicPlan);

        Role basicRole = createRole(RolesEnum.BASIC);
        roleRepository.save(basicRole);

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole(basicUser, basicRole);
        userRoles.add(userRole);

        basicUser.getUserRoles().addAll(userRoles);

        basicUser = userRepository.save(basicUser);

        return basicUser;
    }

    protected User createUser(TestName testName) {
        return createUser(testName.getMethodName(), testName.getMethodName()+"@devopsbuddy.com");
    }
}
