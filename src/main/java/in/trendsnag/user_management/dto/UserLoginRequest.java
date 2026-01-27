package in.trendsnag.user_management.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {
	
	@NotBlank(message = "Email/Phone/username anything works")
	private String identifier;
	
	@NotBlank(message = "Hope you haven't forgotten it")
	private String password;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}
