package in.trendsnag.user_management.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import in.trendsnag.user_management.dto.UserLoginRequest;
import in.trendsnag.user_management.dto.UserLoginResponse;
import in.trendsnag.user_management.model.Permission;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.security.JwtUtil;

@Service
public class LoginService {
	
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	
	public LoginService(AuthenticationManager authenticationManager,
			            JwtUtil jwtUtil) {
		
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	
	public UserLoginResponse login(UserLoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));

        // Step 3 → generate token
        String token = jwtUtil.generateToken(authentication);

        User user = (User) authentication.getPrincipal();
        // Step 4 → return response DTO

        
        Set<String> permissions =
        	    user.getRole().getPermissions()
        	        .stream()
        	        .map(Permission::getName)
        	        .collect(Collectors.toSet());

        	return new UserLoginResponse(
        	    token,
        	    user.getUsername(),
        	    user.getRole().getName(),
        	    permissions
        	);
  
	}
}
