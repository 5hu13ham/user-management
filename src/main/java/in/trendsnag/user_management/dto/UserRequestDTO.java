package in.trendsnag.user_management.dto;
import jakarta.validation.constraints.*;
public class UserRequestDTO {
    @NotBlank @Size(min=3,max=40) private String username;
    @NotBlank @Email private String email;
    @NotBlank private String firstName;
    private String lastName;
    @Pattern(regexp="^$|^[0-9]{10,15}$") private String phone;
    @Min(18) @Max(120) private Integer age;
    @Size(min=8,max=72) private String password;
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username=username;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email=email;}
    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName=firstName;}
    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName=lastName;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone=phone;}
    public Integer getAge() {return age;}
    public void setAge(Integer age) {this.age=age;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password=password;}
}
