package in.trendsnag.user_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.repository.UserRepository;

@Service
public class UserAuthService {
	
	private final UserRepository userRepository;

	public UserAuthService(UserRepository userRepository) {
		
		this.userRepository = userRepository;

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
