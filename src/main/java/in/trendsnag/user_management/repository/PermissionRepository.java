package in.trendsnag.user_management.repository;
import in.trendsnag.user_management.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface PermissionRepository extends MongoRepository<Permission, String> {
    Optional<Permission> findByName(String name);
}
