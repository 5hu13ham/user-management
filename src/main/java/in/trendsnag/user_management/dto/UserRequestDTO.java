package in.trendsnag.user_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class UserRequestDTO {
    
@NotBlank(message = "First Name is not an optional field")
private String firstName;

private String lastName;

@Email(message = "Invalid email format")
@NotBlank(message = "Email is required")
private String email;

@NotBlank(message = "Password is required")
@Size(min = 6, message = "Password must be at least 6 characters")
private String password;

/**
@NotBlank(message = "Role is required")
@Size(min = 4, max = 10)
private String role;

TODO:
- Add JWT-based authentication
- Restrict role updates to ADMIN only
- Create separate AdminUserUpdateRoleDTO
- Protect endpoint using Spring Security (@PreAuthorize)

**/

@NotBlank(message = "Enter a unique username")
private String username;

@Pattern(regexp = "^\\d{10}$")
private String phone;

@Min(value = 18, message = "Age must be atleast 18")
@Max(value = 99, message = "Need proof of your age more than 99")
private Integer age;



public String getUsername() {
	return username;
}

public void setUsername(String username) {
	this.username = username;
}

public String getPhone() {
	return phone;
}

public void setPhone(String phone) {
	this.phone = phone;
}

public Integer getAge() {
	return age;
}

public void setAge(Integer age) {
	this.age = age;
}


public String getFirstName() {
	return firstName;
}

public void setFirstName(String firstName) {
	this.firstName = firstName;
}

public String getLastName() {
	return lastName;
}

public void setLastName(String lastName) {
	this.lastName = lastName;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public String getPassword() {
	return password;
}

public void setPassword(String password) {
	this.password = password;
}

/**public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}**/

}
