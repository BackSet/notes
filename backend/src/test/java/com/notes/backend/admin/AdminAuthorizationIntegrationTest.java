package com.notes.backend.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Authorization tests for the admin endpoints: the bootstrapped admin (with all
 * base permissions) is allowed; a publicly registered user (no permissions) is
 * forbidden; no token is unauthorized. Also covers the last-admin protection.
 * Uses an isolated in-memory DB so the bootstrapped admin does not leak.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:admin-authz-test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "app.initial-admin.enabled=true",
        "app.initial-admin.email=admin@example.com",
        "app.initial-admin.username=admin",
        "app.initial-admin.password=admin-bootstrap-password"
})
class AdminAuthorizationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void admin_canListUsers() throws Exception {
        String token = adminToken();
        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username", hasItem("admin")));
    }

    @Test
    void admin_canReadRolesAndPermissions() throws Exception {
        String token = adminToken();
        mockMvc.perform(get("/api/admin/roles").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("ADMIN")))
                .andExpect(jsonPath("$[?(@.name=='ADMIN')].permissions[*]", hasItem("USER_READ")));
        mockMvc.perform(get("/api/admin/permissions").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("PERMISSION_READ")));
    }

    @Test
    void adminEndpoint_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
    }

    @Test
    void publicUser_cannotAccessAdminEndpoints() throws Exception {
        register("pub@example.com", "pubuser");
        String token = token("pubuser", "Sup3rSecret!");

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/roles").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/permissions").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canGetUserById_andMissingUserIs404() throws Exception {
        String token = adminToken();
        long adminId = meId(token);

        mockMvc.perform(get("/api/admin/users/" + adminId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", org.hamcrest.Matchers.is("admin")));

        mockMvc.perform(get("/api/admin/users/999999").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void admin_canDisableAndEnablePublicUser() throws Exception {
        long userId = register("toggle@example.com", "toggleuser");
        String token = adminToken();

        mockMvc.perform(post("/api/admin/users/" + userId + "/disable").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", org.hamcrest.Matchers.is(false)));

        mockMvc.perform(post("/api/admin/users/" + userId + "/enable").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", org.hamcrest.Matchers.is(true)));
    }

    @Test
    void admin_canPatchPublicUser() throws Exception {
        long userId = register("patch@example.com", "patchuser");
        String token = adminToken();

        mockMvc.perform(patch("/api/admin/users/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("username", "patched-user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", org.hamcrest.Matchers.is("patched-user")));
    }

    @Test
    void disablingLastAdmin_isRejected() throws Exception {
        String token = adminToken();
        long adminId = meId(token);

        mockMvc.perform(post("/api/admin/users/" + adminId + "/disable").header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());

        // The admin remains enabled.
        mockMvc.perform(get("/api/admin/users/" + adminId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", org.hamcrest.Matchers.is(true)));
    }

    // --- helpers ---

    private String adminToken() throws Exception {
        return token("admin", "admin-bootstrap-password");
    }

    private long register(String email, String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("email", email, "username", username, "password", "Sup3rSecret!")))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private String token(String identifier, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("identifier", identifier, "password", password)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private long meId(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private String json(String... keyValues) throws Exception {
        JsonNode node = objectMapper.createObjectNode();
        var obj = (com.fasterxml.jackson.databind.node.ObjectNode) node;
        for (int i = 0; i < keyValues.length; i += 2) {
            obj.put(keyValues[i], keyValues[i + 1]);
        }
        return objectMapper.writeValueAsString(obj);
    }
}
