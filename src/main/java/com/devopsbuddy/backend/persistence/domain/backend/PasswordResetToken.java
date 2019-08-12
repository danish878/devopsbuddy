package com.devopsbuddy.backend.persistence.domain.backend;

import com.devopsbuddy.backend.persistence.converters.LocalDateTimeAttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class PasswordResetToken implements Serializable {

    private static final long serialVersionUID = -6106092076545126105L;

    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetToken.class);

    private static final int DEFAULT_TOKEN_LENGTH_IN_MINUTES = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String token;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime expiryDate;

    @ManyToOne
    private User user;

    public PasswordResetToken() {
    }

    /**
     * Full constructor
     * @param token The user token. It must not be null
     * @param user The user for which the token should be created. It must not be null
     * @param creationDateTime The date time when this request was created. It must not be null
     * @param expirationInMinutes The length, in minutes, for which this token will be valid. If zero, it will be
     *                            assigned a default value of 120 (2 hours)
     * @throws IllegalArgumentException If the token, user or creation date time are null
     */
    public PasswordResetToken(String token, User user, LocalDateTime creationDateTime, int expirationInMinutes) {
        if(token == null || user == null || creationDateTime == null)
            throw new IllegalArgumentException("token, user or creation date time cannot be null");

        if(expirationInMinutes == 0) {
            LOG.warn("The token expiration length in minutes is zero. Assigning the default value {}", DEFAULT_TOKEN_LENGTH_IN_MINUTES);
            expirationInMinutes = DEFAULT_TOKEN_LENGTH_IN_MINUTES;
        }

        this.token = token;
        this.user = user;
        this.expiryDate = creationDateTime.plusMinutes(expirationInMinutes);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordResetToken that = (PasswordResetToken) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
