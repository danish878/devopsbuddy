package com.devopsbuddy.backend.persistence.repositories;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Returns a User given a username or null if not found
     * @param username The username
     * @return a User given a username or null if not found
     */
    User findByUsername(String username);

    /**
     * Returns a User given an email or null if not found
     * @param email
     * @return a User given an email or null if not found
     */
    User findByEmail(String email);

    /**
     *
     * @param userId
     * @param password
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updateUserPassword(@Param("userId") long userId, @Param("password") String password);
}
