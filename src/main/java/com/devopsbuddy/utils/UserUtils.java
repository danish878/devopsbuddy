package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.web.controllers.ForgotMyPasswordController;
import com.devopsbuddy.web.domain.frontend.BasicAccountPayload;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {

    /**
     * Non instantiable
     */
    private UserUtils() {
        throw new AssertionError("Non instantiable");
    }

    /**
     * Creates a user with basic attributes set.
     * @param username
     * @param email
     * @return A User entity
     */
    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(email);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("phone");
        user.setCountry("country");
        user.setEnabled(true);
        user.setDescription("description");
        user.setProfileImageUrl("imageurl");

        return user;
    }

    /**
     * Builds and returns the URL to reset the user password
     * @param request The Http Servlet Request
     * @param userId
     * @param token
     * @return the URL to reset the user password
     */
    public static String createPasswordResetUrl(HttpServletRequest request, long userId, String token) {
        String passwordResetUrl =
                request.getScheme() +
                        "://" +
                        request.getServerName() +
                        ":" +
                        request.getServerPort() +
                        request.getContextPath() +
                        ForgotMyPasswordController.CHANGE_PASSWORD_URL_MAPPING +
                        "?id=" +
                        userId +
                        "&token=" +
                        token;

        return passwordResetUrl;
    }

    public static <T extends BasicAccountPayload> User fromWebUserToDomainUser(T frontEndPayload) {
        User user = new User();
        user.setUsername(frontEndPayload.getUsername());
        user.setPassword(frontEndPayload.getPassword());
        user.setFirstName(frontEndPayload.getFirstName());
        user.setLastName(frontEndPayload.getLastName());
        user.setEmail(frontEndPayload.getEmail());
        user.setPhoneNumber(frontEndPayload.getPhoneNumber());
        user.setCountry(frontEndPayload.getCountry());
        user.setEnabled(true);
        user.setDescription(frontEndPayload.getDescription());

        return user;
    }
}
