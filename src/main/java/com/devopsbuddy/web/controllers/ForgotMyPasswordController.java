package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.I18NService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

@Controller
public class ForgotMyPasswordController {

    public static final String EMAIL_FORM_VIEW_NAME = "forgotmypassword/emailForm";
    public static final String CHANGE_PASSWORD_VIEW_NAME = "forgotmypassword/changePassword";
    public static final String PASSWORD_RESET_ATTRIBUTE_NAME = "passwordReset";
    public static final String MESSAGE_ATTRIBUTE_NAME = "message";
    public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";
    public static final String CHANGE_PASSWORD_URL_MAPPING = "/changeuserpassword";
    public static final String EMAIL_SENT_KEY = "emailSent";
    public static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";
    public static final String EMAIL_SUBJECT_TEXT_PROPERTY_NAME = "forgotmypassword.email.subject";

    /**
     * The application logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ForgotMyPasswordController.class);

    @Autowired
    private I18NService i18NService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Value("webmaster.email")
    private String webMasterEmail;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @GetMapping(FORGOT_PASSWORD_URL_MAPPING)
    public String forgotPassword() {
        return EMAIL_FORM_VIEW_NAME;
    }

    @PostMapping(FORGOT_PASSWORD_URL_MAPPING)
    public String forgotPassword(HttpServletRequest request, @RequestParam("email") String email, Model model) {

        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);

        if (passwordResetToken == null) {
            LOG.warn("Email {} does not exist", email);
        } else {
            User user = passwordResetToken.getUser();
            String token = passwordResetToken.getToken();

            String passwordResetUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
            LOG.debug("Reset Password URL {}", passwordResetUrl);

            String emailText = i18NService.getMessage(EMAIL_MESSAGE_TEXT_PROPERTY_NAME, request.getLocale());
            String emailSubject = i18NService.getMessage(EMAIL_SUBJECT_TEXT_PROPERTY_NAME, request.getLocale());

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject(emailSubject);
            mailMessage.setText(emailText + "\r\n" + passwordResetUrl);
            mailMessage.setFrom(webMasterEmail);

            emailService.sendGenericEmailMessage(mailMessage);
        }

        model.addAttribute(EMAIL_SENT_KEY, true);

        return EMAIL_FORM_VIEW_NAME;
    }

    @GetMapping(CHANGE_PASSWORD_URL_MAPPING)
    public String changePassword(@RequestParam("id") long id,
                                 @RequestParam("token") String token,
                                 Locale locale,
                                 Model model) {

        if (StringUtils.isEmpty(token) || id == 0) {
            LOG.error("Invalid user id {}  or token value {}", id, token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Invalid user id or token value");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);

        if (passwordResetToken == null) {
            LOG.warn("A token couldn't be found with value {}", token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Token not found");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = passwordResetToken.getUser();
        if (user.getId() != id) {
            LOG.error("The user id {} passed as parameter does not match the user id {} associated with the token {}",
                    id, user.getId(), token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.invalid", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        if (LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpiryDate())) {
            LOG.error("The token {} has expired", token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.expired", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        model.addAttribute("principalId", user.getId());

        // OK to proceed. We auto-authenticate the user so that in the POST request we can check
        // if the user is authenticated
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return CHANGE_PASSWORD_VIEW_NAME;
    }

    @PostMapping(CHANGE_PASSWORD_URL_MAPPING)
    public String changePassword(@RequestParam("principal_id") long userId,
                                 @RequestParam("password") String password,
                                 Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOG.error("An unauthenticated user tried to invoke the reset password POST method");
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = (User) authentication.getPrincipal();
        if (user.getId() != userId) {
            LOG.error("Security breach! User {} is trying to make a password reset request on behalf of {}",
                    user.getId(), userId);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        userService.updatePassword(userId, password);
        LOG.info("Password successfully updated for user {}", user.getUsername());

        model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "true");

        return CHANGE_PASSWORD_VIEW_NAME;
    }

}