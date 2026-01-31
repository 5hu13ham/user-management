package in.trendsnag.user_management.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.UserRepository;

@Service
public class UserAuthService implements UserDetailsService {
	
	private final UserRepository userRepository;

	public UserAuthService(UserRepository userRepository) {
		
		this.userRepository = userRepository;

	}
	
	@Override
	public UserDetails loadUserByUsername(String indentifier) throws UsernameNotFoundException
	{
		User user = findByIdentifier(indentifier);
		return user;
	}

	public User findByIdentifier(String identifier) {
	    if (identifier.contains("@")) {
	        return userRepository.findByEmail(identifier)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	    } else if (identifier.matches("\\d+")) {
	        return userRepository.findByPhone(identifier)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	    } else {
	        return userRepository.findByUsername(identifier)
	            .orElseThrow(() -> new RuntimeException("User not found"));
	    }
	}
}
