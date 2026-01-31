package in.trendsnag.user_management.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRegisterRequest {
	
	@NotBlank(message = "SignUp using a unique username, email, phonenumber" )
	private String identifier;
	
	@NotBlank(message = "Enter password")
	private String password;
	
	
	public String getIdentifier() {
		return identifier;
	}
	public String getPassword() {
		return password;
	}
	
	

}
