package com.devopsbuddy.backend.service;

import com.devopsbuddy.backend.persistence.domain.backend.*;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlanEnum;
import com.devopsbuddy.enums.RolesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService {

    /**
     * The application logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public User createUser(User user, RolesEnum rolesEnum, PlanEnum planEnum) {

        User localUser = userRepository.findByEmail(user.getEmail());

        if (localUser != null) {
            LOG.info("User with username {} and email {} already exist. Nothing will be done.",
                    user.getUsername(), user.getEmail());
        } else {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            Plan plan = new Plan(planEnum);
            if (!planRepository.existsById(planEnum.getId()))
                plan = planRepository.save(plan);

            user.setPlan(plan);

            Role role = new Role(rolesEnum);
            role = roleRepository.save(role);

            Set<UserRole> userRoles = new HashSet<>();
            UserRole userRole = new UserRole(user, role);
            userRoles.add(userRole);

            user.getUserRoles().addAll(userRoles);

            localUser = userRepository.save(user);
        }
        return localUser;
    }

    /**
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * @param email
     * @return
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * @param userId
     * @param password
     */
    @Transactional
    public void updatePassword(long userId, String password) {
        password = passwordEncoder.encode(password);
        userRepository.updateUserPassword(userId, password);
        LOG.debug("Password updated successfullly for user id {}", userId);

        Set<PasswordResetToken> resetTokens = passwordResetTokenRepository.findAllByUserId(userId);
        if (!resetTokens.isEmpty())
            passwordResetTokenRepository.deleteAll(resetTokens);
    }
}
