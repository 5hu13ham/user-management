package in.trendsnag.user_management.repository;
import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByIdAndIsDeletedFalse(String id);
    Page<User> findByIsDeletedFalse(Pageable pageable);
    List<User> findByRole(Role role);
    List<User> findByActiveAndIsDeletedFalse(boolean active);
    List<User> findByFirstNameContainingIgnoreCase(String keyword);
    long countByRoleName(String roleName);
    long countByActive(boolean active);
}
