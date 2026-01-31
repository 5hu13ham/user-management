package in.trendsnag.user_management.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.trendsnag.user_management.dto.UserLoginResponse;
import in.trendsnag.user_management.dto.UserRegisterRequest;
import in.trendsnag.user_management.model.Role;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.UserRepository;
import in.trendsnag.user_management.security.JwtUtil;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public RegisterService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserLoginResponse register(UserRegisterRequest request) 
    {
    	String identifier = request.getIdentifier();
    	
    	if (userExists(identifier))
    	{
    		throw new RuntimeException("User Already Exists");
    	}
    	
    	User user = new User();
    	
    	mapIdentifier(user, identifier);

    	user.setPassword(passwordEncoder.encode(request.getPassword()));
    	user.setRole(Role.USER);
    	user.setActive(true);
    	
    	userRepository.save(user);
    	
    	Authentication auth = new UsernamePasswordAuthenticationToken(
    			user,
    			null,
    			user.getAuthorities());
    	
        String token = jwtUtil.generateToken(auth);

        return new UserLoginResponse(token, user.getUsername(), user.getRole().name());
    }

	private boolean userExists(String identifier) 
	{
		// TODO Auto-generated method stub
		
		if (identifier.contains("@"))
		{
			if (userRepository.findByEmail(identifier)!=null)
				return true;
		return false;
		}
		
		else if (identifier.matches("\\d+"))
		{
			if (userRepository.findByPhone(identifier)!=null)
					return true;
			return false;
		}
		
		else
		{
			if (userRepository.findByUsername(identifier)!=null)
				return true;
			return false;
	
		}
	}
	
	private void mapIdentifier(User user, String identifier) {
        if (identifier.contains("@")) {
            user.setEmail(identifier);
            user.setUsername(identifier.split("@")[0]);
        } else if (identifier.matches("\\d+")) {
            user.setPhone(identifier);
            user.setUsername(identifier);
        } else {
            user.setUsername(identifier);
        }
    }
    
    
}
