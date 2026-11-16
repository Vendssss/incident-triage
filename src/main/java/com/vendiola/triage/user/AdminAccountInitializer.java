package com.vendiola.triage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Creates the first ADMIN account on startup when the users table is empty, so a fresh installation can be logged
 * into. Does nothing once any user exists. The password comes from {@code TRIAGE_ADMIN_PASSWORD} and is never logged.
 */
@Component
class AdminAccountInitializer implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

	private static final int MIN_PASSWORD_LENGTH = 12;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final String username;

	private final String email;

	private final String password;

	AdminAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
			@Value("${triage.bootstrap.admin-username}") String username,
			@Value("${triage.bootstrap.admin-email}") String email,
			@Value("${triage.bootstrap.admin-password}") String password) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (userRepository.count() > 0) {
			return;
		}
		if (!StringUtils.hasText(password)) {
			log.warn("No users exist and TRIAGE_ADMIN_PASSWORD is not set; nobody can log in until it is");
			return;
		}
		if (password.length() < MIN_PASSWORD_LENGTH) {
			throw new IllegalStateException(
					"TRIAGE_ADMIN_PASSWORD must be at least " + MIN_PASSWORD_LENGTH + " characters");
		}
		userRepository.save(new User(username, email, passwordEncoder.encode(password), Role.ADMIN));
		log.info("Created initial admin account '{}'", username);
	}

}
