package com.vendiola.triage.auth;

import com.vendiola.triage.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads login credentials from the {@code users} table for Spring Security.
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public DatabaseUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return userRepository.findByUsername(username)
			.map(user -> User.withUsername(user.getUsername())
				.password(user.getPasswordHash())
				.roles(user.getRole().name())
				.disabled(!user.isEnabled())
				.build())
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

}
