package in.trendsnag.user_management.repository;
import in.trendsnag.user_management.model.User;
import org.springframework.data.domain.Page;
public interface UserRepositoryCustom {
    Page<User> FilterSearchSortUsers(String keyword, String role, Boolean active, Boolean deleted,
                                    String ageGroup, int page, int size, String sortBy, String sortDir);
}
