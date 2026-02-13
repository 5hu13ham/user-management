package in.trendsnag.user_management.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;

import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;

@Repository
public interface RoleRepository extends JpaRepository<User, Long> {
	
	Role findByName(String name);

}
