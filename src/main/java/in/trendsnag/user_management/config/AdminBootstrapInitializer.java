package in.trendsnag.user_management.config;

import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.RoleRepository;
import in.trendsnag.user_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Order(2)
public class AdminBootstrapInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    @Value("${app.bootstrap.admin.enabled:false}") private boolean enabled;
    @Value("${app.bootstrap.admin.username:}") private String username;
    @Value("${app.bootstrap.admin.email:}") private String email;
    @Value("${app.bootstrap.admin.password:}") private String password;

    public AdminBootstrapInitializer(UserRepository users,RoleRepository roles,PasswordEncoder encoder) {
        this.users=users; this.roles=roles; this.encoder=encoder;
    }
    @Override public void run(String... args) {
        if (!enabled) return;
        if (username.isBlank() || email.isBlank() || password.length()<12)
            throw new IllegalStateException("Admin bootstrap requires username, email, and 12+ character password");
        if (users.findByUsername(username).isPresent()) return;
        if (users.findByEmail(email).isPresent())
            throw new IllegalStateException("Bootstrap email is already assigned");
        User admin=new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setFirstName("Administrator");
        admin.setPassword(encoder.encode(password));
        admin.setRole(roles.findByName("ADMIN")
            .orElseThrow(() -> new IllegalStateException("ADMIN role missing")));
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        users.save(admin);
    }
}
