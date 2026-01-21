package com.storyai.storytelling_backend.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storyai.storytelling_backend.DTO.LoginRequest;
import com.storyai.storytelling_backend.DTO.RegisterRequest;
import com.storyai.storytelling_backend.config.TestDataBuilder;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.*;
import com.storyai.storytelling_backend.service.JwtService;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected StoryRepository storyRepository;

    @Autowired
    protected StoryChapterRepository storyChapterRepository;

    @Autowired
    protected StorySessionRepository storySessionRepository;

    @Autowired
    protected EmailVerificationRepository emailVerificationRepository;

    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;

    @Autowired
    protected TestDataBuilder testDataBuilder;

    // Common constants
    protected static final String AUTH_HEADER = "Authorization";
    protected static final String BEARER_PREFIX = "Bearer ";

    @BeforeEach
    void baseSetUp() {
        storySessionRepository.deleteAll();
        storyChapterRepository.deleteAll();
        storyRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        emailVerificationRepository.deleteAll();
        userRepository.deleteAll();
        TestDataBuilder.resetCounters();
    }

    /**
     * Registers a new test user and returns the user and password
     */
    protected TestDataBuilder.UserWithPlainPassword registerTestUser() {
        return testDataBuilder.createUserWithPlainPassword();
    }

    /**
     * Logs in a user and returns the JWT token
     */
    protected String loginUser(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    /**
     * Creates and returns a test user with a valid JWT token
     */
    protected String getAuthToken() throws Exception {
        var userWithPassword = registerTestUser();
        return loginUser(userWithPassword.user().getEmail(), userWithPassword.plainPassword());
    }

    /**
     * Adds JWT authentication to the request
     */
    protected MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder, String token) {
        return requestBuilder.header(AUTH_HEADER, BEARER_PREFIX + token);
    }

    /**
     * Converts an object to JSON string
     */
    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Converts JSON string to the specified type
     */
    protected <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Converts JSON string to the specified type reference (useful for generic types)
     */
    protected <T> T fromJson(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(json, typeReference);
    }

    // ========== Test Data Creation Helpers ==========

    /**
     * Creates and saves a test user with the given email and password
     */
    protected User createAndSaveUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmailVerified(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }


    /**
     * Asserts that the response is a successful response with the given status
     */
    protected ResultActions assertSuccessResponse(ResultActions result, int status) throws Exception {
        return result.andExpect(status().is(status))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    /**
     * Asserts that the response is an error response with the given status and message
     */
    protected ResultActions assertErrorResponse(ResultActions result, int status, String message) throws Exception {
        return result.andExpect(status().is(status))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message").value(containsString(message)));
    }

    /**
     * Asserts that the response contains validation errors for the given fields
     */
    protected ResultActions assertValidationErrors(ResultActions result, String... fields) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.type").value("VALIDATION_ERROR"));

        for (String field : fields) {
            result.andExpect(jsonPath("$.error.errors[?(@.field == '" + field + "')]").exists());
        }

        return result;
    }

    /**
     * Custom matcher for date time assertions
     */
    protected static class DateTimeMatcher extends TypeSafeMatcher<String> {
        @Override
        protected boolean matchesSafely(String item) {
            try {
                LocalDateTime.parse(item);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a valid date time string");
        }
    }

    protected static DateTimeMatcher isDateTime() {
        return new DateTimeMatcher();
    }
}
