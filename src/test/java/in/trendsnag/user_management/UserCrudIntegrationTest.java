package in.trendsnag.user_management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.RoleRepository;
import in.trendsnag.user_management.repository.UserRepository;
import in.trendsnag.user_management.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserCrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository users;
    @Autowired RoleRepository roles;
    @Autowired JwtUtil jwt;

    @Test
    void adminCanCreateFilterUpdateAndSoftDeleteUsers() throws Exception {
        String suffix=UUID.randomUUID().toString().replace("-", "").substring(0, 9);
        User admin=new User();
        admin.setUsername("admin"+suffix);
        admin.setEmail("admin"+suffix+"@example.com");
        admin.setFirstName("Test Admin");
        admin.setRole(roles.findByName("ADMIN").orElseThrow());
        admin.setPassword("not-for-login-in-this-test");
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin=users.save(admin);
        String token="Bearer "+jwt.generateToken(
            new UsernamePasswordAuthenticationToken(admin,null,admin.getAuthorities()));
        String username="demo"+suffix;
        String payload=mapper.writeValueAsString(Map.of(
            "username",username,"email",username+"@example.com",
            "firstName","Demo","lastName","User","password","StrongPassword123!"));
        String response=mvc.perform(post("/users").header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.username").value(username))
            .andReturn().getResponse().getContentAsString();
        JsonNode created=mapper.readTree(response).path("data");
        String id=created.path("id").asText();

        mvc.perform(get("/users/filter-search").header("Authorization",token)
                .param("keyword",username).param("size","10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalItems").value(1));

        mvc.perform(patch("/users/"+id+"/active").header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"active\":false}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.active").value(false));

        mvc.perform(delete("/users/"+id).header("Authorization",token))
            .andExpect(status().isOk());

        mvc.perform(get("/users/"+id).header("Authorization",token))
            .andExpect(status().isNotFound());

        users.deleteById(admin.getId());
    }
}
