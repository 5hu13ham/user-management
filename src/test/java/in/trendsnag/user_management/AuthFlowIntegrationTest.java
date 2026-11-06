package in.trendsnag.user_management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @Test
    void publicHealthEndpointAndPrivateUserEndpoint() throws Exception {
        mvc.perform(get("/api/ping")).andExpect(status().isOk());
        mvc.perform(get("/users")).andExpect(status().isUnauthorized());
    }

    @Test
    void registrationGrantsOnlyOwnProfileAccess() throws Exception {
        String username = "tester" + UUID.randomUUID().toString().substring(0, 10);
        String body = mapper.writeValueAsString(Map.of(
                "username", username,
                "email", username + "@example.com",
                "firstName", "Integration",
                "password", "SafeTestPassword123!"));

        String json = mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn().getResponse().getContentAsString();
        JsonNode response = mapper.readTree(json);
        String bearer = "Bearer " + response.get("token").asText();

        mvc.perform(get("/users/me").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username));

        mvc.perform(get("/users/1").header("Authorization", bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsMalformedJwt() throws Exception {
        mvc.perform(get("/users/me").header("Authorization", "Bearer not.a.real.jwt"))
                .andExpect(status().isUnauthorized());
    }
}
