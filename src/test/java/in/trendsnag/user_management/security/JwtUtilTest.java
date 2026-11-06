package in.trendsnag.user_management.security;

import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private final String secret = "test-secret-only-1234567890123456789012345678901234567890";

    @Test
    void roundTripTokenIsSignedAndBoundToUsername() {
        JwtUtil jwt = new JwtUtil(secret);
        User user = new User();
        user.setUsername("sample");
        Role role = new Role();
        role.setName("USER");
        user.setRole(role);

        String token = jwt.generateToken(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        assertEquals("sample", jwt.extractIdentifier(token));
        assertTrue(jwt.isTokenValid(token, "sample"));
        assertFalse(jwt.isTokenValid(token, "someone-else"));
    }

    @Test
    void rejectsWeakSigningSecret() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("unsafe"));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtUtil jwt = new JwtUtil(secret);
        User user = new User();
        user.setUsername("sample");
        Role role = new Role();
        role.setName("USER");
        user.setRole(role);
        String token = jwt.generateToken(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        assertThrows(RuntimeException.class, () -> jwt.extractIdentifier(token + "tamper"));
    }
}
