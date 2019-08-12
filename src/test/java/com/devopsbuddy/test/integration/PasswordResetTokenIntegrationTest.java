package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordResetTokenIntegrationTest extends AbstractIntegrationTest {

    @Value("${token.expiration.length.minutes}")
    private int expirationTimeInMinutes;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Rule public TestName testName = new TestName();

    @Before
    public void init() {
        Assert.assertTrue(expirationTimeInMinutes == 120);
    }

    @Test
    public void testTokenExpirationLength() throws Exception {

        User user = createUser(testName);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());


        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token = UUID.randomUUID().toString();

        LocalDateTime expectedTime = now.plusMinutes(expirationTimeInMinutes);

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);

        LocalDateTime actualTime = passwordResetToken.getExpiryDate();
        Assert.assertNotNull(actualTime);
        Assert.assertEquals(expectedTime, actualTime);
    }

    @Test
    public void testFindTokenByTokenValue() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        createPasswordResetToken(token, user, now);

        PasswordResetToken retrievedPasswordResetToken = passwordResetTokenRepository.findByToken(token);
        Assert.assertNotNull(retrievedPasswordResetToken);
        Assert.assertNotNull(retrievedPasswordResetToken.getId());
        Assert.assertNotNull(retrievedPasswordResetToken.getUser());
    }

    @Test
    public void testDeleteToken() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        long tokenId = passwordResetToken.getId();
        passwordResetTokenRepository.deleteById(tokenId);

        Optional<PasswordResetToken> shouldNotExistToken = passwordResetTokenRepository.findById(tokenId);

        Assert.assertFalse(shouldNotExistToken.isPresent());
    }

    @Test
    public void testCascadeDeleteFromUserEntity() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        createPasswordResetToken(token, user, now);

        userRepository.deleteById(user.getId());

        Set<PasswordResetToken> shouldBeEmpty = passwordResetTokenRepository.findAllByUserId(user.getId());
        Assert.assertTrue(shouldBeEmpty.isEmpty());
    }

    @Test
    public void testMultipleTokensAreReturnedWhenQueringByUserId() throws Exception {

        User user = createUser(testName);
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();
        String token3 = UUID.randomUUID().toString();

        Set<PasswordResetToken> tokens = new HashSet<>();
        tokens.add(createPasswordResetToken(token1, user, now));
        tokens.add(createPasswordResetToken(token2, user, now));
        tokens.add(createPasswordResetToken(token3, user, now));

        passwordResetTokenRepository.saveAll(tokens);

        User founduser = userRepository.findById(user.getId()).get();

        Set<PasswordResetToken> actualTokens = passwordResetTokenRepository.findAllByUserId(founduser.getId());
        Assert.assertTrue(actualTokens.size() == tokens.size());
        List<String> tokensAsList = tokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        List<String> actualTokensAsList = actualTokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        Assert.assertEquals(tokensAsList, actualTokensAsList);
    }


    /**
     * PRIVATE METHOD
     * @param token
     * @param user
     * @param now
     * @return
     */
    private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime now) {

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, now, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        Assert.assertNotNull(passwordResetToken.getId());
        return passwordResetToken;

    }
}
