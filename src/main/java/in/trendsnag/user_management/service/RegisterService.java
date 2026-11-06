package in.trendsnag.user_management.service;

import in.trendsnag.user_management.dto.UserLoginResponse;
import in.trendsnag.user_management.dto.UserRegisterRequest;
import in.trendsnag.user_management.model.Permission;
import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.RoleRepository;
import in.trendsnag.user_management.repository.UserRepository;
import in.trendsnag.user_management.security.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegisterService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public RegisterService(UserRepository users, RoleRepository roles,
                           PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public UserLoginResponse register(UserRegisterRequest request) {
        if (users.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (users.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()
                && users.findByPhone(request.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        Role defaultRole = roles.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default USER role is not initialized"));
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone() == null || request.getPhone().isBlank() ? null : request.getPhone());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(defaultRole);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = users.save(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        String token = jwtUtil.generateToken(auth);
        Set<String> permissions = user.getRole().getPermissions().stream()
                .map(Permission::getName).collect(Collectors.toSet());
        return new UserLoginResponse(token, user.getUsername(), user.getRole().getName(), permissions);
    }
}
