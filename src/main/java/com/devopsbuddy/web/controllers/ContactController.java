package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.web.domain.frontend.FeedbackPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(ContactController.class);

    /** The key which identifies the feedback payload in the Model */
    public static final String FEEDBACK_MODEL_KEY = "feedback";

    /** The key which identifies the feedback payload in the Model. */
    private static final String CONTACT_US_VIEW_NAME = "contact/contact";

    public static final String EMAIL_SENT = "emailSent";

    @Autowired
    private EmailService emailService;

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute(FEEDBACK_MODEL_KEY, new FeedbackPojo());
        return CONTACT_US_VIEW_NAME;
    }

    @PostMapping("/contact")
    public String contact(@ModelAttribute(FEEDBACK_MODEL_KEY) FeedbackPojo feedback, Model model) {
        LOG.debug("Feedback POJO content: {}", feedback);
        emailService.sendFeedbackEmail(feedback);
        model.addAttribute(EMAIL_SENT, true);
        return CONTACT_US_VIEW_NAME;
    }
}
