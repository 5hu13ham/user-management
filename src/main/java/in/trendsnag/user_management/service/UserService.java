package in.trendsnag.user_management.service;

import in.trendsnag.user_management.dto.UserRequestDTO;
import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.RoleRepository;
import in.trendsnag.user_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users=users; this.roles=roles; this.encoder=encoder;
    }

    public User createUser(UserRequestDTO dto) {
        if (dto.getPassword()==null || dto.getPassword().isBlank())
            throw new IllegalArgumentException("Password is required for new users");
        ensureUnique(dto.getUsername(), dto.getEmail(), dto.getPhone(), null);
        Role role=roles.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default USER role is not initialized"));
        User user=new User();
        applyEditableFields(user,dto);
        user.setRole(role);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return users.save(user);
    }

    public User updateUser(String id, UserRequestDTO dto) {
        User existing=findUserById(id);
        ensureUnique(dto.getUsername(),dto.getEmail(),dto.getPhone(),id);
        applyEditableFields(existing,dto);
        if (dto.getPassword()!=null && !dto.getPassword().isBlank()) {
            existing.setPassword(encoder.encode(dto.getPassword()));
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return users.save(existing);
    }

    private void applyEditableFields(User user, UserRequestDTO dto) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone()==null || dto.getPhone().isBlank()?null:dto.getPhone());
        user.setAge(dto.getAge());
    }

    private void ensureUnique(String username, String email, String phone, String id) {
        if (users.findByUsername(username).filter(u -> !u.getId().equals(id)).isPresent())
            throw new IllegalArgumentException("Username already exists");
        if (users.findByEmail(email).filter(u -> !u.getId().equals(id)).isPresent())
            throw new IllegalArgumentException("Email already exists");
        if (phone!=null && !phone.isBlank()
                && users.findByPhone(phone).filter(u -> !u.getId().equals(id)).isPresent())
            throw new IllegalArgumentException("Phone number already exists");
    }

    public Page<User> list(int page, int size) {
        if (page<0 || size<1 || size>100) throw new IllegalArgumentException("Invalid page or size");
        return users.findByIsDeletedFalse(PageRequest.of(page,size,Sort.by("username").ascending()));
    }

    public Page<User> filter(String keyword, String role, Boolean active, Boolean deleted,
                             String ageGroup, int page, int size, String sortBy, String sortDir) {
        return users.FilterSearchSortUsers(keyword,role,active,deleted,ageGroup,page,size,sortBy,sortDir);
    }

    public User findUserById(String id) {
        return users.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public void softDeleteUser(String id) {
        User user=findUserById(id);
        user.setActive(false);
        user.setDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        users.save(user);
    }

    public User setActive(String id, boolean active) {
        User user=findUserById(id);
        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());
        return users.save(user);
    }
}
