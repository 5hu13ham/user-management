package in.trendsnag.user_management.dto;

import java.util.Set;

public class UserLoginResponse {
	
	private String token;
	private String username;

	private String role;
	private Set<String> permissions;
	
	public UserLoginResponse(String token, String username, String role, Set<String> permissions) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
	}
	
	
	
	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	

}
