package com.vendiola.triage.auth;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.vendiola.triage.user.Role;
import com.vendiola.triage.user.User;
import com.vendiola.triage.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exercises login, token validation and role checks through the real HTTP layer. Each test runs in a transaction that
 * is rolled back, so no data is left in the database.
 */
@SpringBootTest(properties = "triage.jwt.secret=dGVzdC1vbmx5LXNpZ25pbmcta2V5LW5vdC1mb3ItcHJvZHVjdGlvbi11c2U=")
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTests {

	private static final String PASSWORD = "correct-horse-battery";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void createUsers() {
		userRepository.save(new User("it-admin", "it-admin@example.com", passwordEncoder.encode(PASSWORD), Role.ADMIN));
		userRepository
			.save(new User("it-analyst", "it-analyst@example.com", passwordEncoder.encode(PASSWORD), Role.ANALYST));
	}

	@Test
	void loginWithValidCredentialsReturnsBearerToken() throws Exception {
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson("it-admin")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tokenType").value("Bearer"))
			.andExpect(jsonPath("$.accessToken").isNotEmpty())
			.andExpect(jsonPath("$.expiresIn").value(900));
	}

	@Test
	void loginWithWrongPasswordReturnsGenericProblem() throws Exception {
		mockMvc
			.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"it-admin\",\"password\":\"wrong-password\"}"))
			.andExpect(status().isUnauthorized())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.detail").value("Invalid username or password"));
	}

	@Test
	void loginWithUnknownUserReturnsSameProblemAsWrongPassword() throws Exception {
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson("nobody")))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.detail").value("Invalid username or password"));
	}

	@Test
	void loginWithBlankFieldsIsRejected() throws Exception {
		mockMvc
			.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"\",\"password\":\"\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void requestWithoutTokenIsUnauthorized() throws Exception {
		mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
	}

	@Test
	void requestWithTamperedTokenIsUnauthorized() throws Exception {
		String token = loginAs("it-analyst");
		String tampered = token.substring(0, token.length() - 4) + "AAAA";

		mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + tampered))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void currentUserIsResolvedFromTokenWithoutExposingPasswordHash() throws Exception {
		mockMvc.perform(get("/api/users/me").header("Authorization", bearer("it-analyst")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("it-analyst"))
			.andExpect(jsonPath("$.role").value("ANALYST"))
			.andExpect(jsonPath("$.passwordHash").doesNotExist());
	}

	@Test
	void analystCannotListUsers() throws Exception {
		mockMvc.perform(get("/api/users").header("Authorization", bearer("it-analyst")))
			.andExpect(status().isForbidden());
	}

	@Test
	void analystCannotCreateUsers() throws Exception {
		mockMvc
			.perform(post("/api/users").header("Authorization", bearer("it-analyst"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(createUserJson("it-new-user", "it-new-user@example.com")))
			.andExpect(status().isForbidden());
	}

	@Test
	void adminCanCreateUser() throws Exception {
		mockMvc
			.perform(post("/api/users").header("Authorization", bearer("it-admin"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(createUserJson("it-new-user", "it-new-user@example.com")))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", startsWith("/api/users/")))
			.andExpect(jsonPath("$.username").value("it-new-user"))
			.andExpect(jsonPath("$.role").value("LEAD"));
	}

	@Test
	void creatingDuplicateUsernameReturnsConflict() throws Exception {
		mockMvc
			.perform(post("/api/users").header("Authorization", bearer("it-admin"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(createUserJson("it-analyst", "someone-else@example.com")))
			.andExpect(status().isConflict());
	}

	@Test
	void creatingUserWithShortPasswordIsRejected() throws Exception {
		mockMvc
			.perform(post("/api/users").header("Authorization", bearer("it-admin"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"it-short\",\"email\":\"it-short@example.com\","
						+ "\"password\":\"short\",\"role\":\"ANALYST\"}"))
			.andExpect(status().isBadRequest());
	}

	private String bearer(String username) throws Exception {
		return "Bearer " + loginAs(username);
	}

	private String loginAs(String username) throws Exception {
		String body = mockMvc
			.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson(username)))
			.andReturn()
			.getResponse()
			.getContentAsString();
		return JsonPath.read(body, "$.accessToken");
	}

	private static String loginJson(String username) {
		return "{\"username\":\"" + username + "\",\"password\":\"" + PASSWORD + "\"}";
	}

	private static String createUserJson(String username, String email) {
		return "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + PASSWORD
				+ "\",\"role\":\"LEAD\"}";
	}

}
