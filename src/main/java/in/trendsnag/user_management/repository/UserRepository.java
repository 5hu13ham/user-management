package in.trendsnag.user_management.repository;

import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom{
	
	List<User> findByRole(Role role);
	
	@Query("SELECT u FROM User u WHERE u.active = :active AND u.isDeleted = false")
	List<User> findByActive(@Param("active") boolean active);
	
	List<User> findByFirstNameContaining(String keyword);
	
	long countByRole(Role role);
	long countByActive(boolean active);
	
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Optional<User> findByPhone(String phone);
	
	@Query(value = "SELECT * FROM users WHERE is_deleted = false", nativeQuery = true)
	List<User> findAllActiveUsers();

	@Query(value = "SELECT * FROM users WHERE id = :id AND is_deleted = false", nativeQuery = true)
	Optional<User> findActiveUserById(@Param("id") Long id);

	Page<User> findByIsDeletedFalse(Pageable pageable);
	
	


}
