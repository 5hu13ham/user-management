package in.trendsnag.user_management.repository;
import in.trendsnag.user_management.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
