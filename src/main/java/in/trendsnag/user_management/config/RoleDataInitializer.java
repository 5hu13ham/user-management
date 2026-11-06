package in.trendsnag.user_management.config;

import in.trendsnag.user_management.model.Permission;
import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.repository.PermissionRepository;
import in.trendsnag.user_management.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Component
public class RoleDataInitializer implements CommandLineRunner {
    private final RoleRepository roles;
    private final PermissionRepository permissions;

    public RoleDataInitializer(RoleRepository roles, PermissionRepository permissions) {
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Permission userRead = ensurePermission("USER_READ");
        Permission userWrite = ensurePermission("USER_WRITE");
        Permission adminRead = ensurePermission("ADMIN_READ");
        Permission adminWrite = ensurePermission("ADMIN_WRITE");

        ensureRole("USER", Set.of(userRead));
        ensureRole("ADMIN", Set.of(userRead, userWrite, adminRead, adminWrite));
        // Intentional: no default administrator account or hard-coded password.
    }

    private Permission ensurePermission(String name) {
        return permissions.findByName(name).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setName(name);
            permission.setDescription(name.replace('_', ' '));
            return permissions.save(permission);
        });
    }

    private void ensureRole(String name, Set<Permission> defaultPermissions) {
        Role role = roles.findByName(name).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(name);
            return roles.save(newRole);
        });
        if (role.getPermissions().isEmpty()) {
            role.setPermissions(defaultPermissions);
            roles.save(role);
        }
    }
}
