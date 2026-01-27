package in.trendsnag.user_management.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import in.trendsnag.user_management.dto.UserLoginRequest;
import in.trendsnag.user_management.dto.UserLoginResponse;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.security.JwtUtil;

@Service
public class LoginService {
	
	private final UserAuthService userAuthService;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	public LoginService(UserAuthService userAuthService,
			            BCryptPasswordEncoder passwordEncoder,
			            JwtUtil jwtUtil) {
		
		this.userAuthService = userAuthService;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}
	
	public User login(UserLoginRequest loginRequest) {

        // Step 1 → get the user
        User user = userAuthService.findByIdentifier(loginRequest.getIdentifier());

        // Step 2 → verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Step 3 → generate token
        String token = jwtUtil.generateToken(loginRequest.getIdentifier());

        // Step 4 → return response DTO
        return user;
        
  
	}
}
