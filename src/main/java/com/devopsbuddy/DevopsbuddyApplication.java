package com.devopsbuddy;

import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.service.PlanService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.PlanEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class DevopsbuddyApplication implements CommandLineRunner {

	/** The application logger */
	private static final Logger LOG = LoggerFactory.getLogger(DevopsbuddyApplication.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PlanService planService;

	@Value("${webmaster.username}")
	private String webmasterUsername;

	@Value("${webmaster.password}")
	private String webmasterPassword;

	@Value("${webmaster.email}")
	private String getWebmasterEmail;

	public static void main(String[] args) {
		SpringApplication.run(DevopsbuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		LOG.info("Creating Basic and Pro plans in the database...");
		planService.createPlan(PlanEnum.BASIC.getId());
		planService.createPlan(PlanEnum.PRO.getId());

		User user = UserUtils.createBasicUser(webmasterUsername, getWebmasterEmail);
		user.setPassword(webmasterPassword);
		LOG.debug("Creating user with username {}", user.getUsername());
		userService.createUser(user, RolesEnum.ADMIN, PlanEnum.PRO);
		LOG.info("User {} created", user.getUsername());
	}
}
