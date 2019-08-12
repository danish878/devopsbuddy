package com.devopsbuddy.backend.service;

import com.devopsbuddy.web.domain.frontend.FeedbackPojo;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {

    /**
     * Sends an email with the content in the Feedback Pojo
     * @param feedbackPojo The Feedback Pojo
     */
    void sendFeedbackEmail(FeedbackPojo feedbackPojo);

    /**
     * Sends an email with the content of the Simple Mail Message object.
     * @param message The object containing the email message
     */
    void sendGenericEmailMessage(SimpleMailMessage message);
}
