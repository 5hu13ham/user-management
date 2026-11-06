package in.trendsnag.user_management.repository;
import in.trendsnag.user_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private static final Set<String> SORT_FIELDS =
            Set.of("username", "email", "firstName", "lastName", "age", "createdAt", "role.name");
    private final MongoTemplate mongoTemplate;
    public UserRepositoryCustomImpl(MongoTemplate mongoTemplate) { this.mongoTemplate = mongoTemplate; }

    @Override
    public Page<User> FilterSearchSortUsers(String keyword, String role, Boolean active,
                                           Boolean deleted, String ageGroup, int page,
                                           int size, String sortBy, String sortDir) {
        if (page < 0 || size < 1 || size > 100)
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        if (!SORT_FIELDS.contains(sortBy))
            throw new IllegalArgumentException("Unsupported sort field: " + sortBy);
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc"))
            throw new IllegalArgumentException("sortDir must be asc or desc");
        List<Criteria> filters = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            if (keyword.length() > 100) throw new IllegalArgumentException("Search keyword is too long");
            String regex = ".*" + Pattern.quote(keyword.strip()) + ".*";
            filters.add(new Criteria().orOperator(
                    Criteria.where("username").regex(regex, "i"),
                    Criteria.where("email").regex(regex, "i"),
                    Criteria.where("firstName").regex(regex, "i"),
                    Criteria.where("lastName").regex(regex, "i")));
        }
        if (role != null && !role.isBlank()) filters.add(Criteria.where("role.name").is(role.toUpperCase()));
        if (active != null) filters.add(Criteria.where("active").is(active));
        filters.add(Criteria.where("isDeleted").is(deleted != null && deleted));
        if (ageGroup != null && !ageGroup.isBlank()) {
            switch (ageGroup.toLowerCase()) {
                case "teenagers" -> filters.add(Criteria.where("age").gte(13).lte(19));
                case "adults" -> filters.add(Criteria.where("age").gte(20).lte(59));
                case "elderly" -> filters.add(Criteria.where("age").gte(60));
                default -> throw new IllegalArgumentException("Unknown age group");
            }
        }
        Criteria combined = filters.size() == 1 ? filters.get(0)
                : new Criteria().andOperator(filters.toArray(new Criteria[0]));
        long count = mongoTemplate.count(new Query(combined), User.class);
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);
        List<User> users = mongoTemplate.find(new Query(combined).with(pageable), User.class);
        return new PageImpl<>(users, pageable, count);
    }
}
