package com.notes.backend.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end auth flow against the security filter chain (register, login, me,
 * refresh, logout, logout-all and token revocation). Transactional so registered
 * users roll back and never leak into the shared in-memory test database.
 */
@SpringBootTest
@Transactional
class AuthFlowIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private static final String PASSWORD = "Sup3rSecret!";

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void register_createsEnabledUserWithNoRoles_andHidesPassword() throws Exception {
        MvcResult result = register("reg@example.com", "reguser")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("reg@example.com")))
                .andExpect(jsonPath("$.username", is("reguser")))
                .andExpect(jsonPath("$.enabled", is(true)))
                .andExpect(jsonPath("$.roles").isEmpty())
                .andExpect(jsonPath("$.permissions").isEmpty())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body.toLowerCase()).doesNotContain("password");
    }

    @Test
    void register_duplicateEmail_returnsConflict() throws Exception {
        register("dup@example.com", "dupuser1").andExpect(status().isCreated());
        register("dup@example.com", "dupuser2").andExpect(status().isConflict());
    }

    @Test
    void login_returnsTokens_andMeWorksWithAccessToken() throws Exception {
        register("login@example.com", "loginuser").andExpect(status().isCreated());

        JsonNode tokens = login("login@example.com", PASSWORD);
        String accessToken = tokens.get("accessToken").asText();
        assertThat(tokens.get("tokenType").asText()).isEqualTo("Bearer");
        assertThat(tokens.get("refreshToken").asText()).isNotBlank();

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("loginuser")))
                .andExpect(jsonPath("$.email", is("login@example.com")));
    }

    @Test
    void login_withUsername_alsoWorks() throws Exception {
        register("byname@example.com", "bynameuser").andExpect(status().isCreated());
        JsonNode tokens = login("bynameuser", PASSWORD);
        assertThat(tokens.get("accessToken").asText()).isNotBlank();
    }

    @Test
    void login_wrongPassword_returnsGenericUnauthorized() throws Exception {
        register("wrong@example.com", "wronguser").andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("identifier", "wrong@example.com", "password", "not-the-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("wrong@example.com"))));
    }

    @Test
    void me_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_validToken_returnsNewAccessToken() throws Exception {
        register("refresh@example.com", "refreshuser").andExpect(status().isCreated());
        JsonNode tokens = login("refresh@example.com", PASSWORD);
        String refreshToken = tokens.get("refreshToken").asText();

        JsonNode refreshed = readJson(mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(refreshed.get("accessToken").asText()).isNotBlank();
    }

    @Test
    void logout_revokesRefreshToken_soRefreshFails() throws Exception {
        register("logout@example.com", "logoutuser").andExpect(status().isCreated());
        JsonNode tokens = login("logout@example.com", PASSWORD);
        String accessToken = tokens.get("accessToken").asText();
        String refreshToken = tokens.get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("refreshToken", refreshToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("refreshToken", refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutAll_revokesEveryRefreshToken() throws Exception {
        register("logoutall@example.com", "logoutalluser").andExpect(status().isCreated());
        JsonNode first = login("logoutall@example.com", PASSWORD);
        JsonNode second = login("logoutall@example.com", PASSWORD);
        String accessToken = first.get("accessToken").asText();

        mockMvc.perform(post("/api/auth/logout-all")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("refreshToken", first.get("refreshToken").asText())))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("refreshToken", second.get("refreshToken").asText())))
                .andExpect(status().isUnauthorized());
    }

    // --- helpers ---

    private org.springframework.test.web.servlet.ResultActions register(String email, String username) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("email", email, "username", username, "password", PASSWORD)));
    }

    private JsonNode login(String identifier, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("identifier", identifier, "password", password)))
                .andExpect(status().isOk())
                .andReturn();
        return readJson(result);
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String json(String... keyValues) throws Exception {
        var node = objectMapper.createObjectNode();
        for (int i = 0; i < keyValues.length; i += 2) {
            node.put(keyValues[i], keyValues[i + 1]);
        }
        return objectMapper.writeValueAsString(node);
    }
}
