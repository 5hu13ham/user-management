package in.trendsnag.user_management.dto;
import java.time.LocalDateTime;
public class UserResponseDTO {
    private final String id, username, email, firstName, lastName, phone, role;
    private final Integer age;
    private final boolean active;
    private final LocalDateTime createdAt, updatedAt;
    public UserResponseDTO(String id, String username, String email, String firstName,
                           String lastName, String phone, Integer age, String role,
                           boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id=id; this.username=username; this.email=email; this.firstName=firstName;
        this.lastName=lastName; this.phone=phone; this.age=age; this.role=role;
        this.active=active; this.createdAt=createdAt; this.updatedAt=updatedAt;
    }
    public String getId() {return id;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getPhone() {return phone;}
    public Integer getAge() {return age;}
    public String getRole() {return role;}
    public boolean isActive() {return active;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
