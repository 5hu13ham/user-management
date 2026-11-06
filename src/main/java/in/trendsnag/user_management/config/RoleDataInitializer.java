package in.trendsnag.user_management.config;

import in.trendsnag.user_management.model.Permission;
import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.repository.PermissionRepository;
import in.trendsnag.user_management.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
@Order(1)
public class RoleDataInitializer implements CommandLineRunner {
    private final RoleRepository roles;
    private final PermissionRepository permissions;
    public RoleDataInitializer(RoleRepository roles, PermissionRepository permissions) {
        this.roles=roles; this.permissions=permissions;
    }
    @Override public void run(String... args) {
        Permission userRead=ensurePermission("USER_READ");
        Permission userWrite=ensurePermission("USER_WRITE");
        Permission adminRead=ensurePermission("ADMIN_READ");
        Permission adminWrite=ensurePermission("ADMIN_WRITE");
        ensureRole("USER",Set.of());
        ensureRole("ADMIN",Set.of(userRead,userWrite,adminRead,adminWrite));
    }
    private Permission ensurePermission(String name) {
        return permissions.findByName(name).orElseGet(() -> {
            Permission p=new Permission();
            p.setName(name);
            p.setDescription(name.replace('_',' '));
            return permissions.save(p);
        });
    }
    private void ensureRole(String name,Set<Permission> defaults) {
        if (roles.findByName(name).isPresent()) return;
        Role role=new Role();
        role.setName(name);
        role.setPermissions(new HashSet<>(defaults));
        roles.save(role);
    }
}
