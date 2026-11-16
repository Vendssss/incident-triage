package com.vendiola.triage.user;

import java.util.List;

import com.vendiola.triage.common.ConflictException;
import com.vendiola.triage.common.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponse create(CreateUserRequest request) {
		if (userRepository.existsByUsername(request.username())) {
			throw new ConflictException("Username is already taken");
		}
		if (userRepository.existsByEmail(request.email())) {
			throw new ConflictException("Email is already registered");
		}
		User user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()),
				request.role());
		return UserResponse.from(userRepository.save(user));
	}

	public UserResponse getByUsername(String username) {
		return userRepository.findByUsername(username)
			.map(UserResponse::from)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	public UserResponse getById(Long id) {
		return userRepository.findById(id)
			.map(UserResponse::from)
			.orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
	}

	public List<UserResponse> getAll() {
		return userRepository.findAll(Sort.by("username")).stream().map(UserResponse::from).toList();
	}

}
