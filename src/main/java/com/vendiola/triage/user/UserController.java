package com.vendiola.triage.user;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public UserResponse currentUser(@AuthenticationPrincipal Jwt jwt) {
		return userService.getByUsername(jwt.getSubject());
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('LEAD', 'ADMIN')")
	public List<UserResponse> listUsers() {
		return userService.getAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('LEAD', 'ADMIN')")
	public UserResponse getUser(@PathVariable Long id) {
		return userService.getById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
		UserResponse created = userService.create(request);
		return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
	}

}
