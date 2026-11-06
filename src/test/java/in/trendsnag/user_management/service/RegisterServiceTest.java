package in.trendsnag.user_management.service;

import in.trendsnag.user_management.dto.UserRegisterRequest;
import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.RoleRepository;
import in.trendsnag.user_management.repository.UserRepository;
import in.trendsnag.user_management.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {
    @Mock private UserRepository users;
    @Mock private RoleRepository roles;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtUtil jwt;

    private RegisterService service;

    @BeforeEach
    void setUp() {
        service = new RegisterService(users, roles, encoder, jwt);
    }

    private UserRegisterRequest request() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("sample");
        request.setEmail("sample@example.com");
        request.setFirstName("Sample");
        request.setPassword("strongpass123");
        return request;
    }

    @Test
    void registersWithLeastPrivilegedRoleAndHashedPassword() {
        Role userRole = new Role();
        userRole.setName("USER");
        when(users.findByUsername("sample")).thenReturn(Optional.empty());
        when(users.findByEmail("sample@example.com")).thenReturn(Optional.empty());
        when(roles.findByName("USER")).thenReturn(Optional.of(userRole));
        when(encoder.encode("strongpass123")).thenReturn("hashed-password");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwt.generateToken(any())).thenReturn("signed.jwt.value");

        var result = service.register(request());

        assertEquals("sample", result.getUsername());
        assertEquals("USER", result.getRole());
        assertEquals("signed.jwt.value", result.getToken());
        var captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        assertEquals("hashed-password", captor.getValue().getPassword());
        assertTrue(captor.getValue().isActive());
        assertSame(userRole, captor.getValue().getRole());
    }

    @Test
    void rejectsDuplicateUsernameBeforeSaving() {
        when(users.findByUsername("sample")).thenReturn(Optional.of(new User()));
        assertThrows(IllegalArgumentException.class, () -> service.register(request()));
        verify(users, never()).save(any());
    }

    @Test
    void rejectsDuplicateEmailBeforeSaving() {
        when(users.findByUsername("sample")).thenReturn(Optional.empty());
        when(users.findByEmail("sample@example.com")).thenReturn(Optional.of(new User()));
        assertThrows(IllegalArgumentException.class, () -> service.register(request()));
        verify(users, never()).save(any());
    }

    @Test
    void failsSafelyIfDefaultRoleIsNotInitialized() {
        when(users.findByUsername("sample")).thenReturn(Optional.empty());
        when(users.findByEmail("sample@example.com")).thenReturn(Optional.empty());
        when(roles.findByName("USER")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> service.register(request()));
        verify(users, never()).save(any());
    }
}
